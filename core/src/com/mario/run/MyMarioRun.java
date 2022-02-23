package com.mario.run;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MyMarioRun extends ApplicationAdapter {
    private String tag = "log";
    SpriteBatch batch;
    Texture background;
    Texture coin;
    Texture bomb;
    private float widthScreen = 0;
    private float heightScreen = 0;
    private Texture[] character;
    private Texture[] dizzyCharacter;
    private int fps = 30;
    private int count = 0;
    private int countDizzy = 0;
    private int coinCount = 0;
    private int bombCount = 0;
    private int state = 0;
    private int stateDizzy = 0;
    private float characterX = 0;
    private float characterY = 0;
    private float gravity = 0.3f;
    private float velocity = 0f;
    private final ArrayList<Coins> coins = new ArrayList();
    private final ArrayList<Bombs> bombs = new ArrayList();
    private final ArrayList<Rectangle> coinsRectangle = new ArrayList();
    private final ArrayList<Rectangle> bombsRectangle = new ArrayList();
    private final Rectangle mCharacterRectangle = new Rectangle(0, 0, 0, 0);
    private int score = 0;
    private int gameState = 0;
    private BitmapFont font;

    MyMarioRun() {
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("img.png");
        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        widthScreen = Gdx.graphics.getWidth();
        heightScreen = Gdx.graphics.getHeight();
        font = new BitmapFont(false);
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        initCharacter();
    }

    private void initCharacter() {
        character = new Texture[4];
        dizzyCharacter = new Texture[2];
        character[0] = new Texture("jump/frame-1.png");
        character[1] = new Texture("jump/frame-2.png");
        character[2] = new Texture("jump/frame-3.png");
        character[3] = new Texture("jump/frame-4.png");
        dizzyCharacter[0] = new Texture("dizzy/frame-1.png");
        dizzyCharacter[1] = new Texture("dizzy/frame-2.png");

        characterX = widthScreen / 2 - character[0].getWidth() / 2f;
        characterY = heightScreen / 2 - character[0].getHeight() / 2f;
    }

    private void makeCoin() {
        float height = new Random().nextFloat() * heightScreen;
        coins.add(new Coins(widthScreen, height));
    }

    private void makeBomb() {
        float height = new Random().nextFloat() * heightScreen;
        bombs.add(new Bombs(widthScreen, height));
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, widthScreen, heightScreen);
        if (gameState == 1) {
            handleTouch();
            if (bombCount <= 150) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }
            bombsRectangle.clear();
            for (int i = 0; i < bombs.size(); i++) {
                batch.draw(bomb, bombs.get(i).getX(), bombs.get(i).getY());
                bombs.get(i).setX(bombs.get(i).getX() - 8);
                bombsRectangle.add(new Rectangle(bombs.get(i).getX(), bombs.get(i).getY(), bomb.getWidth(), bomb.getHeight()));
            }

            if (coinCount <= 60) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinsRectangle.clear();
            for (int i = 0; i < coins.size(); i++) {
                batch.draw(coin, coins.get(i).getX(), coins.get(i).getY());
                coins.get(i).setX(coins.get(i).getX() - 4);
                coinsRectangle.add(new Rectangle(coins.get(i).getX(), coins.get(i).getY(), coin.getWidth(), coin.getHeight()));
            }

            if (count <= fps) {
                count++;
            } else {
                count = 0;
                if (state < 3) state++;
                else state = 0;
            }
            velocity += gravity;
            characterY -= velocity;
            if (characterY <= 0) characterY = 0;
            if ((characterY >= heightScreen - character[state].getHeight())) characterY = heightScreen - character[state].getHeight();
            batch.draw(character[state], characterX, characterY);
            mCharacterRectangle.set(characterX, characterY, character[state].getWidth(), character[state].getHeight());
            for (int i = 0; i < coinsRectangle.size(); i++) {
                if (Intersector.overlaps(mCharacterRectangle, coinsRectangle.get(i))) {
                    score++;
                    Gdx.app.log(tag, "Coin collision ~");
                    coinsRectangle.remove(i);
                    coins.remove(i);
                    break;
                }
            }

            for (int i = 0; i < bombsRectangle.size(); i++) {
                if (Intersector.overlaps(mCharacterRectangle, bombsRectangle.get(i))) {
                    Gdx.app.log(tag, "Bomb collision ~");
                    gameState = 2;
                }
            }

            font.draw(batch, score + "", 100, 150);
        } else if (gameState == 0) {
            font.draw(batch, "Start Game", widthScreen/2-400, heightScreen/2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else {
			font.draw(batch, "Game Over", widthScreen/2-400, heightScreen/2);
            if (countDizzy <= fps) {
                countDizzy++;
            } else {
                countDizzy = 0;
                if (stateDizzy < 1) stateDizzy++;
                else stateDizzy = 0;
            }
            batch.draw(dizzyCharacter[stateDizzy], characterX, characterY);
            score=0;
            velocity=0;
            coins.clear();
            bombs.clear();
            coinCount=0;
            bombCount=0;
            bombsRectangle.clear();
            coinsRectangle.clear();
            initCharacter();
            if(Gdx.input.justTouched()){
                gameState=1;
            }
        }


        batch.end();
    }

    private void handleTouch() {
        if (Gdx.input.justTouched()) velocity -= 10;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

class Coins {
    private float X;
    private float Y;

    public Coins(float x, float y) {
        X = x;
        Y = y;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }
}

class Bombs {
    private float X;
    private float Y;

    public Bombs(float x, float y) {
        X = x;
        Y = y;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }
}