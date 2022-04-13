package ca.dhaliwal.spaceshooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class EnemyShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 0.75f;
    
    public EnemyShip(float xCentre, float yCentre, 
                        float width, float height, 
                        float movementSpeed, int shield,
                        float laserWidth, float laserHeight, 
                        float laserMovementSpeed, float timeBetweenShots,
                        TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion, 
                        TextureRegion laserTextureRegion) {
        super(xCentre, yCentre, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed,
                timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        
        directionVector = new Vector2(0, -1);
        
    }

    

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector(){
        double bearing = SpaceShooterGame.random.nextDouble()*2*Math.PI;
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency){
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Laser[] firelaser() {
        Laser[] laser = new Laser[1];
        laser[0] = new Laser(boundingBox.x+boundingBox.width*0.52f,
                            boundingBox.y+boundingBox.height*0.10f,
                            laserWidth, laserHeight,
                            laserMovementSpeed, laserTextureRegion);
        timeSinceLastShot = 0;
        
        return laser;
    }
    @Override
    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if (shield > 0){
            batch.setColor(Color.RED);
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y-boundingBox.height*0.2f, boundingBox.width, boundingBox.height);
            batch.setColor(Color.WHITE);
        }
    }
}
