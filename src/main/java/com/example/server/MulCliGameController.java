package com.example.server;


import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Math.*;

public class MulCliGameController {

    @FXML
    public Rectangle rectangle;

    @FXML
    public Text gamer_id;

    @FXML
    private Circle circleBig;

    @FXML
    private Circle circleSmall;

    @FXML
    private Line line;

    private Line[] arrows;

    private Text[] gamer_names;
    private Text[] hit_counts;
    private Text[] arrow_counts;

    @FXML
    private Line border;

    @FXML
    private Pane pane;




    boolean start_game = false;
    boolean arrow_shot_trigger = false;
    boolean pause_trigger = false;


    Socket cs;
    int port = 3124;
    InetAddress ip = null;
    BufferedReader dis;
    BufferedWriter dos;

    private InformationGamers createInformation(int COUNT_GAMERS){

        InformationGamers info = new InformationGamers(COUNT_GAMERS);


        for(int i = 0; i < COUNT_GAMERS; i++){
            info.arrow_shots[i] = false;
            info.arrow_counts[i] = 0;
            info.hit_counts[i] = 0;
            info.arrows[i].x_end = arrows[i].getEndX() + arrows[i].getLayoutX();
            info.arrows[i].x_start = arrows[i].getStartX() + arrows[i].getLayoutX();
            info.arrows[i].y_end = arrows[i].getEndY() + arrows[i].getLayoutY();
            info.arrows[i].y_start = arrows[i].getStartY() + arrows[i].getLayoutY();
        }

        info.line.x_end = line.getEndX() + line.getLayoutX();
        info.line.x_start = line.getStartX() + line.getLayoutX();
        info.line.y_end = line.getEndY() + line.getLayoutY();
        info.line.y_start = line.getStartY() + line.getLayoutY();

        info.border.x_end = border.getEndX() + border.getLayoutX();
        info.border.x_start = border.getStartX() + border.getLayoutX();
        info.border.y_end = border.getEndY() + border.getLayoutY();
        info.border.y_start = border.getStartY() + border.getLayoutY();

        info.circleBig.x = circleBig.getCenterX() + circleBig.getLayoutX();
        info.circleBig.y = circleBig.getCenterY() + circleBig.getLayoutY();
        info.circleBig.R = circleBig.getRadius();

        info.circleSmall.x = circleSmall.getCenterX() + circleSmall.getLayoutX();
        info.circleSmall.y = circleSmall.getCenterY() + circleSmall.getLayoutY();
        info.circleSmall.R = circleSmall.getRadius();

        info.rectangle.width = rectangle.getWidth() + rectangle.getLayoutX();
        info.rectangle.height = rectangle.getHeight() + rectangle.getLayoutY();


        return info;

    }

    Polygon pol;

    @FXML
    protected void onStartButtonClick() {
        if(start_game == true) return;

        start_game = true;
        int COUNT_GAMERS = HelloApplication.COUNT_GAMERS;

        arrows = new Line[COUNT_GAMERS];
        gamer_names = new Text[COUNT_GAMERS];
        hit_counts = new Text[COUNT_GAMERS];
        arrow_counts = new Text[COUNT_GAMERS];

        for(int i = 0; i < COUNT_GAMERS; i++){
            arrows[i] = (Line)pane.getScene().lookup("#arrow"+i);
            arrows[i].setStartX(rectangle.getWidth() + rectangle.getLayoutX() - arrows[i].getLayoutX());
            arrows[i].setEndX(rectangle.getWidth() + rectangle.getLayoutX() + 76.5 - arrows[i].getLayoutX());

            gamer_names[i] = (Text)pane.getScene().lookup("#gamer_name"+i);
            hit_counts[i] = (Text)pane.getScene().lookup("#hit_count"+i);
            arrow_counts[i] = (Text)pane.getScene().lookup("#arrow_count"+i);
        }

        //hit_counts[0].setText("jun seba");


        InformationGamers info = createInformation(COUNT_GAMERS);
        Gson gson = new Gson();
        String info_json = gson.toJson(info);

        try {
            ip = InetAddress.getLocalHost();
            cs = new Socket(ip, port);

            dis = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            dos = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream()));

