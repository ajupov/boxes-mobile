package com.example.us.boxes.models;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.us.boxes.screens.GameScreen;

import java.io.Serializable;

public class Box implements Serializable {
    //Конструктор
    public Box(BoxType type, BoxState state, Position position){
        this.type = type;
        this.state = state;
        this.position = position;

        annigilationTime = null;      //Не уничтожается при создании
        step = -1;
        contacted = false;

        noSerializableInitialize();
    }

    //Переменные
    public Position position;           //Позиция ящика
    public BoxType  type;               //Тип ящика
    public BoxState state;              //Состояние ящика

    public boolean  contacted;          //Контактность
    public int      step;               //Шаг движения

    public Integer  annigilationTime;   //Время уничтожения

    public Contact  left;   //Левый контакт
    public Contact  right;  //Правый контакт
    public Contact  up;     //Верхний контакт
    public Contact  down;   //Нижний контакт

    public static final int SIZE = 10;

    //Контактируемость
    public boolean contacting(int i){
        contacted = false;
        if (position.y > 0) {
            //Проверка на контакт с игроком
            if (Contact.contactedPlayerUp(down, GameScreen.player.up)) {
                if(type == BoxType.LIVE) { //Если жизнь
                    GameScreen.player.liveInc(i);
                    return false;
                } else if(type == BoxType.BOX_BLACK) {  //Если черный ящик
                    if (GameScreen.player.state != Player.PlayerState.DROPS && GameScreen.player.state != Player.PlayerState.KILLED) {
                        GameScreen.player.liveDec();
                    }
                } else if(GameScreen.player.artefact == Box.BoxType.NONE && type != BoxType.BOX_RED && type != BoxType.BOX_GREEN && type != BoxType.BOX_BLUE && type != BoxType.BOX_YELLOW) {
                    //Если не ящик и нет артефакта
                    GameScreen.player.artefact = type;
                    GameScreen.boxes.removeIndex(i);
                    return false;
                } else {    //Иначе
                    switch (GameScreen.player.state) {
                        case DROPS:
                        case KILLED:
                            break;
                        case GROUNDED:
                        case STEPS_LEFT:
                        case STEPS_RIGHT:
                        case MOVES_LEFT:
                        case MOVES_RIGHT:
                            contacted = true;
                            GameScreen.player.liveDec();
                            break;
                        default:
                            this.setAnnigilation();
                            break;
                    }
                }
            }

            //Проверка на контакт с другими ящиками
            for (int j = 0; j < GameScreen.boxes.size; ++j) {
                //Проверка на контакт с ящиками
                if (Contact.contacted(down, GameScreen.boxes.get(j).up)) {
                    contacted = true;
                    break;
                }
            }
        } else {
            contacted = true;
        }
        return true;
    }

    //Поиск аннигилируемых
    public void simpleFounding(){
        if (state == BoxState.GROUNDED || state == BoxState.NONE) {
            Box leftBox = null, rightBox = null, upBox = null, downBox = null;
            for (int j = 0; j < GameScreen.boxes.size; ++j) {
                Box temp = GameScreen.boxes.get(j);
                if (temp.state == BoxState.GROUNDED || temp.state == BoxState.NONE) {
                    if(Contact.contacted(left, temp.right)) {
                        leftBox = temp;
                        continue;
                    }
                    if (Contact.contacted(right, temp.left)) {
                        rightBox = temp;
                        continue;
                    }
                    if (Contact.contacted(up, temp.down)) {
                        upBox = temp;
                        continue;
                    }
                    if (Contact.contacted(down, temp.up)) {
                        downBox = temp;
                        continue;
                    }
                }
            }
            nearFounding(leftBox, downBox);
            nearFounding(leftBox, rightBox);
            nearFounding(leftBox, upBox);
            nearFounding(downBox, rightBox);
            nearFounding(downBox, upBox);
            nearFounding(rightBox, upBox);
        }
    }
    private void nearFounding(Box second, Box third){
        if (second != null && third != null) {
            if(type == second.type && type == third.type) {
                switch (type) {
                    case BOX_RED:
                    case BOX_GREEN:
                    case BOX_BLUE:
                    case BOX_YELLOW:
                        setAnnigilation();
                        second.setAnnigilation();
                        third.setAnnigilation();
                        break;
                }
            }
        }
    }
    public void setAnnigilation(){
        state = BoxState.NONE;
        if(annigilationTime == null) {
            annigilationTime = GameScreen.gameSpeed * SIZE;
        }
    }

    //Работа с текстурами
    public TextureRegion getFrame(){
        TextureRegion temp;
        if (state == BoxState.NONE) {
            temp = GameScreen.craschAnimation.getKeyFrame(annigilationTime % (GameScreen.gameSpeed * 8), true);
        } else {
            temp = GameScreen.boxSprites.get(type.name());
        }
        return temp;
    }

    //Движения
    public void moving(){
        if (contacted == false && (state == BoxState.FLYING || state == BoxState.GROUNDED)) {
            state = BoxState.FLYING;
            move(0, -1);
        } else {
            if(step > 0) {
                switch (state) {
                    case MOVING_LEFT:
                        move(-1, 0);
                        break;
                    case MOVING_RIGHT:
                        move(1, 0);
                        break;
                    default:
                        break;
                }
                step--;
                if(step == 0) {
                    switch (state) {
                        case MOVING_LEFT:
                        case MOVING_RIGHT:
                            state = Box.BoxState.FLYING;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if(contacted == true && state != Box.BoxState.NONE){
                    state = BoxState.GROUNDED;
                }
            }
            if (state == Box.BoxState.NONE) {
                annigilationTime -= GameScreen.gameSpeed * 2;
            }
        }
    }
    public void move(int x, int y){
        position.x += x;
        position.y += y;
        left.move(x,y);
        right.move(x,y);
        down.move(x,y);
        up.move(x,y);
    }

    //Перечисления
    public enum BoxType {
        BOX_RED,
        BOX_GREEN,
        BOX_BLUE,
        BOX_YELLOW,
        BOX_BLACK,

        BOOMB_RED,
        BOOMB_GREEN,
        BOOMB_BLUE,
        BOOMB_YELLOW,
        BOOMB_BLACK,

        HUMMER,
        MAGNIT,
        POWER,
        JUMP,
        TIMER,

        LIVE,

        NONE
    }
    public enum BoxState {
        MOVING_LEFT,
        MOVING_RIGHT,
        FLYING,
        GROUNDED,
        NONE
    }

    public void noSerializableInitialize() {
        contacted = false;
        //Установка боковых граней
        left =  new Contact(new Position(position), new Position(position.x, position.y + SIZE));
        right = new Contact(new Position(position.x + SIZE, position.y), new Position(position.x + SIZE, position.y + SIZE));
        up =    new Contact(new Position(position.x, position.y + SIZE), new Position(position.x + SIZE, position.y + SIZE));
        down =  new Contact(new Position(position), new Position(position.x + SIZE, position.y));
    }
    public void write(FileHandle file) {
        file.writeString(position.toString() + '\n', false);
        file.writeString(type.toString() + '\n', false);
        file.writeString(state.toString() + '\n', false);
        file.writeString(((Integer)(step)).toString() + '\n', false);
        file.writeString(annigilationTime.toString() + '\n', false);
    }
}

