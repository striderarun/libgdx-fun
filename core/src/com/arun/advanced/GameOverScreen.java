package com.arun.advanced;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameOverScreen implements Screen {

    final Drop game;
    OrthographicCamera camera;

    public GameOverScreen(Drop game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin();
        game.getBitmapFont().draw(game.getSpriteBatch(), "Game Over!!! ", 100, 150);
        game.getBitmapFont().draw(game.getSpriteBatch(), "Tap anywhere to start a new game!", 100, 100);
        game.getBitmapFont().draw(game.getSpriteBatch(), "Game by Arun!", 600, 100);
        game.getSpriteBatch().end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
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

    }
}
