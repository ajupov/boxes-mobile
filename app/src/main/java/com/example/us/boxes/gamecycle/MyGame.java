package com.example.us.boxes.gamecycle;
import com.badlogic.gdx.Game;
import com.example.us.boxes.screens.GameScreen;
import com.example.us.boxes.screens.MenuScreen;

public class MyGame extends Game {
    public GameScreen gameScreen;
    public MenuScreen menuScreen;


    @Override
    public void create(){
        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
    }

//    @Override
//    public void resize(int width, int height){
//
//    }
//
//    @Override
//    public void render(){
//
//    }
//
    @Override
    public void pause(){
        if(gameScreen.player != null) {
            //gameScreen.player.write();
        }
    }
//
//    @Override
//    public void resume(){
//
//    }
//
    @Override
    public void dispose(){

    }
}
