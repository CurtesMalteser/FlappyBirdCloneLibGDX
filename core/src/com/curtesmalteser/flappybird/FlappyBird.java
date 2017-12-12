package com.curtesmalteser.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;

    // Bird
    private Texture[] bird;

    // Background
    private Texture background;
    private Texture teste;

    // Tubes
    private Texture topTube;
    private Texture bottomTube;

    // Game Over
    private Texture gameOver;

    private Random random;

    // Show the Score
    private BitmapFont scoreFont;

    // Game Over message
    private BitmapFont gameOverMessageFont;

    // Create the shapes to detect collisions
    private Circle birdCircle;
    private Rectangle topTubeRectangle;
    private Rectangle bottomTubeRectangle;
    private ShapeRenderer shapeRenderer;

    // Config attributes
    private float screenWidth;
    private float screenHeight;

    private int gameState = 0; // 0 --> game is stopped 1 --> game started --> game over
    private int score = 0;

    // Initial bird position o Y axis
    private float birdInitVerticalPosition = 0;

    // The variation is used to move the the bird wings
    private float variation = 0;

    // Bird falls speed
    private float birdFallSpeed = 0;

    // Tube movement
    private float hTubeMovement = 0;

    // Background movement
    private int hBackgroundMovement = 0;
    private int hTesteMovement = 0;

    // Tube space
    private float spaceBetweenTubes;

    private float deltaTime;

    // Random number to move the tubes up/down
    private float randomSpaceBetweenTubes;

    // Test if bird passed the tube
    // The boolean, by default was already false, just set it by the visual effect
    private boolean goal = false;

    // Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;
    @Override
    public void create () {

        batch = new SpriteBatch();

        random = new Random();

        // Instantiate the shapes to detect collision
        birdCircle = new Circle();
        topTubeRectangle = new Rectangle();
        bottomTubeRectangle = new Rectangle();
        shapeRenderer = new ShapeRenderer();

        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(6);

        gameOverMessageFont = new BitmapFont();
        gameOverMessageFont.setColor(Color.WHITE);
        gameOverMessageFont.getData().setScale(3);

        // Instantiate the bird
        bird = new Texture[3];
        bird[0] = new Texture("bird1.png");
        bird[1] = new Texture("bird2.png");
        bird[2] = new Texture("bird3.png");

        /*bird = new Texture[4];
        bird[0] = new Texture("pterodactyl_01.png");
        bird[1] = new Texture("pterodactyl_02.png");
        bird[2] = new Texture("pterodactyl_03.png");
        bird[3] = new Texture("pterodactyl_04.png");*/


        background = new Texture("fundo.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        teste = new Texture("teste.png");
        teste.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Instantiate the tubes
        topTube = new Texture("cano_topo.png");
        bottomTube = new Texture("cano_baixo.png");

        // Instantiate the game over screen
        gameOver = new Texture("game_over.png");

        // Instantiate the camera and viewport
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        // Whit the .getWith() and getHeight we get the screen dimensions
        screenWidth = VIRTUAL_WIDTH;
        screenHeight = VIRTUAL_HEIGHT;

        birdInitVerticalPosition = screenHeight / 2;
        hTubeMovement = screenWidth;
        spaceBetweenTubes = 300;
    }

    @Override
    public void render () {

        camera.update();

        // Clean the previous frames to safe memory
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

        // Animition of the bird inside of the array to iterate trough the array
        variation += deltaTime * 10;
        if (variation > 2) variation = 0;

        if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;

            }

        } else {

           // birdFallSpeed++;
            birdFallSpeed++;
            if (birdInitVerticalPosition > 0 || birdFallSpeed < 0)
                birdInitVerticalPosition = birdInitVerticalPosition - birdFallSpeed;

            if (gameState == 1) {

                hBackgroundMovement += deltaTime * 100;
                hTesteMovement += deltaTime * 150;

                hTubeMovement -= deltaTime * 200;

                // Check if the the touch event occurs
                if (Gdx.input.justTouched()) {
                    birdFallSpeed = -15;
                }


                // Check if the tube left the screen
                if (hTubeMovement < 0 - topTube.getWidth()) {
                    hTubeMovement = screenWidth;
                    randomSpaceBetweenTubes = random.nextInt(400) - 200;
                    goal = false;
                }

                // Increment the score
                if (hTubeMovement < 120 - topTube.getWidth()) {

                    if(!goal) {
                        score++;
                        goal = true;
                    }

                }

            } else { // Game over screen --> game state --> 2

                if (Gdx.input.justTouched()) {

                    gameState = 0;
                    score = 0;
                    birdFallSpeed = 0;
                    birdInitVerticalPosition = screenHeight / 2;
                    hTubeMovement = screenWidth;
                    hBackgroundMovement = 0;

                }

            }
        }

        // Camera data settings for projections
        batch.setProjectionMatrix( camera.combined );

        batch.begin();


        // To move the background set the scroll values on the 3rd or 4th
        batch.draw(background, 0,0, hBackgroundMovement, 0, (int) screenWidth, (int) screenHeight);
        //batch.draw(background, 0, 0, screenWidth, screenHeight); //Static Background

        batch.draw(teste, 0,0, hTesteMovement, 0, (int) screenWidth, (int) screenHeight);

        batch.draw(topTube, hTubeMovement, screenHeight / 2 + spaceBetweenTubes / 2 + randomSpaceBetweenTubes);
        batch.draw(bottomTube, hTubeMovement, screenHeight / 2 - bottomTube.getHeight() - spaceBetweenTubes / 2 + randomSpaceBetweenTubes);
        batch.draw(bird[(int) variation], 120, birdInitVerticalPosition);
        scoreFont.draw(batch, String.valueOf(score), screenWidth / 2, screenHeight - 100 );

        if ( gameState == 2 ) {

            //Draw the game over image
            batch.draw(
                    gameOver,
                    screenWidth / 2 - gameOver.getWidth() / 2,
                    screenHeight / 2 - gameOver.getHeight() / 2);

            gameOverMessageFont.draw(batch, "Touch to re-start!", screenWidth / 2 - 165 , screenHeight / 2 - 100);
        }

        batch.end();

        // Create the shapes and set the dimensions to detect collisions
        birdCircle.set(120 + bird[0].getWidth() / 2,
                birdInitVerticalPosition + bird[0].getHeight() / 2,
                bird[0].getWidth() / 2);

       topTubeRectangle = new Rectangle(hTubeMovement,
               screenHeight / 2 + spaceBetweenTubes / 2 + randomSpaceBetweenTubes,
               topTube.getWidth(),
               topTube.getHeight()
       );

        bottomTubeRectangle = new Rectangle(
                hTubeMovement,
                screenHeight / 2 - bottomTube.getHeight() - spaceBetweenTubes / 2 + randomSpaceBetweenTubes,
                bottomTube.getWidth(),
                bottomTube.getHeight()
        );

        // Draw the shapes to detect the collisions
        /*shapeRenderer.begin( ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.rect(bottomTubeRectangle.x, bottomTubeRectangle.y, bottomTubeRectangle.width, bottomTubeRectangle.height);
        shapeRenderer.rect(topTubeRectangle.x, topTubeRectangle.y, topTubeRectangle.width, topTubeRectangle.height);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();*/

        // Collision test
        if( Intersector.overlaps(birdCircle, topTubeRectangle)
                || Intersector.overlaps(birdCircle, bottomTubeRectangle)
                || birdInitVerticalPosition <= 0
                || birdInitVerticalPosition >= screenHeight) {
            //Gdx.app.log("AJDB", "Collision Course!");
            gameState = 2;
        }
    }

    // We override the mthod resize to set the viewport
    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);

    }
}
