package ca.dhaliwal.spaceshooter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import ca.dhaliwal.spaceshooter.GameStateManager.STATE;

public class GameScreen implements Screen {

    // screen
    private Stage stage;
    private Camera camera;
    Skin skin;
    SpaceShooterGame game;

    // graphics
    SpriteBatch batch; // renderes all sprites in order
    private TextureAtlas textureAtlas;
    protected TextureAtlas buttonAtlas;
    private Texture enemyExplosion;
    private Texture playerExplosion;
    // private Texture background;
    private TextureRegion[] backgrounds;

    protected TextureRegion playerShipTextureRegion, playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;

    // timing
    // private int backgroundOffset;
    private float[] backgroundOffsets = { 0, 0, 0, 0 };
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 3f;
    private float enemySpawnTimer = 0;

    // world parameters
    protected final float WORLD_WIDTH = 500;
    protected final float WORLD_HEIGHT = 500; // constants that wont't change
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    // game objects
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;

    // Player score/death
    private int score = 0;
    private boolean playerDeath;

    // Heads-Up Display
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;

    // Sounds/Music
    private Sound enemyExplosionSound;
    private Sound playerExplosionSound;
    private Sound enemyLaserSound;
    private Sound playerLaserSound;
    private Sound playerHitSound;
    private Sound enemyHitSound;
    private Sound lifeUp;
    private Music music;
    int lifeTime;

