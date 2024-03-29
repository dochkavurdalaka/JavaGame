package com.example.server;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    static final int COUNT_GAMERS = 2;

    @Override
    public void start(Stage stage) throws IOException {



        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Pane root = (Pane)fxmlLoader.load();

        Rectangle rectangle = (Rectangle) root.getChildren().get(7);
        Line border = (Line)root.getChildren().get(4);
        double height = rectangle.getHeight();
        double width = rectangle.getWidth();
        for(int i = 0; i<COUNT_GAMERS; i++){
            Line arrow = new Line(rectangle.getWidth(), (i + 0.5) * height / COUNT_GAMERS, rectangle.getWidth() + 76.5, (i + 0.5) * height / COUNT_GAMERS);

            arrow.setId("arrow" + i);
            root.getChildren().add(arrow);

            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(new Double[]{
                    10.0, (i + 0.5) * height / COUNT_GAMERS - 20,
                    width-10, (i + 0.5) * height / COUNT_GAMERS,
                    10.0, (i + 0.5) * height / COUNT_GAMERS + 20 });
            polygon.setFill(Color.BLUE);
            polygon.setId("polygon" + i);
            root.getChildren().add(polygon);


            double x = border.getLayoutX() + border.getStartX();
            Text text1 = new Text(x + 10, 20 + 80 * i, "Игрок:");
            text1.setFont(new Font(15));
            text1.setId("gamer_name" + i);

            Text text2 = new Text(x + 10, 45 + 80 * i, "Счет игрока:");
            text2.setFont(new Font(15));
            text2.setId("hit_count" + i);

            Text text3 = new Text(x + 10, 70 + 80 * i, "Выстрелов:");
            text3.setFont(new Font(15));
            text3.setId("arrow_count" + i);

            root.getChildren().add(text1);
            root.getChildren().add(text2);
            root.getChildren().add(text3);


        }


        Scene scene = new Scene(root);


        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }





    public static void main(String[] args) {
        launch();



    }
}