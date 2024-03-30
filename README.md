# JavaGame
Simple Java multiplayer game created, using JavaFX.
Class MulThrGameServer implements a multi-client server, to which clients connect.
Class HelloApplication implements a client, which can connect to server.
Class MulCliGameController implements a handler, which update graphic form, used by client.
hello-view.fxml describes this graphic form on FXML language

First, server starts working. After that, clients connect to him. 
Client should run the application: 
![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/ed313a2d-412b-47b6-a79b-2da0e97d1df6)

Clients should push start game button to start playing. After that, client connects to server and his position in the graphic form becomes marked by red triangle and form shows his gamer id: 
![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/e9c6b8ac-1b3f-4dea-a4a8-9a6f627a38e7)

After all clients connected, circles start to moving and client can shot an arrow or pause game:
![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/b26ac3d8-a26c-47c5-8b36-aea183ab1245)
![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/eca5fc01-6aad-4d77-bdf4-7cd98f384b94)
![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/ae60417e-0d7e-42a2-9ab9-67804b5f0c74)

If player hits with his arrow blue circle,then he gets one point, if he hits yellow circle, he gets 2 points. Yellow circle moves 2x faster then blue.
As soon as any player gets six or more points, he becomes winner in the game and the game stops. Server print to console the name of the winner, and all clients automatically disconnect from server.

![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/718fc82f-259c-4aaf-ac62-2ea14d3b5cc9)

![image](https://github.com/dochkavurdalaka/JavaGame/assets/30550066/f98f35e6-8d3f-44d9-92fb-0076dc730b1d)