    GameScreen() { // Setup the constructor for the game screen
        camera = new OrthographicCamera(); // can't see what's behind it, 2D camera
        stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)); // what the user sees

        // set up the texture atlas
        textureAtlas = new TextureAtlas("images.atlas");
        buttonAtlas = new TextureAtlas("menuButtons.atlas");
        skin = new Skin(buttonAtlas);

        // background = new Texture(Gdx.files.internal("SpaceBackground.png"));
        // backgroundOffset = 0; // for scrolling background

        // setting up the background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("SpaceBackground");
        backgrounds[1] = textureAtlas.findRegion("SpaceBackground2");
        backgrounds[2] = textureAtlas.findRegion("SpaceBackground3");
        backgrounds[3] = textureAtlas.findRegion("SpaceBackground4");

        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        // initialize texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip1_red");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed2");
        playerShieldTextureRegion = textureAtlas.findRegion("shield3");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false, true);
        playerLaserTextureRegion = textureAtlas.findRegion("laserGreen10");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed14");
        enemyExplosion = new Texture(Gdx.files.internal("enemyexplosions.png"));
        playerExplosion = new Texture(Gdx.files.internal("playerexplosions.png"));

        // Setting up the pause button
        TextButtonStyle buttonStyle = new TextButtonStyle();
        font = new BitmapFont(Gdx.files.internal("FutileHero.fnt"));
        buttonStyle.up = skin.getDrawable("Pause.Square.Button");
        buttonStyle.down = skin.getDrawable("Pause.Square.Button");
        buttonStyle.font = font;

        final TextButton pauseButton = new TextButton("", buttonStyle);

        pauseButton.setWidth(WORLD_WIDTH / 10);
        pauseButton.setHeight(WORLD_HEIGHT / 10);
        pauseButton.setPosition(WORLD_WIDTH * 2.6f / 3, WORLD_HEIGHT * 2.4f / 3);

        pauseButton.setColor(10, 10, 10, 0.5f);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (music.isPlaying()) {
                    music.pause();
                }
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.PAUSED);
            }
        });

        // set up game objects
        playerShip = new PlayerShip(WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
                50, 50,
                100, 10,
                3f, 10, 100, 0.8f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        explosionList = new LinkedList<>();

        // setting up sounds
        enemyExplosionSound = Gdx.audio.newSound(Gdx.files.internal("enemyBoom.wav"));
        playerExplosionSound = Gdx.audio.newSound(Gdx.files.internal("playerBoom.wav"));
        enemyLaserSound = Gdx.audio.newSound(Gdx.files.internal("enemyShoot.wav"));
        playerLaserSound = Gdx.audio.newSound(Gdx.files.internal("playerShoot.wav"));
        enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("enemyHurt.wav"));
        playerHitSound = Gdx.audio.newSound(Gdx.files.internal("playerHurt.wav"));
        lifeUp = Gdx.audio.newSound(Gdx.files.internal("lifeUp.wav"));

        // setting up music
        music = Gdx.audio.newMusic(Gdx.files.internal("unprepared.ogg"));

        batch = new SpriteBatch();
        stage.addActor(pauseButton);
        playerDeath = false;

        prepareHUD();
    }

    private void prepareHUD() {
        // Create a BitmapFont from our font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("FutilePro.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.5f);
        fontParameter.borderColor = new Color(0, 0, 0, 0.3f);

        font = fontGenerator.generateFont(fontParameter);

        // scale the font to fit world space
        font.getData().setScale(0.25f);

        // calculate hud margins. etc.
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    private void scoreChange(float deltaTime) {
        if (score == 0) {
            return;
        }

        if (score % 1000 == 0 && lifeTime % 240 == 0) {
            lifeUp.play(1f, SpaceShooterGame.random.nextFloat(), SpaceShooterGame.random.nextFloat());
            playerShip.lives++;
            playerShip.shield ++;
            playerShip.movementSpeed += SpaceShooterGame.random.nextFloat();
            if (playerShip.timeBetweenShots > 0.4f){
                playerShip.timeBetweenShots -= SpaceShooterGame.random.nextFloat();
            }
            else{
                playerShip.timeBetweenShots += 0.4f;
            }
            lifeTime = 0;
            return;
        }
    }

    @Override
    public void render(float deltaTime) { // How much time since render cycle
        batch.begin();
        // scrolling background
        renderBackground(deltaTime);
        music.setLooping(true);
        music.setVolume(0.125f);
        music.play();

        lifeTime++;
        scoreChange(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);
        }
        playerShip.draw(batch);

        // lasers
        renderLasers(deltaTime);

        // detect collisions between lasers and ships
        detectCollisions(deltaTime);

        // explosions
        updateAndRenderExplosions(deltaTime);

        // hud rendering
        updateAndRenderHUD();

        respawn();

        gameOver();

        batch.end();

        stage.draw();

    }

    private void updateAndRenderHUD() {
        // render top row labels
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);
        // render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth,
                Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCentreX, hudRow2Y,
                hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y,
                hudSectionWidth, Align.right, false);
    }

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;

        if (enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyShipList
                    .add(new EnemyShip(SpaceShooterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5, WORLD_HEIGHT - 5,
                            50, 50,
                            100, 5,
                            3.0f, 10, 70, 1.0f,
                            enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
            if (score % 200 == 0 && score > 0) {
                enemyShipList.add(new EnemyShip(SpaceShooterGame.random.nextFloat() * (WORLD_WIDTH - 10) + 5, WORLD_HEIGHT - 5,
                50, 50,
                1000, (5 + (int) SpaceShooterGame.random.nextFloat()),
                10f, 200, 500, SpaceShooterGame.random.nextFloat(),
                enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
                lifeTime = 0;
            }
            enemySpawnTimer -= timeBetweenEnemySpawns;

        }
    }

    private void detectInput(float deltaTime) {
        // keyboard input

        // strategy: determine the max distance the ship can move
        // check each possible keystroke that matters and move accordingly

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float) WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.getApplicationListener().pause();
            if (music.isPlaying()) {
                music.pause();
            }
            ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.PAUSED);
        }
        // touch input (also mouse)
        if (Gdx.input.isTouched() && !Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            // get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            // convert to world parameters
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = stage.getViewport().unproject(touchPoint);

            // calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height / 2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {

                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                // scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0)
                    xMove = Math.min(xMove, rightLimit);
                else
                    xMove = Math.max(xMove, leftLimit);

                if (yMove > 0)
                    yMove = Math.min(yMove, upLimit);
                else
                    yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);

            }

        }
    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        // strategy: determine the max distance the ship can move

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) WORLD_HEIGHT / 2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0)
            xMove = Math.min(xMove, rightLimit);
        else
            xMove = Math.max(xMove, leftLimit);

        if (yMove > 0)
            yMove = Math.min(yMove, upLimit);
        else
            yMove = Math.max(yMove, downLimit);
        if (enemyShip.boundingBox.x == 0)
            Math.abs(xMove);
        else if (enemyShip.boundingBox.x < 0)
            xMove = -xMove;

        enemyShip.translate(xMove, yMove);
    }

    private void detectCollisions(float deltaTime) {
        // for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();

            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    // contact with enemy ship
                    if (enemyShip.hitAndCheckDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(enemyExplosion,
                                new Rectangle(enemyShip.boundingBox),
                                1f));
                        enemyExplosionSound.play();
                        score += 100;
                    }
                    enemyHitSound.play();
                    laserListIterator.remove();
                    break;
                }
            }
        }

        // for each enemy laser, check whether it intersects the player ship
        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                // contact with player ship
                if (playerShip.hitAndCheckDestroyed(laser)) {
                    explosionList.add(new Explosion(playerExplosion,
                            new Rectangle(playerShip.boundingBox),
                            2f));
                    playerExplosionSound.play();
                    playerShip.translate(WORLD_WIDTH * 2, WORLD_HEIGHT * 2);
                    playerShip.lives--;
                    playerShip.timeSinceLastShot = -3;
                    playerDeath = true;
                }
                playerHitSound.play();
                laserListIterator.remove();
            }

        }

    }

    private void respawn() {
        if (playerDeath == true && playerShip.timeSinceLastShot >= 0) {
            playerShip.translate(-WORLD_WIDTH * 2, -WORLD_HEIGHT * 2);
            playerShip.shield = 10;
            playerShip.movementSpeed = 100;
            playerShip.timeBetweenShots = 0.8f;
            playerDeath = false;
        }
    }

    private void gameOver() {
        if (playerShip.lives < 0) {
            if (music.isPlaying()) {
                music.stop();
            }
            ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.GAMEOVER);
        }
    }

    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float deltaTime) {
        // create new lasers
        // player lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.firelaser();
            for (Laser laser : lasers) {
                playerLaserList.add(laser);
                playerLaserSound.play(0.25f);
            }
        }
        // enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.firelaser();
                enemyLaserList.addAll(Arrays.asList(lasers));
                enemyLaserSound.play(0.5f);
            }
        }

        // draw lasers
        // remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            batch.setColor(Color.YELLOW);
            laser.draw(batch);
            batch.setColor(Color.WHITE);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    protected void renderBackground(float deltaTime) {

        // update position of background images
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += deltaTime * playerShip.movementSpeed;

        // draw each background layer
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer], WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], 0, -backgroundOffsets[layer] + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);

        }

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // camera centered true
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }
}
