package ca.dhaliwal.spaceshooter;

import java.util.HashMap;

import com.badlogic.gdx.Screen;

public class GameStateManager {
    
    private final SpaceShooterGame game; //call the SpaceShooterGame application

    private HashMap<STATE, Screen> gameScreens = new HashMap<STATE, Screen>(); //store the game screens in a hash table for quick access

    public enum STATE{ //enum State refernced the game state
        MAIN_MENU, 
        PLAY,
        PAUSED,
        GAMEOVER
    }


    public GameStateManager(final SpaceShooterGame game){ //contructor for GSM with the SpaceShooterGame as it's parameter
        this.game = game; 

        initGameScreens(); // call the init game screens method
        setScreen(STATE.MAIN_MENU); //set the first screen for the SpaceShooterGame class to the title Screen
    }

    protected void initGameScreens(){ //method to initilize the game screens
         // initalized the Hash table to store each screen
        gameScreens.put(STATE.MAIN_MENU, new TitleScreen(game)); // main menu = TitleScreen class
        gameScreens.put(STATE.PLAY, new GameScreen()); // play = GameScreen
        gameScreens.put(STATE.PAUSED, new PauseScreen(game)); // Paused = PausedScreen
        gameScreens.put(STATE.GAMEOVER, new GameOverScreen(game));
    }

    public void setScreen(STATE nextScreen){ //method to switch screens
        game.setScreen(gameScreens.get(nextScreen)); // will get the screen in the hash table and set it as the screen to display
    }

    public void removeScreen(STATE screen){ //method to remove screen, used for new game
        gameScreens.remove(screen); //remove screen from hash table
    }

    public void dispose(){ //dispose the screens as needed
        for (Screen screen: gameScreens.values()){
            if (screen != null){
                screen.dispose();
            }
        }
    }
}