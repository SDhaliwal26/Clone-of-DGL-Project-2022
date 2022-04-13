package ca.dhaliwal.spaceshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Explosion {
    
    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;

    private Rectangle boundingBox;

    Explosion(Texture texture, Rectangle boundingBox, float totalAnimationTime) {
        this.boundingBox = boundingBox;
        int explosionSize = texture.getWidth()/texture.getHeight();
        
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < explosionSize; i++) {
            frames.add(new TextureRegion(texture, 
                    i * texture.getHeight(), 
                    0, 
                    texture.getHeight(),
                    texture.getHeight()));
        }

        explosionAnimation = new Animation<TextureRegion>(totalAnimationTime/explosionSize, frames);
        explosionTimer = 0;
    }

    public void update(float deltaTime) {
        explosionTimer += deltaTime;
    }

    public void draw (SpriteBatch batch) {
        batch.draw(explosionAnimation.getKeyFrame(explosionTimer), 
                    boundingBox.x,
                    boundingBox.y,
                    boundingBox.width,
                    boundingBox.height);
    }

    public boolean isFinished() {
        return explosionAnimation.isAnimationFinished(explosionTimer);
    }

    
}
