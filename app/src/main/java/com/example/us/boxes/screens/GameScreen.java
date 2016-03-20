package com.example.us.boxes.screens;
import android.util.Log;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.example.us.boxes.gamecycle.MyGame;
import com.example.us.boxes.models.Box;
import com.example.us.boxes.models.Player;
import com.example.us.boxes.models.Position;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;

public class GameScreen implements Screen, InputProcessor {
    //Игра
    MyGame game;
    public GameScreen(MyGame game) {
        this.game = game;
    }

    //Объекты мира
    public static Player        player;     //Игрок
    public static Array<Box>    boxes;      //Массив ящиков
    public static Integer       score;      //Очки
    public static Integer       gameSpeed;  //Скорость игры
    public static boolean       gameEnd;    //Признак конца игры
    public static Integer       fps;        //Кадров в секунду
    public static int           gameTimer;
    public static int           boxTimer;
    public static boolean       paused;

    //Переменные конфигурации мира
    public static final int WORLD_WIDTH = 160;  //Ширина мира
    public static final int WORLD_HEIGHT = 60;  //Высота мира
    private static float    PPU_X;              //Относительный размер 1 точки по горизонтали
    private static float    PPU_Y;              //Относительный размер 1 точки по вертикали
    private static int      screenWidth;        //Размер экрана по горизонтали
    private static int      screenHeight;       //Размер экрана по вертикали
    private static int      leftFrame;          //Нижняя граница в картинке
    private static int      downFrame;          //Левая граница в картинке

    //Идентификаторы касания
    private static boolean  touched;    //Признак касания
    private static int      xBegin;     //Координата x
    private static int      yBegin;     //Координата y

    //Спрайты и анимация
    public static HashMap<String, TextureRegion>    boxSprites;         //Текстуры ящиков
    Array<Box.BoxType> boxTypes;
    public static Animation                         craschAnimation;    //Анимация уничтожения ящика
    private static Texture                          background;         //Текстура фона
    private static BitmapFont                       font;               //Шрифт
    private static SpriteBatch                      batch;              //Упаковщик спрайтов
    private static Random                           rand;               //ДПЧ

