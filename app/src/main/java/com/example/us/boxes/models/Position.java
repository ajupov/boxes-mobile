package com.example.us.boxes.models;
import com.example.us.boxes.screens.GameScreen;
import java.util.Random;

public class Position {
    public int x;
    public int y;

    public Position(Position position){
        this.x = position.x;
        this.y = position.y;
    }
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public static Position getRandomPosition(){
        Position position = new Position(Math.abs(new Random().nextInt() % GameScreen.WORLD_WIDTH / Box.SIZE) * Box.SIZE, 0);
        boolean founded = false;
        while (position.y < GameScreen.WORLD_HEIGHT) {
            for (int j = 0; j < GameScreen.boxes.size; j++) {
                if (GameScreen.boxes.get(j).position.equals(position)) {
                    founded = true;
                    break;
                }
            }
            if (founded == true) {
                position.y += Box.SIZE;
                founded = false;
            } else {
                break;
            }
        }
        return position;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Position other = (Position) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }
}
