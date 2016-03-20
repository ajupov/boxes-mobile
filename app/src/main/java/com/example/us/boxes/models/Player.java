package com.example.us.boxes.models;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.us.boxes.screens.GameScreen;

import java.util.Random;

public class Player {
    //Конструктор
    public Player(Position position, PlayerType type){
        this.position = position;
        this.type = type;
        state = PlayerState.DROPS;
        oldDirection = PlayerState.STEPS_LEFT;
        lives = 2;
        step = -1;
        animationTime = 0;
        artefact = Box.BoxType.NONE;
        artefactTimer = 0;
        artefactApplied = false;

        noSerializableInitialize();
    }

    //Переменные для сохранения
    public Position     position;               //Позиция
    public PlayerType   type;                   //Тип игрока
    public PlayerState  state;                  //Состояние игрока
    public PlayerState  oldDirection;           //Старое направление
    public int          lives;                  //Количество жизней
    public int          step;                   //Шаг движения
    private float       animationTime;          //Время анимации
    public Box.BoxType  artefact;               //Артефакт
    public float        artefactTimer;          //Время действия артефакта
    public boolean      artefactApplied;        //Состояние артефакта
    public Integer[]    Lbox = new Integer[6];  //Номера левых контактируемых ящиков
    public Integer[]    Rbox = new Integer[6];  //Номера правых контактируемых ящиков
    public Integer[]    Abox = new Integer[8];  //Номера контактируемых ящиков вокруг

    public boolean      contacted;  //Контактность

    private TextureRegion   currentFrame;           //Текущий фрейм
    private Animation       groundedAnimation;      //Анимация приземленного
    private Animation       killedAnimation;        //Анимация убитого
    private Animation       jumpedAnimation;        //Анимация прыгнутого
    private Animation       leftStepingAnimation;   //Анимация движения влево
    private Animation       rightStepingAnimation;  //Анимация движения вправо
    private Animation       leftMovingAnimation;    //Анимация перемещения влево
    private Animation       rightMovingAnimation;   //Анимация перемещения вправо

    public Contact  left;   //Левая грань
    public Contact  right;  //Правая грань
    public Contact  up;     //Верхняя грань
    public Contact  down;   //Нижняя грань