    //Методы события окна
    @Override public void show(){
        score = 0;
        gameTimer = 0;
        boxTimer = 0;
        gameSpeed = 10;
        gameEnd = false;
        touched = false;
        paused = false;

        loadTextures();
        createWorld();

        //createWorld_temp();                 //Создание мира
        Gdx.input.setInputProcessor(this);  //Установка контроллера
        Gdx.input.setCatchBackKey(true);    //Устанока кнопки возврата
    }
    @Override public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        PPU_X = width / 171f;
        PPU_Y = height / 110f;
        leftFrame = width / 32;
        downFrame = height / 20;
    }
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);

        paused = true;
    }
    @Override public void pause() {
        paused = true;
    }
    @Override public void resume() {
        paused = false;
    }
    @Override public void dispose() {
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCatchBackKey(false);
        batch.dispose();
    }
    @Override public void render(float delta) {
        if(paused == false) {
            fps = ((Float) (1 / delta)).intValue();
            int hBoxesCount = 0;
            Box box;

            batch.begin();
            //Фон
            batch.draw(background, 0, 0, screenWidth, screenHeight);

            //Ящики
            for (int i = 0; i < boxes.size; i++) {
                box = boxes.get(i);
                batch.draw(box.getFrame(), box.position.x * PPU_X + leftFrame, box.position.y * PPU_Y + downFrame, PPU_X * Box.SIZE, PPU_Y * Box.SIZE);
                if (box.contacting(i) == false) {
                    i--;
                    continue;
                }
                if (box.position.y == 0) {
                    hBoxesCount++;
                }
                box.moving();
                box.simpleFounding();
            }

            //Игрока
            player.contacting(hBoxesCount);
            player.moving();
            player.artefactTimerTick();
            batch.draw(player.getFrame(), player.position.x * PPU_X + leftFrame, player.position.y * PPU_Y + downFrame, PPU_X * Box.SIZE, PPU_Y * Box.SIZE * 2);

            if (gameEnd == false) {
                //Артефакт
                batch.draw(boxSprites.get(player.artefact.name()), 0, screenHeight - PPU_Y * 10, PPU_X * 10, PPU_Y * 10);
                gameTimer = 0;
                boxTimer++;

                if (boxTimer > (Math.abs(rand.nextInt() % 100) + 100)) {
                    boxTimer = 0;
                    Position position = new Position(Math.abs(rand.nextInt() % (WORLD_WIDTH / Box.SIZE)) * Box.SIZE, Math.abs(rand.nextInt() % 100) + 3 * WORLD_HEIGHT);
                    boxes.add(new Box(boxTypes.get(Math.abs(rand.nextInt() % boxTypes.size)), Box.BoxState.FLYING, position));
                }

            } else {
                gameTimer++;
                if (gameTimer < Box.SIZE * 5) {
                    Gdx.input.vibrate(gameSpeed);
                    font.setScale(0.25f + gameTimer * 0.02f, -0.35f * gameTimer * 0.02f);
                } else {

                }
                font.drawMultiLine(batch, "GAME\nOVER", screenWidth * 0.5f, screenHeight * 0.5f, 2f, BitmapFont.HAlignment.CENTER);
            }
            batch.end();

            sleep();
        }
    }

    //Методы событий клавиш и мыши
    @Override public boolean mouseMoved(int x, int y) {
        return true;
    }
    @Override public boolean keyTyped(char character) {
        return false;
    }
    @Override public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            this.hide();
        }
        return true;
    }
    @Override public boolean keyUp(int keycode) {
        //if(keycode == Keys.BACK ){
        //this.dispose();
        //gameScreen.setScreen(gameScreen.intro);
        //}
        return true;
    }
    @Override public boolean scrolled(int amount) {
        return false;
    }

    //Методы событий тачскрина
    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }
        if(pointer == 0) {
            xBegin = x;
            yBegin = y;
            touched = true;
        }
        return true;
    }
    @Override public boolean touchUp(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }

        if (touched && pointer == 0 && player.state == Player.PlayerState.GROUNDED && gameEnd == false) {
            touched = false;
            if(Math.abs(xBegin - x) < screenWidth / 20 && yBegin - y > screenHeight / 6){
                Log.e("Усману", "ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED);
                xBegin = x;
                yBegin = y;
                return true;
            }
            if(Math.abs(xBegin - x) < screenWidth / 20 && yBegin - y < -screenHeight / 6){
                touched = false;
                Log.e("Усману", "ВНИЗ, " + player.position.x + " " + player.position.y);
                player.artefactApply();
                xBegin = x;
                yBegin = y;
                return true;
            }
            if(xBegin - x > screenWidth / 11 && yBegin - y > screenHeight / 7){
                Log.e("Усману", "ВЛЕВО-ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED_LEFT_LOW);
                xBegin = x;
                yBegin = y;
                return true;
            }
            if(xBegin - x < -screenWidth / 11 && yBegin - y > screenHeight / 7){
                Log.e("Усману", "ВПРАВО-ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED_RIGHT_LOW);
                xBegin = x;
                yBegin = y;
                return true;
            }
        }
        return true;
    }
    @Override public boolean touchDragged(int x, int y, int pointer) {
        if (player.state == Player.PlayerState.GROUNDED && touched && pointer == 0 && gameEnd == false) {
            if(xBegin - x > screenWidth / 8 && Math.abs(yBegin - y) < screenHeight / 10){
                Log.e("Усману", "ВЛЕВО");
                player.setDirection(Player.PlayerState.STEPS_LEFT);
                xBegin = x;
                yBegin = y;
            }
            if(xBegin - x < -screenWidth / 8 && Math.abs(yBegin - y) < screenHeight / 10){
                Log.e("Усману", "ВПРАВО");
                player.setDirection(Player.PlayerState.STEPS_RIGHT);
                xBegin = x;
                yBegin = y;
            }
            if(Math.abs(xBegin - x) < screenWidth / 20 && yBegin - y > screenHeight / 2){
                Log.e("Усману", "ВЫСОКО-ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED_HIGH);
                xBegin = x;
                yBegin = y;
            }
            if(xBegin - x > screenWidth / 5 && yBegin - y > screenHeight / 3){
                Log.e("Усману", "ВЫСОКО-ВЛЕВО-ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED_LEFT_HIGH);
                xBegin = x;
                yBegin = y;
            }
            if(xBegin - x < -screenWidth / 5 && yBegin - y > screenHeight / 3){
                Log.e("Усману", "ВЫСОКО-ВПРАВО-ВВЕРХ");
                player.setDirection(Player.PlayerState.JUMPED_RIGHT_HIGH);
                xBegin = x;
                yBegin = y;
            }
        }
        return true;
    }

    private void createWorld(){

        for (int i = 0; i < 2; i++) {
            boxTypes.add(Box.BoxType.BOOMB_RED);
            boxTypes.add(Box.BoxType.BOOMB_GREEN);
            boxTypes.add(Box.BoxType.BOOMB_BLUE);
            boxTypes.add(Box.BoxType.BOOMB_YELLOW);
            boxTypes.add(Box.BoxType.BOOMB_BLACK);

            for (int j = 0; j < 5; j++) {
                boxTypes.add(Box.BoxType.BOX_BLACK);
            }
            for (int j = 0; j < 10; j++) {
                boxTypes.add(Box.BoxType.BOX_RED);
                boxTypes.add(Box.BoxType.BOX_GREEN);
                boxTypes.add(Box.BoxType.BOX_BLUE);
                boxTypes.add(Box.BoxType.BOX_YELLOW);
            }
        }
        boxTypes.add(Box.BoxType.HUMMER);
        boxTypes.add(Box.BoxType.MAGNIT);
        boxTypes.add(Box.BoxType.POWER);
        boxTypes.add(Box.BoxType.JUMP);
        boxTypes.add(Box.BoxType.TIMER);
        boxTypes.add(Box.BoxType.LIVE);

        int count = Math.abs(rand.nextInt() % 5) + 100;
        for (int i = 0; i < count; i++) {
            Position position = Position.getRandomPosition();
            if (position.y < GameScreen.WORLD_HEIGHT) {
                GameScreen.boxes.add(new Box(boxTypes.get(Math.abs(rand.nextInt() % boxTypes.size)), Box.BoxState.FLYING, position));
            }
        }
        player = new Player(Position.getRandomPosition(), Player.PlayerType.STRONG);
    }
    private void loadTextures(){
        boxes = new Array<Box>();
        boxTypes = new Array<Box.BoxType>();
        batch = new SpriteBatch();
        boxSprites = new HashMap<String, TextureRegion>();
        rand = new Random();

        background = new Texture(Gdx.files.internal("game/background_game.jpg"));
        Texture tempTexture = new Texture(Gdx.files.internal("game/box_textures.png"));
        TextureRegion tempTextureRegion[][] = TextureRegion.split(tempTexture, tempTexture.getWidth() / 4, tempTexture.getHeight() / 5);
        boxSprites.put(Box.BoxType.BOX_RED.name(), tempTextureRegion[0][0]);
        boxSprites.put(Box.BoxType.BOX_GREEN.name(), tempTextureRegion[0][1]);
        boxSprites.put(Box.BoxType.BOX_BLUE.name(), tempTextureRegion[0][2]);
        boxSprites.put(Box.BoxType.BOX_YELLOW.name(), tempTextureRegion[0][3]);
        boxSprites.put(Box.BoxType.BOX_BLACK.name(), tempTextureRegion[1][0]);
        boxSprites.put(Box.BoxType.BOOMB_RED.name(), tempTextureRegion[1][1]);
        boxSprites.put(Box.BoxType.BOOMB_GREEN.name(), tempTextureRegion[1][2]);
        boxSprites.put(Box.BoxType.BOOMB_BLUE.name(), tempTextureRegion[1][3]);
        boxSprites.put(Box.BoxType.BOOMB_YELLOW.name(), tempTextureRegion[2][0]);
        boxSprites.put(Box.BoxType.BOOMB_BLACK.name(), tempTextureRegion[2][1]);
        boxSprites.put(Box.BoxType.HUMMER.name(), tempTextureRegion[2][2]);
        boxSprites.put(Box.BoxType.MAGNIT.name(), tempTextureRegion[2][3]);
        boxSprites.put(Box.BoxType.POWER.name(), tempTextureRegion[3][0]);
        boxSprites.put(Box.BoxType.JUMP.name(), tempTextureRegion[3][1]);
        boxSprites.put(Box.BoxType.TIMER.name(), tempTextureRegion[3][2]);
        boxSprites.put(Box.BoxType.LIVE.name(), tempTextureRegion[3][3]);
        boxSprites.put(Box.BoxType.NONE.name(), tempTextureRegion[4][3]);
        craschAnimation = new Animation(4f, tempTextureRegion[4]);
    }
    private void createWorld_temp() {

        boxes.add(new Box(Box.BoxType.LIVE, Box.BoxState.GROUNDED, new Position(0, 0)));
        boxes.add(new Box(Box.BoxType.BOX_RED, Box.BoxState.GROUNDED, new Position(0, 10)));
        player = new Player(new Position(10, 0), Player.PlayerType.STRONG);
    }
    private void sleep(){
        if((score / 100) >= 0){
            gameSpeed = 50 - score / 100;
            if(gameSpeed <= 0){
                gameSpeed = 10;
            }
        }

        try {
            //Thread.sleep(fps / 2 + gameSpeed);
            //Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        FileHandle file = Gdx.files.local("temp");
        player.write(file);
        for(Box box : boxes) {
            box.write(file);
        }
    }
    private void write(FileHandle file) {

    file.writeString(score.toString() + '\n', false);
    file.writeString(gameSpeed.toString() + '\n', false);
    file.writeString(((Boolean)gameEnd).toString() + '\n', false);
}

}