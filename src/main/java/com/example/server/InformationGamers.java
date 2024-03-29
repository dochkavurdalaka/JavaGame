package com.example.server;

public class InformationGamers {

    int COUNT_GAMERS;
    InformationGamers(int COUNT_GAMERS){
        this.COUNT_GAMERS = COUNT_GAMERS;
        arrow_shots = new boolean[COUNT_GAMERS];
        arrow_counts = new int[COUNT_GAMERS];
        hit_counts = new int[COUNT_GAMERS];
        arrows = new InformationGamers.Line[COUNT_GAMERS];
        for(int i = 0; i < COUNT_GAMERS; i++){
            arrows[i] = new InformationGamers.Line();
        }
    }

    public class Line{
        double x_start;
        double y_start;
        double x_end;
        double y_end;
    }

    public class Circle{
        double x;
        double y;
        double R;
    }

    public class Rectangle{
        double width;
        double height;
    }

    static class GamerOut{
        boolean arrow_shot_trigger;
        boolean pause_trigger;
    }

    boolean[] arrow_shots;



    int[] arrow_counts;
    int[] hit_counts;


    InformationGamers.Line[] arrows;
    InformationGamers.Line line = new InformationGamers.Line();
    InformationGamers.Line border = new InformationGamers.Line();


    InformationGamers.Circle circleBig = new InformationGamers.Circle();
    InformationGamers.Circle circleSmall = new InformationGamers.Circle();


    InformationGamers.Rectangle rectangle = new InformationGamers.Rectangle();

    boolean gameContinuing = true;

}