    //Установка направления передвижения
    public void setDirection(PlayerState direction){
        boxesChecking(direction);
        switch (direction) {
            case STEPS_LEFT:
                leftDirection();
                break;
            case STEPS_RIGHT:
                rightDirection();
                break;
            case JUMPED:
                upDirection(direction);
                break;
            case JUMPED_HIGH:
                upDirection(direction);
                break;
            case JUMPED_LEFT_LOW:
                leftUpDirection(direction);
                break;
            case JUMPED_LEFT_HIGH:
                leftUpDirection(direction);
                break;
            case JUMPED_RIGHT_LOW:
                rightUpDirection(direction);
                break;
            case JUMPED_RIGHT_HIGH:
                rightUpDirection(direction);
                break;
        }
    }
    public void boxesChecking(PlayerState checkDirection){
        Box box;
        switch (checkDirection) {
            //Определение при движении влево
            case STEPS_LEFT:
            case JUMPED_LEFT_LOW:
            case JUMPED_LEFT_HIGH:
                for (int i = 0; i < Lbox.length; i++) {
                    Lbox[i] = null;
                }
                //Определение Lbox[0]
                for (int i = 0; i < GameScreen.boxes.size; i++) {
                    box = GameScreen.boxes.get(i);
                    if (left.contact(box.right)) {
                        if (box.state == Box.BoxState.FLYING) {
                            Lbox[0] = Integer.MAX_VALUE;
                            break;
                        } //Ящик рядом летает
                        if (box.position.y == position.y) {
                            Lbox[0] = i;
                            break;
                        }
                    }
                }
                //Определение Lbox[1] и Lbox[2]
                if (Lbox[0] != null && Lbox[0] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Lbox[0]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.left.contact(GameScreen.boxes.get(i).right)) {
                            if (GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Lbox[1] = Integer.MAX_VALUE;
                                continue;
                            }
                            Lbox[1] = i;
                            continue;
                        }
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if (GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Lbox[2] = Integer.MAX_VALUE;
                                continue;
                            }
                            Lbox[2] = i;
                            continue;
                        }
                    }
                }
                //Определение Lbox[3] и Lbox[4]
                if (Lbox[1] != null && Lbox[1] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Lbox[1]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.left.contact(GameScreen.boxes.get(i).right)) {
                            if (GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Lbox[3] = Integer.MAX_VALUE;
                                continue;
                            }
                            Lbox[3] = i;
                            continue;
                        }
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if (GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Lbox[4] = Integer.MAX_VALUE;
                                continue;
                            }
                            Lbox[4] = i;
                            continue;
                        }
                    }
                }
                //Определение Lbox[5]
                if (Lbox[2] != null && Lbox[2] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Lbox[2]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if (GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Lbox[5] = Integer.MAX_VALUE;
                                break;
                            }
                            Lbox[5] = i;
                            break;
                        }
                    }
                }
                break;
            //Определение при движении вправо
            case STEPS_RIGHT:
            case JUMPED_RIGHT_LOW:
            case JUMPED_RIGHT_HIGH:
                for (int i = 0; i < Rbox.length; i++) {
                    Rbox[i] = null;
                }
                //Определение Rbox[0]
                for (int i = 0; i < GameScreen.boxes.size; i++) {
                    box = GameScreen.boxes.get(i);
                    if (right.contact(box.left)) {
                        if(box.state == Box.BoxState.FLYING) {
                            Rbox[0] = Integer.MAX_VALUE;
                            break;
                        } //Ящик рядом летает

                        if(box.position.y == position.y) {
                            Rbox[0] = i;
                            break;
                        }
                    }
                }
                //Определение Rbox[1] и Rbox[2]
                if(Rbox[0] != null && Rbox[0] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Rbox[0]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.right.contact(GameScreen.boxes.get(i).left)) {
                            if(GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Rbox[1] = Integer.MAX_VALUE;
                                continue;
                            }
                            Rbox[1] = i;
                            continue;
                        }
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if(GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Rbox[2] = Integer.MAX_VALUE;
                                continue;
                            }
                            Rbox[2] = i;
                            continue;
                        }
                    }
                }
                //Определение Rbox[3] и Rbox[4]
                if(Rbox[1] != null && Rbox[1] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Rbox[1]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.right.contact(GameScreen.boxes.get(i).left)) {
                            if(GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Rbox[3] = Integer.MAX_VALUE;
                                continue;
                            }
                            Rbox[3] = i;
                            continue;
                        }
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if(GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Rbox[4] = Integer.MAX_VALUE;
                                continue;
                            }
                            Rbox[4] = i;
                            continue;
                        }
                    }
                }
                //Определение Rbox[5]
                if(Rbox[2] != null && Rbox[2] != Integer.MAX_VALUE) {
                    box = GameScreen.boxes.get(Rbox[2]);
                    for (int i = 0; i < GameScreen.boxes.size; i++) {
                        if (box.up.contact(GameScreen.boxes.get(i).down)) {
                            if(GameScreen.boxes.get(i).state == Box.BoxState.FLYING) {
                                Rbox[5] = Integer.MAX_VALUE;
                                break;
                            }
                            Rbox[5] = i;
                            break;
                        }
                    }
                }
                break;
            case CHECK_AROUND:
                int arroundBoxesCounter = 0;
                for (int i = 0; i < Abox.length; i++) {
                    Abox[i] = null;
                }
                for (int i = 0; i < GameScreen.boxes.size; i++) {
                    box = GameScreen.boxes.get(i);
                    if(Contact.contacted(down, box.up) || Contact.contactedPlayerUp(up, box.down) || Contact.contacted(left, box.right) || Contact.contacted(right, box.left)) {
                        Abox[arroundBoxesCounter] = i;
                        arroundBoxesCounter++;
                        continue;
                    }
                }
                break;
            default:
                break;
        }
    }
    private void boxesSetDirection(PlayerState direction, Box first, Box second){
        switch (direction) {
            case MOVES_LEFT:
            case STEPS_LEFT:
                if(second == null) {
                    if(first == null) {
                        if(position.x > 0) {
                            state = PlayerState.STEPS_LEFT;
                            step = Box.SIZE;
                        } else {
                            state = PlayerState.DROPS;
                        }
                    } else {
                        if (first.position.x > 0 && first.type != Box.BoxType.BOX_BLACK) {
                            first.state = Box.BoxState.MOVING_LEFT;
                            state = PlayerState.MOVES_LEFT;
                            first.step = step = Box.SIZE;
                        } else {
                            state = PlayerState.DROPS;
                        }
                    }
                } else {
                    if (first.position.x > 0 && second.position.x > 0 && first.type != Box.BoxType.BOX_BLACK && second.type != Box.BoxType.BOX_BLACK) {
                        first.state = second.state = Box.BoxState.MOVING_LEFT;
                        state = PlayerState.MOVES_LEFT;
                        first.step = second.step = step = Box.SIZE;
                    } else {
                        state = PlayerState.DROPS;
                    }
                }
                break;
            case MOVES_RIGHT:
            case STEPS_RIGHT:
                if(second == null) {
                    if(first == null) {
                        if(position.x < GameScreen.WORLD_WIDTH - Box.SIZE) {
                            state = PlayerState.STEPS_RIGHT;
                            step = Box.SIZE;
                        } else {
                            state = PlayerState.DROPS;
                        }
                    } else {
                        if (first.position.x < GameScreen.WORLD_WIDTH - Box.SIZE && first.type != Box.BoxType.BOX_BLACK) {
                            first.state = Box.BoxState.MOVING_RIGHT;
                            state = PlayerState.MOVES_RIGHT;
                            first.step = step = Box.SIZE;
                        } else {
                            state = PlayerState.DROPS;
                        }
                    }
                } else {
                    if (first.position.x < GameScreen.WORLD_WIDTH - Box.SIZE && second.position.x < GameScreen.WORLD_WIDTH - Box.SIZE && first.type != Box.BoxType.BOX_BLACK && second.type != Box.BoxType.BOX_BLACK) {
                        first.state = second.state = Box.BoxState.MOVING_RIGHT;
                        state = PlayerState.MOVES_RIGHT;
                        first.step = second.step = step = Box.SIZE;
                    } else {
                        state = PlayerState.DROPS;
                    }
                }
                break;
            case GROUNDED:
            case DROPS:
                state = PlayerState.DROPS;
                break;
        }
    }
    private void leftDirection() {
        Box box_0 = null, box_1 = null;
        //Если ящик[0] не существует
        if(Lbox[0] == null) {
            //Идти
            boxesSetDirection(PlayerState.STEPS_LEFT, null, null);
        } else {
            //Если ящик[0] падает
            if(Lbox[0] == Integer.MAX_VALUE || (box_0 = GameScreen.boxes.get(Lbox[0])).type == Box.BoxType.BOX_BLACK) {
                //Стоять
                boxesSetDirection(PlayerState.DROPS, null, null);
            } else {
                if(Lbox[2] == null) {   //Если ящик[2] не существует и ящик[0] не черный
                    if(box_0.type == Box.BoxType.LIVE) {    //Если это жизнь
                        liveInc(Lbox[0]);   //Взять
                        boxesSetDirection(PlayerState.STEPS_LEFT, null, null);
                    } else {
                        //Если у игрока нет артефакта
                        if(artefact == Box.BoxType.NONE && box_0.type != Box.BoxType.BOX_RED && box_0.type != Box.BoxType.BOX_GREEN && box_0.type != Box.BoxType.BOX_BLUE && box_0.type != Box.BoxType.BOX_YELLOW) {
                            artefact = box_0.type;
                            GameScreen.boxes.removeIndex(Lbox[0]);
                            boxesSetDirection(PlayerState.STEPS_LEFT, null, null);
                        } else {    //Есть артефакт
                            if(Lbox[1] == null) {
                                //Передвигаем
                                boxesSetDirection(PlayerState.MOVES_LEFT, box_0, null);
                            } else {
                                if(type == PlayerType.STRONG || (artefact == Box.BoxType.POWER && artefactApplied)) { //Сильный
                                    if(Lbox[1] != Integer.MAX_VALUE && (box_1 = GameScreen.boxes.get(Lbox[1])).type != Box.BoxType.BOX_BLACK && Lbox[3] == null && Lbox[4] == null) {
                                        boxesSetDirection(PlayerState.MOVES_LEFT, box_0, box_1);
                                    } else {
                                        boxesSetDirection(PlayerState.DROPS, null, null);
                                    }
                                } else {    //Слабый
                                    boxesSetDirection(PlayerState.DROPS, null, null);
                                }
                            }
                        }
                    }
                } else {
                    if(type == PlayerType.STRONG || (artefact == Box.BoxType.POWER && artefactApplied)) { //Сильный
                        if(Lbox[2] != Integer.MAX_VALUE && (box_1 = GameScreen.boxes.get(Lbox[2])).type != Box.BoxType.BOX_BLACK && Lbox[1] == null && Lbox[4] == null && Lbox[5] == null) {
                            boxesSetDirection(PlayerState.MOVES_LEFT, box_0, box_1);
                        } else {
                            boxesSetDirection(PlayerState.DROPS, null, null);
                        }
                    } else {    //Слабый
                        boxesSetDirection(PlayerState.DROPS, null, null);
                    }
                }
            }
        }
        oldDirection = PlayerState.STEPS_LEFT;
    }
    private void rightDirection() {
        Box box_0 = null, box_1 = null;
        //Если ящик[0] не существует
        if(Rbox[0] == null) {
            //Идти
            boxesSetDirection(PlayerState.STEPS_RIGHT, null, null);
        } else {
            //Если ящик[0] падает
            if(Rbox[0] == Integer.MAX_VALUE || (box_0 = GameScreen.boxes.get(Rbox[0])).type == Box.BoxType.BOX_BLACK) {
                //Стоять
                boxesSetDirection(PlayerState.DROPS, null, null);
            } else {
                if(Rbox[2] == null) {   //Если ящик[2] не существует и ящик[0] не черный
                    if(box_0.type == Box.BoxType.LIVE) {    //Если это жизнь
                        liveInc(Rbox[0]);   //Взять
                        boxesSetDirection(PlayerState.STEPS_RIGHT, null, null);
                    } else {
                        //Если у игрока нет артефакта
                        if(artefact == Box.BoxType.NONE && box_0.type != Box.BoxType.BOX_RED && box_0.type != Box.BoxType.BOX_GREEN && box_0.type != Box.BoxType.BOX_BLUE && box_0.type != Box.BoxType.BOX_YELLOW) {
                            artefact = box_0.type;
                            GameScreen.boxes.removeIndex(Rbox[0]);
                            boxesSetDirection(PlayerState.STEPS_RIGHT, null, null);
                        } else {    //Есть артефакт
                            if(Rbox[1] == null) {
                                //Передвигаем
                                boxesSetDirection(PlayerState.MOVES_RIGHT, box_0, null);
                            } else {
                                if(type == PlayerType.STRONG || (artefact == Box.BoxType.POWER && artefactApplied)) { //Сильный
                                    if(Rbox[1] != Integer.MAX_VALUE && (box_1 = GameScreen.boxes.get(Rbox[1])).type != Box.BoxType.BOX_BLACK && Rbox[3] == null && Rbox[4] == null) {
                                        boxesSetDirection(PlayerState.MOVES_RIGHT, box_0, box_1);
                                    } else {
                                        boxesSetDirection(PlayerState.DROPS, null, null);
                                    }
                                } else {    //Слабый
                                    boxesSetDirection(PlayerState.DROPS, null, null);
                                }
                            }
                        }
                    }
                } else {
                    if(type == PlayerType.STRONG || (artefact == Box.BoxType.POWER && artefactApplied)) { //Сильный
                        if(Rbox[2] != Integer.MAX_VALUE && (box_1 = GameScreen.boxes.get(Rbox[2])).type != Box.BoxType.BOX_BLACK && Rbox[1] == null && Rbox[4] == null && Rbox[5] == null) {
                            boxesSetDirection(PlayerState.MOVES_RIGHT, box_0, box_1);
                        } else {
                            boxesSetDirection(PlayerState.DROPS, null, null);
                        }
                    } else {    //Слабый
                        boxesSetDirection(PlayerState.DROPS, null, null);
                    }
                }
            }
        }
        oldDirection = PlayerState.STEPS_RIGHT;
    }

    private void upDirection(PlayerState direction){
        if((type == PlayerType.JUMPER || (artefact == Box.BoxType.JUMP && artefactApplied)) && position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE * 2 && direction == PlayerState.JUMPED_HIGH) {    //Высокий
            state = PlayerState.JUMPED_HIGH;
            step = Box.SIZE;
        } else {    //Низкий
            if(position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE) {
                state = PlayerState.JUMPED;
                step = Box.SIZE;
            }
        }
    }
    private void leftUpDirection(PlayerState direction){
        if((type == PlayerType.JUMPER || (artefact == Box.BoxType.JUMP && artefactApplied)) && position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE * 2 && direction == PlayerState.JUMPED_LEFT_HIGH) {    //Высокий
            if(Lbox[0] == null && Lbox[2] == null && Lbox[5] == null && position.x > 0) {   //Диагональ свободна
                state = PlayerState.JUMPED_LEFT_HIGH_DIAGONALLY;
            } else {
                state = PlayerState.JUMPED_LEFT_HIGH;
            }
            step = Box.SIZE;
        } else {    //Низкий
            if(position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE) {
                if(Lbox[0] == null && Lbox[2] == null && Lbox[5] == null && position.x > 0) {   //Диагональ свободна
                    state = PlayerState.JUMPED_LEFT_LOW_DIAGONALLY;
                } else {
                    state = PlayerState.JUMPED_LEFT_LOW;
                }
                step = Box.SIZE;
            }
        }
    }
    private void rightUpDirection(PlayerState direction){
        if((type == PlayerType.JUMPER || (artefact == Box.BoxType.JUMP && artefactApplied)) && position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE * 2 && direction == PlayerState.JUMPED_RIGHT_HIGH) {    //Высокий
            if(Rbox[0] == null && Rbox[2] == null && Rbox[5] == null && position.x < GameScreen.WORLD_WIDTH - Box.SIZE) {   //Диагональ свободна
                state = PlayerState.JUMPED_RIGHT_HIGH_DIAGONALLY;
            } else {
                state = PlayerState.JUMPED_RIGHT_HIGH;
            }
            step = Box.SIZE;
        } else {    //Низкий
            if(position.y <= GameScreen.WORLD_HEIGHT - Box.SIZE) {
                if(Rbox[0] == null && Rbox[2] == null && Rbox[5] == null && position.x < GameScreen.WORLD_WIDTH - Box.SIZE) {   //Диагональ свободна
                    state = PlayerState.JUMPED_RIGHT_LOW_DIAGONALLY;
                } else {
                    state = PlayerState.JUMPED_RIGHT_LOW;
                }
                step = Box.SIZE;
            }
        }
    }

    //Работа с артефактами
    public void artefactApply(){
        switch (artefact) {
            case NONE:
                break;
            case BOOMB_RED:
                colorBoombApply(Box.BoxType.BOX_RED);
                break;
            case BOOMB_GREEN:
                colorBoombApply(Box.BoxType.BOX_GREEN);
                break;
            case BOOMB_BLUE:
                colorBoombApply(Box.BoxType.BOX_BLUE);
                break;
            case BOOMB_YELLOW:
                colorBoombApply(Box.BoxType.BOX_YELLOW);
                break;
            case BOOMB_BLACK:
                blackBoombApply();
                break;
            case HUMMER:
                hammerApply();
                break;
            case MAGNIT:
                magnitApply();
                break;
            case JUMP:
            case POWER:
            case TIMER:
                otherApply();
                break;
            default:
                artefact = Box.BoxType.NONE;
                break;
        }
    }
    public void liveInc(int boxNumber){
        if(lives < 3) {
            lives++;
        }
        GameScreen.boxes.removeIndex(boxNumber);
    }
    public void liveDec(){
        lives--;
        if(lives > 0) {
            move(-position.x + Math.abs(new Random().nextInt()) % (GameScreen.WORLD_WIDTH / Box.SIZE) * Box.SIZE, GameScreen.WORLD_HEIGHT);
            state = PlayerState.DROPS;
        } else {
            state = PlayerState.KILLED;
            GameScreen.gameEnd = true;
        }
    }
    private void colorBoombApply(Box.BoxType boxType){
        Box box;
        for (int i = 0; i < GameScreen.boxes.size; ++i) {
            box = GameScreen.boxes.get(i);
            if(box.type == boxType) {
                box.setAnnigilation();
            }
        }
        artefact = Box.BoxType.NONE;
    }
    private void blackBoombApply(){
        boxesChecking(PlayerState.CHECK_AROUND);
        for (int i = 0; i < Abox.length; i++) {
            if(Abox[i] != null) {
                GameScreen.boxes.get(Abox[i]).setAnnigilation();
            }
        }
        artefact = Box.BoxType.NONE;
    }
    private void hammerApply(){
        switch (oldDirection) {
            case STEPS_LEFT:
                boxesChecking(PlayerState.STEPS_LEFT);
                if(Lbox[0] != null && Lbox[0] != Integer.MAX_VALUE && GameScreen.boxes.get(Lbox[0]).type != Box.BoxType.BOX_BLACK) {
                    GameScreen.boxes.get(Lbox[0]).setAnnigilation();
                    artefact = Box.BoxType.NONE;
                }
                break;
            case STEPS_RIGHT:
                boxesChecking(PlayerState.STEPS_RIGHT);
                if(Rbox[0] != null && Rbox[0] != Integer.MAX_VALUE && GameScreen.boxes.get(Rbox[0]).type != Box.BoxType.BOX_BLACK) {
                    GameScreen.boxes.get(Rbox[0]).setAnnigilation();
                    artefact = Box.BoxType.NONE;
                }
                break;
        }
    }
    private void magnitApply(){
        boxesChecking(PlayerState.STEPS_LEFT);
        boxesChecking(PlayerState.STEPS_RIGHT);

        if(oldDirection == PlayerState.STEPS_LEFT && Rbox[0] == null && Rbox[2] == null && Lbox[0] != null && GameScreen.boxes.get(Lbox[0]).type != Box.BoxType.BOX_BLACK){
            state = PlayerState.MOVES_RIGHT;
            step = GameScreen.boxes.get(Lbox[0]).step = Box.SIZE;
            GameScreen.boxes.get(Lbox[0]).state = Box.BoxState.MOVING_RIGHT;
            artefact = Box.BoxType.NONE;
        }
        if(oldDirection == PlayerState.STEPS_RIGHT && Lbox[0] == null && Lbox[2] == null && Rbox[0] != null && GameScreen.boxes.get(Rbox[0]).type != Box.BoxType.BOX_BLACK){
            state = PlayerState.MOVES_LEFT;
            step = GameScreen.boxes.get(Rbox[0]).step = Box.SIZE;
            GameScreen.boxes.get(Rbox[0]).state = Box.BoxState.MOVING_LEFT;
            artefact = Box.BoxType.NONE;
        }
    }
    private void otherApply(){
        if(artefactApplied == false) {
            artefactApplied = true;
            artefactTimer = 0;
        }
    }
    public void artefactTimerTick(){
        if(artefactApplied){
            artefactTimer++;
            if(artefactTimer > GameScreen.fps * 10){
                artefactApplied = false;
                artefact = Box.BoxType.NONE;
            }
        }
    }

    //Работа с текстурами
    public TextureRegion getFrame(){
        switch (state) {
            case GROUNDED:
                currentFrame = groundedAnimation.getKeyFrame(animationTime, true);
                break;
            case KILLED:
                currentFrame = killedAnimation.getKeyFrame(animationTime, true);
                break;
            case JUMPED:
                currentFrame = jumpedAnimation.getKeyFrame(animationTime, true);
                break;
            case DROPS:
                currentFrame = jumpedAnimation.getKeyFrame(animationTime, true);
                break;
            case STEPS_LEFT:
                currentFrame = leftStepingAnimation.getKeyFrame(animationTime, true);
                break;
            case STEPS_RIGHT:
                currentFrame = rightStepingAnimation.getKeyFrame(animationTime, true);
                break;
            case MOVES_LEFT:
                currentFrame = leftMovingAnimation.getKeyFrame(animationTime, true);
                break;
            case MOVES_RIGHT:
                currentFrame = rightMovingAnimation.getKeyFrame(animationTime, true);
                break;
        }
        animationTime += Gdx.graphics.getDeltaTime();
        return currentFrame;
    }

    //Движения
    public void moving(){
        if(position.y <= 0) {
            contacted = true;
        }

        if (contacted == false && (state == PlayerState.DROPS || state == PlayerState.GROUNDED)) {
            move(0, -1);
            state = PlayerState.DROPS;
        } else {
            if(step > 0) {
                //Перемещения для игрока
                switch (state) {
                    case STEPS_LEFT:
                    case MOVES_LEFT:
                        move(-1, 0);
                        break;
                    case STEPS_RIGHT:
                    case MOVES_RIGHT:
                        move(1, 0);
                        break;
                    case JUMPED:
                    case JUMPED_LEFT_LOW:
                    case JUMPED_RIGHT_LOW:
                        if (position.y < GameScreen.WORLD_HEIGHT) {
                            move(0, 1);
                        }
                        break;
                    case JUMPED_HIGH:
                    case JUMPED_LEFT_HIGH:
                    case JUMPED_RIGHT_HIGH:
                        if (position.y < GameScreen.WORLD_HEIGHT) {
                            move(0, 2);
                        }
                        break;
                    case JUMPED_LEFT_LOW_DIAGONALLY:
                        if (position.y < GameScreen.WORLD_HEIGHT && position.x > 0) {
                            move(-1, 1);
                        }
                        break;
                    case JUMPED_LEFT_HIGH_DIAGONALLY:
                        if (position.y < GameScreen.WORLD_HEIGHT && position.x > 0) {
                            move(-1, 2);
                        }
                        break;
                    case JUMPED_RIGHT_LOW_DIAGONALLY:
                        if (position.y < GameScreen.WORLD_HEIGHT && position.x < GameScreen.WORLD_WIDTH - Box.SIZE) {
                            move(1, 1);
                        }
                        break;
                    case JUMPED_RIGHT_HIGH_DIAGONALLY:
                        if (position.y < GameScreen.WORLD_HEIGHT && position.x < GameScreen.WORLD_WIDTH - Box.SIZE) {
                            move(1, 2);
                        }
                        break;
                    default:
                        break;
                }
                step--;
                if(step == 0) {
                    switch (state) {
                        case JUMPED_LEFT_LOW:
                        case JUMPED_LEFT_HIGH:
                            boxesChecking(PlayerState.STEPS_LEFT);
                            leftDirection();
                            break;
                        case JUMPED_RIGHT_LOW:
                        case JUMPED_RIGHT_HIGH:
                            boxesChecking(PlayerState.STEPS_RIGHT);
                            rightDirection();
                            break;
                        default:
                            state = PlayerState.DROPS;
                            break;
                    }
                }
            } else {
                if(contacted == true){
                    state = PlayerState.GROUNDED;
                }
            }
        }
    }
    private void move(int x, int y){
        position.x += x;
        position.y += y;
        left.move(x, y);
        right.move(x, y);
        down.move(x, y);
        up.move(x, y);

    }
    public void contacting(int horisontalBoxesCount){
        contacted = false;
        for (int i = 0; i < GameScreen.boxes.size; ++i) {
            Box box;
            //Отмечаем нижний ряд, если он полон
            if (horisontalBoxesCount == GameScreen.WORLD_WIDTH / Box.SIZE) {
                if (GameScreen.boxes.get(i).position.y == 0) {
                    GameScreen.boxes.get(i).setAnnigilation();
                }
            }

            //Проверка на контактность игрока
            if(position.y == 0){
                contacted = true;
            } else {
                box = GameScreen.boxes.get(i);
                if (Contact.contacted(down, box.up)) {
                    switch (box.type) {
                        case BOX_RED:
                        case BOX_GREEN:
                        case BOX_BLUE:
                        case BOX_YELLOW:
                        case BOX_BLACK:
                            contacted = true;
                            break;
                        case LIVE:
                            liveInc(i);
                            break;
                        default:
                            if (artefact == Box.BoxType.NONE && box.state != Box.BoxState.NONE) {
                                artefact = box.type;
                                GameScreen.boxes.removeIndex(i);
                            } else {
                                contacted = true;
                            }
                            break;
                    }
                }
            }
            if (GameScreen.boxes.size != 0 && i >= 0 && i < GameScreen.boxes.size && GameScreen.boxes.get(i).state == Box.BoxState.NONE && GameScreen.boxes.get(i).annigilationTime <= 0) {
                GameScreen.boxes.removeIndex(i);
                i--;
                GameScreen.score++;
                for (int j = 0; j < 6; j++) {
                    if (Lbox[j] != null) {
                        Lbox[j]--;
                    }
                    if (Rbox[j] != null) {
                        Rbox[j]--;
                    }
                    if(Abox[j] != null){
                        Abox[j]--;
                    }
                }
            }
        }
    }

    //Перечисления
    public enum PlayerType {
        SIMPLE,
        STRONG,
        JUMPER
    }
    public enum PlayerState {
        GROUNDED,
        DROPS,
        KILLED,

        STEPS_LEFT,
        STEPS_RIGHT,

        MOVES_LEFT,
        MOVES_RIGHT,

        JUMPED,
        JUMPED_HIGH,

        JUMPED_LEFT_LOW,
        JUMPED_RIGHT_LOW,
        JUMPED_LEFT_LOW_DIAGONALLY,
        JUMPED_RIGHT_LOW_DIAGONALLY,

        JUMPED_LEFT_HIGH,
        JUMPED_RIGHT_HIGH,
        JUMPED_LEFT_HIGH_DIAGONALLY,
        JUMPED_RIGHT_HIGH_DIAGONALLY,

        CHECK_AROUND
    }

    //////////////////////////////////
    public void noSerializableInitialize(){
        contacted = false;

        left = new Contact(new Position(position), new Position(position.x, position.y + Box.SIZE * 2));
        right = new Contact(new Position(position.x + Box.SIZE, position.y), new Position(position.x + Box.SIZE, position.y + Box.SIZE * 2));
        up = new Contact(new Position(position.x, position.y+ Box.SIZE * 2), new Position(position.x + Box.SIZE, position.y + Box.SIZE * 2));
        down = new Contact(new Position(position), new Position(position.x + Box.SIZE, position.y));

        Texture texture;
        TextureRegion tmp[][];
        switch (type) {
            case SIMPLE:
            default:
                texture = new Texture(Gdx.files.internal("Simple_player.png"));
                break;
            case STRONG:
                texture = new Texture(Gdx.files.internal("Simple_player.png"));
                break;
            case JUMPER:
                texture = new Texture(Gdx.files.internal("Simple_player.png"));
                break;
        }
        tmp = TextureRegion.split(texture, texture.getWidth() / 4, texture.getHeight() / 4);
        jumpedAnimation = new Animation(1 / 4f, tmp[0]);
        groundedAnimation = new Animation(1 / 4f, tmp[1]);
        leftStepingAnimation = new Animation(1 / 4f, tmp[2]);
        rightStepingAnimation = new Animation(1 / 4f, tmp[3]);
        leftMovingAnimation = new Animation(1 / 4f, tmp[2]);
        rightMovingAnimation = new Animation(1 / 4f, tmp[3]);
        killedAnimation = jumpedAnimation;
    }
    public void write(FileHandle file) {
        file.writeString(position.toString() + '\n', true);
        file.writeString(type.toString() + '\n', false);
        file.writeString(state.toString() + '\n', false);
        file.writeString(oldDirection.toString() + '\n', false);
        file.writeString(((Integer)(lives)).toString() + '\n', false);
        file.writeString(((Integer)(step)).toString() + '\n', false);
        file.writeString(((Float)(animationTime)).toString() + '\n', false);
        file.writeString(artefact.toString() + '\n', false);
        file.writeString(((Float)(artefactTimer)).toString() + '\n', false);
        file.writeString(((Boolean)(artefactApplied)).toString() + '\n', false);
        file.writeString(Lbox.toString() + '\n', false);
        file.writeString(Rbox.toString() + '\n', false);
        file.writeString(Abox.toString() + '\n', false);
    }
}