package ca.dhaliwal.spaceshooter;


import java.util.Random;

import com.badlogic.gdx.Game;


public class SpaceShooterGame extends Game{

	GameStateManager gsm;
	GameScreen gameScreen;
	TitleScreen titleScreen;
	GameOverScreen gameOverScreen;
	PauseScreen pauseScreen;

	public static Random random = new Random();
	
	@Override
	public void create() {
		gameScreen = new GameScreen();
		titleScreen = new TitleScreen(this);
		gameOverScreen = new GameOverScreen(this);
		pauseScreen = new PauseScreen(this);
		gsm = new GameStateManager(this);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
	}

	
}
