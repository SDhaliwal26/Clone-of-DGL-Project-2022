package ca.dhaliwal.spaceshooter;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


class PlayerShip extends Ship{

    int lives;
    boolean playerDeath;

    public PlayerShip(float xCentre, float yCentre, 
                        float width, float height, 
                        float movementSpeed, int shield,
                        float laserWidth, float laserHeight, 
                        float laserMovementSpeed, float timeBetweenShots,
                        TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion, 
                        TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed,
                timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives = 3;
        playerDeath = false;
        
    }

    @Override
    public Laser[] firelaser() {
        Laser[] laser = new Laser[4];
        laser[0] = new Laser(boundingBox.x+boundingBox.width*0.72f,
                            boundingBox.y+boundingBox.height*0.65f,
                            laserWidth, laserHeight,
                            laserMovementSpeed, laserTextureRegion);
        laser[1] = new Laser(boundingBox.x+boundingBox.width*0.80f,
                            boundingBox.y+boundingBox.height*0.31f,
                            laserWidth, laserHeight,
                            laserMovementSpeed, laserTextureRegion);
        laser[2] = new Laser(boundingBox.x+boundingBox.width*0.27f,
                            boundingBox.y+boundingBox.height*0.67f,
                            laserWidth, laserHeight,
                            laserMovementSpeed, laserTextureRegion);
        laser[3] = new Laser(boundingBox.x+boundingBox.width*0.14f,
                            boundingBox.y+boundingBox.height*0.33f,
                            laserWidth, laserHeight,
                            laserMovementSpeed, laserTextureRegion);
        timeSinceLastShot = 0;

        return laser;
    }


    @Override
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if (shield > 0){
            batch.setColor(Color.GREEN);
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
            batch.setColor(Color.WHITE);
        }
    }
    
}
