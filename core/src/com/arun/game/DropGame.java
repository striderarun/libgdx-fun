package com.arun.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DropGame extends ApplicationAdapter {

    private SpriteBatch sprite;
    private Texture drop;
    private Texture bucket;
    private Sound dropSound;
    private Music dropMusic;
    private OrthographicCamera camera;
    private Rectangle bucketArea;
    private Array<Rectangle> raindrops;
    private long lastDropTime;

    /**
     *  For Android
     */
    @Override
    public void create() {
        sprite = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        bucketArea = new Rectangle();
        bucketArea.x = 800/2 - 64/2;
        bucketArea.y = 20;
        bucketArea.width = 64;
        bucketArea.height = 64;

        raindrops = new Array<Rectangle>();
        dropMusic.setLooping(true);
        dropMusic.play();
        spawnRaindrop();

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
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        sprite.setProjectionMatrix(camera.combined);
        sprite.begin();
        sprite.draw(bucket, bucketArea.x, bucketArea.y);
        for(Rectangle raindrop: raindrops) {
            sprite.draw(drop, raindrop.x, raindrop.y);
        }
        sprite.end();

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucketArea.x = touchPos.x - 64 / 2;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketArea.x -= 300 * Gdx.graphics.getDeltaTime();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketArea.x += 300 * Gdx.graphics.getDeltaTime();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            bucketArea.y += 300 * Gdx.graphics.getDeltaTime();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            bucketArea.y -= 300 * Gdx.graphics.getDeltaTime();
        }

        if(bucketArea.x < 0) {
            bucketArea.x = 0;
        }
        if(bucketArea.x > 800 - 64) {
            bucketArea.x = 800 - 64;
        }
        if(bucketArea.y <0) {
            bucketArea.y=0;
        }
        if (bucketArea.y > 480 - 64) {
            bucketArea.y = 480 - 64;
        }
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }
        Iterator<Rectangle> iter = raindrops.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 100 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0) iter.remove();
            if(raindrop.overlaps(bucketArea)) {
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void dispose() {
        drop.dispose();
        bucket.dispose();
        dropSound.dispose();
        dropMusic.dispose();
        sprite.dispose();
    }


}
