package com.mygdx.game.basic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
/**
 * @author Mehdi Ouassou
 * @version 1.0
 */
public class BasicGame extends ApplicationAdapter {
    private Sound dropSound;

    private SpriteBatch batch;

    private Texture heroImg;
    private Texture enemyImg;

    private Rectangle hero;
    private Rectangle enemy;

    // Objects used
    Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;
    SpriteBatch spriteBatch;
    // A variable for tracking elapsed time for the animation
    float stateTime;

    private OrthographicCamera camera;

    private boolean flagStart = true;

    BitmapFont myBitMapFont;

    @Override
    public void create () {
        myBitMapFont = new BitmapFont();
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        heroImg = new Texture("square.png");
        enemyImg = new Texture("enemysprites.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        batch = new SpriteBatch();

        criaAnima(batch);

        hero = new Rectangle();
        hero.x = 20;
        hero.y = 20;
        hero.width = 20;
        hero.height = 20;

        enemy = new Rectangle();
        enemy.x = 80;
        enemy.y = 80;
        enemy.width = 20;
        enemy.height = 20;

    }

    private void criaAnima(SpriteBatch batch) {
        int FRAME_COLS = 1;
        int FRAME_ROWS = 2;

        // Load the sprite sheet as a Texture
        walkSheet = new Texture(Gdx.files.internal("enemysprites.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        walkAnimation = new Animation<TextureRegion>(0.525f, walkFrames);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        stateTime = 0f;
    }

    @Override
    public void render () {
        if (flagStart) {
            flagStart = false;
        }
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        camera.update();
        checkColision();

        //BATCH
        batch.begin();


        batch.draw(enemyImg,enemy.x,enemy.y,enemy.width,enemy.height);
        renderizaHeroi(batch);
        myBitMapFont.setColor(1f, 1f, 1f, 1f);
        myBitMapFont.draw(batch, "pontos: 0 ",50,600);


        batch.end();
        //BATCH

        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
            hero.y += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            hero.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            hero.y -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            hero.x += 200 * Gdx.graphics.getDeltaTime();
        }

    }

    private void renderizaHeroi(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        //batch.draw(heroImg,hero.x,hero.y,hero.width,hero.height);
        batch.draw(currentFrame, hero.x, hero.y,hero.width,hero.height); // Draw current frame at (50, 50)
    }

    private void checkColision() {
        if(hero.overlaps(enemy)){
            aleatorizaQuadrado();
            dropSound.play();
        }
    }

    private void aleatorizaQuadrado(){
        int min = 50; // Minimum value of range
        int max = 500; // Maximum value of range

        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        int random_int2 = (int)Math.floor(Math.random() * (max - min + 1) + min);

        System.out.println("Random X "+ random_int + " , Random Y " + random_int2);
        enemy.setX(enemy.getX()+random_int);
        enemy.setY(enemy.getY()+random_int2);
    }

    @Override
    public void dispose () {
        batch.dispose();
        heroImg.dispose();
        enemyImg.dispose();
        dropSound.dispose();
    }
}