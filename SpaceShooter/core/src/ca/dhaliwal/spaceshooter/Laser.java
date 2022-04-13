package ca.dhaliwal.spaceshooter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Laser {

    // position and dimensions
    Rectangle boundingBox;

    // graphics
    TextureRegion textureRegion;

    // laser physical characteristics
    float movementSpeed; // world units per second

    public Laser(float xCentre, float yBottom, float width, float height, 
                float movementSpeed, TextureRegion textureRegion) {
        

        this.boundingBox = new Rectangle(xCentre - width/2, yBottom, width, height);

        this.textureRegion = textureRegion;
        this.movementSpeed = movementSpeed;
    }

    public void draw(Batch batch){
        batch.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

  //  public Rectangle getBoundingBox(){
  //      return boundingBox;
  //  } 

}
