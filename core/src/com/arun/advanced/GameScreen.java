package com.arun.advanced;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    final Drop game;

    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;
    int dropsLost;
    int gameLevel;

    public GameScreen(final Drop game) {
        this.game = game;

//        dropImage = new Texture(Gdx.files.internal("drop.png"));
//        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("/Users/arun_subramonian/Documents/Fun/GameTwo/android/assets/data/drop.png"));
        bucketImage = new Texture(Gdx.files.internal("/Users/arun_subramonian/Documents/Fun/GameTwo/android/assets/data/bucket.png"));

//        dropImage = new Texture(Gdx.files.internal("data/drop.png"));
//        bucketImage = new Texture(Gdx.files.internal("data/bucket.png"));
        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("/Users/arun_subramonian/Documents/Fun/GameTwo/android/assets/data/explode.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("/Users/arun_subramonian/Documents/Fun/GameTwo/android/assets/data/music.mp3"));

//        dropSound = Gdx.audio.newSound(Gdx.files.internal("explode.wav"));
//        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
        gameLevel = 1;

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.getSpriteBatch().begin();
        game.getBitmapFont().draw(game.getSpriteBatch(), "Drops Collected: " + dropsGathered, 0, 480);
        game.getBitmapFont().draw(game.getSpriteBatch(), "Drops Lost: " + dropsLost, 200, 480);
        game.getBitmapFont().draw(game.getSpriteBatch(), "Game Level: " + gameLevel, 400, 480);
        game.getSpriteBatch().draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            game.getSpriteBatch().draw(dropImage, raindrop.x, raindrop.y);
        }
        game.getSpriteBatch().end();

        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000/gameLevel)
            spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                iter.remove();
                dropsLost++;
                if(dropsLost > 10) {
                    game.setScreen(new GameOverScreen(game));
                    dispose();
                }
            }
            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                if(dropsGathered >20) {
                    gameLevel = 2;
                } else if (dropsGathered >30) {
                    gameLevel = 3;
                }
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void show() {
        rainMusic.play();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
