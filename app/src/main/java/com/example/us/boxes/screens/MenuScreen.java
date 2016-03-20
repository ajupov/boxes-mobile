package com.example.us.boxes.screens;
import android.util.Log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.example.us.boxes.gamecycle.MyGame;

public class MenuScreen implements Screen, InputProcessor {

    MyGame game;
    public MenuScreen(MyGame game) {
        this.game = game;
    }

    private static SpriteBatch                      batch;              //Упаковщик спрайтов
    private static Texture                          background;         //Текстура фона
    private static Texture                          changeBox;         //Текстура фона
    private static BitmapFont                       font;               //Шрифт
    private static int      screenWidth;        //Размер экрана по горизонтали
    private static int      screenHeight;       //Размер экрана по вертикали
    private static float    PPU_X;              //Относительный размер 1 точки по горизонтали
    private static float    PPU_Y;              //Относительный размер 1 точки по вертикали

    //Идентификаторы касания
    private static int      boxPositionY;
    private static boolean  up;
    public static boolean   paused;
    private static String[] titles;

    //Методы события окна
    @Override public void show(){
        batch = new SpriteBatch();
        boxPositionY = 0;
        up = false;
        titles = new String[]{"New\ngame", "Continue", "Instruction", "Exit"};
        font = getFont(Color.BLACK, 22);
        background = new Texture(Gdx.files.internal("menu/background_menu.jpg"));
        changeBox = new Texture(Gdx.files.internal("menu/box.png"));
        Gdx.input.setInputProcessor(this);  //Установка контроллера
        Gdx.input.setCatchBackKey(true);    //Устанока кнопки возврата
    }
    @Override public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        PPU_X = (width / 10f);
        PPU_Y = (height / 10f)*1.6f;
    }
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    @Override public void pause() {
    }
    @Override public void resume() {
    }
    @Override public void dispose() {
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCatchBackKey(false);
        batch.dispose();
        background.dispose();
        changeBox.dispose();
        font.dispose();
    }
    @Override public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0, screenWidth, screenHeight);
        for (int i = 0; i < 4; ++i) {
            batch.draw(changeBox, PPU_X / 4 + i * screenWidth / 4, PPU_Y + getVerticalPosition(i), PPU_X * 2, PPU_Y * 6);
            font.drawMultiLine(batch, titles[i], PPU_X / 4 + i * screenWidth / 4 + PPU_X, PPU_Y + getVerticalPosition(i) + PPU_Y / 8 + PPU_Y, 0, BitmapFont.HAlignment.CENTER);
        }

        if (up) {
            boxPositionY++;
            if (boxPositionY > PPU_Y)
                up = false;
        } else {
            boxPositionY--;
            if (boxPositionY < 0)
                up = true;
        }

        //sleep(10);
        batch.end();
    }

    //Методы событий клавиш и мыши
    @Override public boolean mouseMoved(int x, int y) {
        return true;
    }
    @Override public boolean keyTyped(char character) {
        return false;
    }
    @Override public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            Gdx.app.exit();
        }
        return true;
    }
    @Override public boolean keyUp(int keycode) {
        return true;
    }
    @Override public boolean scrolled(int amount) {
        return false;
    }

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }
        if(pointer == 0) {
            if(x < screenWidth / 4) {
                if((up == false && y > screenHeight * 0.75f) || (up == true && y <  screenHeight * 0.75f && y > screenHeight* 0.5f))
                game.setScreen(game.gameScreen);
                Log.e("Усману", y + " : " + boxPositionY);
            }
        }
        return true;
    }
    @Override public boolean touchUp(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }
        return true;
    }
    @Override public boolean touchDragged(int x, int y, int pointer) {
        return true;
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private float getVerticalPosition(int i) {
        return ((i % 2 == 0) ? (- PPU_Y / 2 + boxPositionY) : (PPU_Y / 2) - boxPositionY);
    }
    private static BitmapFont getFont(Color color, int size) {
        BitmapFont font;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("trixie.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
