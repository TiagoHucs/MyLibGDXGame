package com.mygdx.game.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * @author Mehdi Ouassou
 * @version 1.0
 */
public class BaseGameScreen implements Screen {
    final BaseGame game;
    private Sound dropSound;

    private SpriteBatch batch;

    private Texture enemyImg;

    private Ator astro;
    private Ator enemy;
    private Ator minerio;
    private Ator base;

    // Objects used
    Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
    Texture walkSheet;
    SpriteBatch spriteBatch;
    // A variable for tracking elapsed time for the animation
    float stateTime;

    private OrthographicCamera camera;

    private boolean flagStart = true;

    BitmapFont myBitMapFont;

    int pontos = 0;
    int o2 = 1000;

    public BaseGameScreen(final BaseGame game) {
        this.game = game;
        myBitMapFont = new BitmapFont();
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        camera = new OrthographicCamera();
        camera.setToOrtho(true, 800, 600);
        batch = new SpriteBatch();

        criaAnima();

        astro = new Ator("astro.png");
        enemy =  new Ator("enemysprites.png");
        minerio =  new Ator("minerio.png");
        base =  new Ator("o2.png");
        base.x = 400;
        base.y = 400;
        base.width = 100;
        base.height = 100;

        aleatorizaQuadrado();
        aleatorizaMinerio();

    }

    private void criaAnima() {
        int FRAME_COLS = 1;
        int FRAME_ROWS = 2;

        walkSheet = new Texture(Gdx.files.internal("enemysprites.png"));
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.525f, walkFrames);
        stateTime = 0f;
    }

    private void renderizaInimigo(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, enemy.x, enemy.y,enemy.width,enemy.height); // Draw current frame at (50, 50)
    }

    private void checkColision() {
        if(astro.overlaps(enemy)){
            pontos++;
            aleatorizaQuadrado();
            dropSound.play();
        }

        if(astro.overlaps(minerio)){
            pontos++;
            aleatorizaMinerio();
            dropSound.play();
        }
    }

    private void checkOxigem() {
        if(astro.overlaps(base)){
            if(o2 < 1000)
            o2++;
        } else {
            if(o2 > 0)
            o2--;
        }

        if(o2 <= 0){
            game.setScreen(new BaseGameMenuScreen(game));
        }
    }

    private void aleatorizaQuadrado(){
        int min = 50; // Minimum value of range
        int max = 500; // Maximum value of range

        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        int random_int2 = (int)Math.floor(Math.random() * (max - min + 1) + min);

        System.out.println("Random X "+ random_int + " , Random Y " + random_int2);
        enemy.setX(random_int);
        enemy.setY(random_int2);
    }

    private void aleatorizaMinerio(){
        int min = 50; // Minimum value of range
        int max = 500; // Maximum value of range

        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        int random_int2 = (int)Math.floor(Math.random() * (max - min + 1) + min);

        System.out.println("Random X "+ random_int + " , Random Y " + random_int2);
        minerio.setX(random_int);
        minerio.setY(random_int2);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        if (flagStart) {
            flagStart = false;
        }
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        camera.update();
        checkColision();
        checkOxigem();
        //BATCH
        batch.begin();

        batch.draw(base.img,base.x,base.y,base.width,base.height);
        batch.draw(astro.img, astro.x, astro.y, astro.width, astro.height);
        batch.draw(minerio.img, minerio.x, minerio.y, minerio.width, minerio.height);
        renderizaInimigo(batch);
        myBitMapFont.setColor(5f, 5f, 1f, 1f);
        myBitMapFont.draw(batch, "Oxigênio: " + o2,50,580);
        myBitMapFont.draw(batch, "Pontos: " + pontos,50,560);


        batch.end();
        //BATCH

        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
            if(astro.x < (600 - astro.getHeight()))
                astro.y += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            if(astro.x > 0)
                astro.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            if(astro.y > 0)
                astro.y -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            if(astro.x < (800 - astro.getWidth()))
                astro.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            System.exit(0);
        }
    }

    @Override
    public void resize(int i, int i1) {

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
    public void dispose () {
        batch.dispose();
        astro.img.dispose();
        enemy.img.dispose();
        base.img.dispose();
        dropSound.dispose();
    }
}