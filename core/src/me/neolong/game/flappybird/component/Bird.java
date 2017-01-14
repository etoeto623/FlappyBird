package me.neolong.game.flappybird.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by longhai on 2017/1/9.
 */
public class Bird {
    private Array<BirdItem> frames = new Array<BirdItem>();
    public final static float interval = 0.15f;
    Animation animation;
    public Vector2 pos, velocity;

    private Bird(){}

    public static Bird getBird(BirdType type){
        Bird bird = new Bird();
        Texture temp = null;
        for(int i = 0; i < 3; i++){
            Texture img = new Texture(String.format("bird%s_%s.png",type.idx,i));
            bird.frames.add(new BirdItem(img, img.getWidth(), img.getHeight()));
            if(i == 1){
                temp = img;
            }
        }
        bird.frames.add(new BirdItem(temp, temp.getWidth(), temp.getHeight()));
        bird.animation = new Animation(0.05f, bird.frames);
        bird.animation.setPlayMode(Animation.PlayMode.LOOP);
        return bird;
    }

    public Array<BirdItem> getFrames(){
        return this.frames;
    }
    public BirdItem getKeyFrame(float stateTime){
        BirdItem item = (BirdItem)this.animation.getKeyFrame(stateTime);
        item.pos = this.pos;
        return item;
    }

    public enum BirdType{
        YELLOW(0), BLUE(1), RED(2);
        private int idx;
        private BirdType(int idx){
            this.idx = idx;
        }
    }

    public static class BirdItem extends TextureRegion{
        public BirdItem(Texture img, int width, int height){
            super(img, width, height);
        }
        public Vector2 pos;
    }
}