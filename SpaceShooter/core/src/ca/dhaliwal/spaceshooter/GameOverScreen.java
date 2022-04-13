package ca.dhaliwal.spaceshooter;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import ca.dhaliwal.spaceshooter.GameStateManager.STATE;

public class GameOverScreen implements Screen {
    private float screen_width = ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.WORLD_WIDTH;
    private float screen_height = ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.WORLD_HEIGHT;
    final Label GAMEOVER;
    final TextButton retryButton;
    final TextButton exitButton;

    private OrthographicCamera camera;
    SpaceShooterGame game;
    Stage stage;
    BitmapFont font;

    public GameOverScreen(SpaceShooterGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        stage = new Stage(new StretchViewport(screen_width,screen_height, camera));
        font = new BitmapFont(Gdx.files.internal("FutileHero.fnt"));
        TextButtonStyle buttonStyle = new TextButtonStyle();
        LabelStyle labelStyle = new LabelStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.YELLOW;
        labelStyle.font = font;
        labelStyle.fontColor = Color.RED;

        GAMEOVER = new Label("GAME OVER", labelStyle);
        retryButton = new TextButton("RETRY", buttonStyle);
        exitButton = new TextButton("RETURN TO MAIN MENU", buttonStyle);

        

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.removeScreen(STATE.PLAY);
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.initGameScreens();
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.PLAY);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.MAIN_MENU);
            }
        });

        stage.addActor(retryButton);
        stage.addActor(exitButton);
        stage.addActor(GAMEOVER);

    }


    public void update(float deltaTime){
        stage.act(deltaTime);
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch.begin();
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.renderBackground(deltaTime);
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch.end();
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        GAMEOVER.setPosition(screen_width / 3.5f, screen_height / 1.4f);
        GAMEOVER.setFontScale(1.5f);
        
        retryButton.setWidth(screen_width / 4);
        retryButton.setHeight(screen_height / 4);
        retryButton.setPosition(screen_width / 2.5f, screen_height / 3f);

        exitButton.setWidth(screen_width / 4);
        exitButton.setHeight(screen_height / 4);
        exitButton.setPosition(screen_width / 2.5f, screen_height / 6f);
        

        GAMEOVER.addAction(alpha(0f));
        retryButton.addAction(alpha(0f));
        exitButton.addAction(alpha(0f));
        
        GAMEOVER.addAction(fadeIn(3f));
        retryButton.addAction(fadeIn(10f));
        exitButton.addAction(fadeIn(15f));
        
        
        
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // camera centered true
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

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
        stage.dispose();
    }
}