            int client_num = Integer.parseInt(dis.readLine());
            System.out.println("Client connected with num: " + client_num);
            System.out.println("your nickname: Gamer_" + client_num);

            pol = (Polygon) pane.getScene().lookup("#polygon"+client_num);
            Platform.runLater(()->{
                pol.setFill(Color.RED);
                gamer_id.setText("Gamer_"+client_num);
            });

            if(client_num == 0){
                dos.write(info_json + "\n");
                dos.flush();

            }
            send_and_receive(client_num, COUNT_GAMERS);

        } catch (IOException ex) {
            System.out.println("Error");
        }

    }



    @FXML
    protected void onPauseButtonClick(){
        pause_trigger = true;

    }




    Thread sendandreceive;
    protected void send_and_receive(int client_num,int COUNT_GAMERS) {

        if (sendandreceive != null) return;


        class SendReceive implements Runnable {
            MulCliGameController hc;
            public SendReceive(MulCliGameController hc)
            {
                this.hc = hc;
            }


            @Override
            public void run() {

                System.out.append("Game client start!!!\n");
                Gson gson = new Gson();

                try {
                    while (true) {

                        Thread.sleep(30);
                        String info_json = dis.readLine();
                        InformationGamers info = gson.fromJson(info_json, InformationGamers.class);

                        if(info.gameContinuing == false) {
                            Platform.runLater(
                                    () -> {

                                        circleBig.setCenterY(info.circleBig.y - circleBig.getLayoutY());
                                        circleSmall.setCenterY(info.circleSmall.y - circleSmall.getLayoutY());

                                        for(int i = 0; i < COUNT_GAMERS; i++){
                                            arrows[i].setEndX(info.arrows[i].x_end - arrows[i].getLayoutX());
                                            arrows[i].setStartX(info.arrows[i].x_start - arrows[i].getLayoutX());
                                            hit_counts[i].setText("Счет игрока: " + info.hit_counts[i]);
                                            arrow_counts[i].setText("Выстрелов: " + info.arrow_counts[i]);
                                            gamer_names[i].setText("Игрок: Gamer_" + i);
                                        }

                                    });
                            break;
                        }

                        InformationGamers.GamerOut gamer_out = new InformationGamers.GamerOut();

                        if(arrow_shot_trigger == true) {
                            gamer_out.arrow_shot_trigger = true;
                            arrow_shot_trigger = false;
                        }

                        if(pause_trigger == true) {
                            gamer_out.pause_trigger = true;
                            pause_trigger = false;
                        }


                        String gamer_out_json = gson.toJson(gamer_out);
                        dos.write(gamer_out_json + "\n");
                        dos.flush();


                        Platform.runLater(
                                () -> {

                                    circleBig.setCenterY(info.circleBig.y - circleBig.getLayoutY());
                                    circleSmall.setCenterY(info.circleSmall.y - circleSmall.getLayoutY());

                                    for(int i = 0; i < COUNT_GAMERS; i++){
                                        arrows[i].setEndX(info.arrows[i].x_end - arrows[i].getLayoutX());
                                        arrows[i].setStartX(info.arrows[i].x_start - arrows[i].getLayoutX());
                                        hit_counts[i].setText("Счет игрока: " + info.hit_counts[i]);
                                        arrow_counts[i].setText("Выстрелов: " + info.arrow_counts[i]);
                                        gamer_names[i].setText("Игрок: Gamer_" + i);
                                    }
                                });

                    }

                } catch (IOException | InterruptedException ex) {
                    System.out.println("Error");
                }finally{
                    System.out.println("Client disconnected");
                    Platform.runLater(()->{pol.setFill(Color.BLUE);});
                    start_game = false;
                    sendandreceive = null;
                    try {
                        cs.close();
                        dis.close();
                        dos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        sendandreceive = new Thread(new SendReceive(this));
        sendandreceive.start();
    }



    @FXML
    protected void onShotButtonClick() {
        arrow_shot_trigger = true;

    }


}
