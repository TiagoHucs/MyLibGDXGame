package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final ShotGame game;
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle hero;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private State state;
    private BitmapFont font;
    private int count_raindrops;

    public GameScreen(final ShotGame game) {
        this.game = game;
        //set initial state
        state = State.RUN;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("square.png"));
        bucketImage = new Texture(Gdx.files.internal("tank.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        rainMusic.setLooping(true);
        rainMusic.play();

        //create the Camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        //creating the bucket
        hero = new Rectangle();
        hero.x = 20;
        hero.y = 20;
        hero.width = 20;
        hero.height = 20;

        //creating the raindrops
        raindrops = new Array<>();
        spawnRaindrop();

        font = new BitmapFont();

        count_raindrops = 0;
    }

    //create a rain drop
    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 20;
        raindrop.height = 20;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.P))
            pause();
        if (Gdx.input.isKeyPressed(Input.Keys.R))
            resume();
        //Red green blue
        Gdx.gl.glClearColor(0, 20, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, hero.x, hero.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        font.draw(batch, String.valueOf(count_raindrops), 10, 470);
        batch.end();

        switch (state) {
            case RUN:
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                    hero.x -= 200 * Gdx.graphics.getDeltaTime();
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                    hero.x += 200 * Gdx.graphics.getDeltaTime();
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                    hero.y -= 200 * Gdx.graphics.getDeltaTime();
                if (Gdx.input.isKeyPressed(Input.Keys.UP))
                    hero.y += 200 * Gdx.graphics.getDeltaTime();
                //check screen limits
                if (hero.x < 0)
                    hero.x = 0;
                if (hero.x > 800 - 20)
                    hero.x = 800 - 20;
                //check time to create another raindrop
                if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
                    spawnRaindrop();
                //move raindrops created
                for (Iterator<Rectangle> it = raindrops.iterator(); it.hasNext(); ) {
                    Rectangle raindrop = it.next();
                    raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
                    //check if it is beyond the screen
                    if (raindrop.y + 64 < 0)
                        it.remove();
                    //check collision between bucket and raindrops
                    if (raindrop.overlaps(hero)) {
                        count_raindrops++;
                        dropSound.play();
                        it.remove();
                    }
                }
                break;
            case PAUSE:
                batch.begin();
                font.draw(batch, "PAUSED", 380, 250);
                batch.end();
                break;
        }


    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        this.state = State.PAUSE;
    }

    @Override
    public void resume() {
        this.state = State.RUN;
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
        batch.dispose();
    }

    public enum State {
        PAUSE,
        RUN,
    }
}
