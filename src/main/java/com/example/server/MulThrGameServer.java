package com.example.server;

import com.google.gson.Gson;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Math.*;

public class MulThrGameServer {
    static final int COUNT_GAMERS = 2;

    InformationGamers info;

    Thread movingCircles;

    Thread[] sendandreceive;

    boolean threadPause = false;

    boolean gameContinuing = true;

    protected void MainWork() {

        if (movingCircles != null) return;

        class CirclesMove implements Runnable {
            MulThrGameServer hc;

            public CirclesMove(MulThrGameServer hc) {
                this.hc = hc;
            }

            boolean CheckCircleArrow(InformationGamers.Circle circle, InformationGamers.Line arrow) {
                double x = arrow.x_end;
                double y = arrow.y_end;
                double x_0 = circle.x;
                double y_0 = circle.y;
                double R = circle.R;
                return !((x - x_0) * (x - x_0) + (y - y_0) * (y - y_0) <= R * R);
            }


            @Override
            public void run() {
                InformationGamers.Line line = info.line;
                double min = min(line.y_end, line.y_start);
                double max = max(line.y_end, line.y_start);

                max = max - min;
                min = 0;

                double length = abs(info.border.x_end);

                double yB = info.circleBig.y;
                double yS = info.circleSmall.y;

                int signB = 1;
                int signS = -1;


                InformationGamers.Line[] arrows = info.arrows;
                InformationGamers.Rectangle rectangle = info.rectangle;

                for (int i = 0; i < COUNT_GAMERS; i++) {
                    arrows[i].x_start = rectangle.width;
                    arrows[i].x_end = rectangle.width + 76;
                }


                InformationGamers.Circle circleBig = info.circleBig;
                InformationGamers.Circle circleSmall = info.circleSmall;

                boolean isRun = true;
                while (isRun) {

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    if (threadPause == true) {
                        synchronized (hc) {
                            try {
                                hc.wait();
                                threadPause = false;
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }

                    yB += signB * 2;

                    if (yB > max - circleBig.R || yB < min + circleBig.R) {
                        signB = -signB;
                    }
                    yS += signS * 4;
                    if (yS > max - circleSmall.R || yS < min + circleSmall.R) {
                        signS = -signS;
                    }
                    circleBig.y = yB;
                    circleSmall.y = yS;


                    for (int i = 0; i < COUNT_GAMERS; i++) {
                        if (info.arrow_shots[i] == true) {
                            InformationGamers.Line arrow = arrows[i];

                            arrow.x_end += 6;
                            arrow.x_start += 6;

                            if (arrow.x_end >= length) {
                                info.arrow_shots[i] = false;
                                arrow.x_end = rectangle.width + 76;
                                arrow.x_start = rectangle.width;


                            } else if (!CheckCircleArrow(circleBig, arrow)) {
                                info.arrow_shots[i] = false;
                                arrow.x_end = rectangle.width + 76;
                                arrow.x_start = rectangle.width;
                                info.hit_counts[i] += 1;


                            } else if (!CheckCircleArrow(circleSmall, arrow)) {
                                info.arrow_shots[i] = false;
                                arrow.x_end = rectangle.width + 76;
                                arrow.x_start = rectangle.width;
                                info.hit_counts[i] += 2;
                            }

                        }
                    }

                    for (int i = 0; i < COUNT_GAMERS; i++){
                        if(info.hit_counts[i] >= 6){
                            isRun = false;
                            gameContinuing = false;
                            System.out.println("The winner is Gamer_" + i + " !!!");
                            System.out.println("Awards !!!");
                            movingCircles = null;
                        }
                    }


                }
            }
        }

        movingCircles = new Thread(new CirclesMove(this));
        movingCircles.start();


    }


    int port = 3124;
    InetAddress ip = null;
    ServerSocket ss;


    protected void send_and_receive(Socket cs, int thread_num) {

        class SendReceive implements Runnable {
            MulThrGameServer hc;
            Socket cs;
            int thread_num;

            final Object lock = new Object();
            final Object lock2 = new Object();

            public SendReceive(MulThrGameServer hc, Socket cs, int thread_num) {
                this.hc = hc;
                this.cs = cs;
                this.thread_num = thread_num;
            }


            @Override
            public void run() {

                Gson gson = new Gson();
                BufferedReader dis = null;
                BufferedWriter dos = null;

                try {
                    dis = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                    dos = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream()));


                    dos.write(thread_num + "\n"); // отправляем сообщение на клиент
                    dos.flush();

                    if (thread_num == 0) {
                        String info_str = dis.readLine();
                        info = gson.fromJson(info_str, InformationGamers.class);
                    }


                    while (true) {
                        Thread.sleep(30);
                        String info_json = gson.toJson(info);

                        if(gameContinuing == false){
                            synchronized(lock2){
                                info.gameContinuing = false;
                                String temp = gson.toJson(info);
                                dos.write(temp + "\n");
                                dos.flush();
                                break;
                            }
                        }
                        else{
                            dos.write(info_json + "\n");
                            dos.flush();
                        }

                        String game_out_json = dis.readLine();
                        InformationGamers.GamerOut game_out = gson.fromJson(game_out_json, InformationGamers.GamerOut.class);

                        if (game_out.arrow_shot_trigger == true && info.arrow_shots[thread_num] == false) {
                            info.arrow_shots[thread_num] = true;
                            info.arrow_counts[thread_num]++;
                        }

                        if (game_out.pause_trigger == true) {
                            synchronized (lock) {
                                if (threadPause == false) {
                                    threadPause = true;
                                } else {
                                    synchronized (hc) {
                                        hc.notifyAll();
                                        threadPause = false;
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }finally {

                    System.out.println("Client " + thread_num + " disconnected");

                    try {
                        if (dos != null) {
                            dos.close();
                        }
                        if (dis != null) {
                            dis.close();
                            cs.close();
                        }

                        if(thread_num == 0){
                            new MulThrGameServer().StartServer();
                        }

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        sendandreceive[thread_num] = new Thread(new SendReceive(this, cs, thread_num));
        sendandreceive[thread_num].start();
    }


    public void StartServer() {
            sendandreceive = new Thread[COUNT_GAMERS];
            gameContinuing = true;

            System.out.append("Game server start!!!\n");
            try {
                ip = InetAddress.getLocalHost();
                ss = new ServerSocket(port, 0, ip);

                for (int i = 0; i < COUNT_GAMERS; i++) {
                    Socket cs = ss.accept();
                    System.out.println("Client connect (" + cs.getPort() + ")");
                    System.out.println("New client connected: " + i + "\n");

                    send_and_receive(cs, i);

                }
                System.out.println("All clients are connected!");
                MainWork();

            } catch (IOException ex) {
                System.out.println("Error");
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                        System.out.println("Server is closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    }


    public static void main(String[] args) {
        new MulThrGameServer().StartServer();
    }

}
