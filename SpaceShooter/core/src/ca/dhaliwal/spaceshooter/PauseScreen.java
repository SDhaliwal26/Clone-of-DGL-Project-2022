package ca.dhaliwal.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import ca.dhaliwal.spaceshooter.GameStateManager.STATE;

public class PauseScreen implements Screen {
    SpaceShooterGame game;
    final TextButton mainMenuButton;
    final TextButton resumeButton;
    private float screen_width = ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.WORLD_WIDTH;
    private float screen_height = ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.WORLD_HEIGHT;
    private OrthographicCamera camera;
    private Sound hover;
    private Sound select;
    Stage stage;
    BitmapFont font;

    protected PauseScreen(SpaceShooterGame game) {
        camera = new OrthographicCamera();
        stage = new Stage(new StretchViewport(screen_width, screen_height, camera));
        font = new BitmapFont(Gdx.files.internal("FutileHero.fnt"));

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.YELLOW;

        mainMenuButton = new TextButton("Return To Main Menu", buttonStyle);
        resumeButton = new TextButton("Resume", buttonStyle);

        resumeButton.setWidth(screen_width / 4);
        resumeButton.setHeight(screen_height / 4);
        resumeButton.setPosition(screen_width / 2.55f, screen_height / 3);

        mainMenuButton.setWidth(screen_width / 4);
        mainMenuButton.setHeight(screen_height / 4);
        mainMenuButton.setPosition(screen_width / 2.5f, screen_height / 8);

        hover = Gdx.audio.newSound(Gdx.files.internal("Blip_Select.wav"));
        select = Gdx.audio.newSound(Gdx.files.internal("Pickup_Coin2.wav"));

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                select.play();
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.PLAY);

            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                select.play();
                ((SpaceShooterGame) Gdx.app.getApplicationListener()).gsm.setScreen(STATE.MAIN_MENU);

            }
        });

        stage.addActor(resumeButton);
        stage.addActor(mainMenuButton);

    }

    public void update(float deltaTime) {
        stage.act(deltaTime);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        final SequenceAction blink = Actions.sequence();
        final SequenceAction blink2 = Actions.sequence();
        
        for (float i = 0; i < 1f; i += 0.1) {
            blink.addAction(Actions.alpha(i));
            blink2.addAction(Actions.alpha(i));
        }

        resumeButton.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hover.play();
                resumeButton.addAction(Actions.forever(blink));
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                resumeButton.addAction(Actions.forever(Actions.alpha(1)));
            }
                
        });

        mainMenuButton.addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                hover.play();
                mainMenuButton.addAction(Actions.forever(blink2));
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                mainMenuButton.addAction(Actions.forever(Actions.alpha(1)));
            }
                
        });};

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch.begin();
        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.renderBackground(deltaTime);
        font.setColor(Color.YELLOW);
        font.draw(((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch, "PAUSED",
                screen_width / 2.55f, screen_height / 1.25f);

        ((SpaceShooterGame) Gdx.app.getApplicationListener()).gameScreen.batch.end();
        stage.draw();
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
