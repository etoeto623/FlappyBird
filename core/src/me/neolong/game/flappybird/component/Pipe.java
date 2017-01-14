package me.neolong.game.flappybird.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by longhai on 2017/1/10.
 */
public class Pipe {
    private final static int MAX_Y = 8;
    private final static float GAP = 3;
    public TextureRegion img;
    public Vector2 pos;
    public float height, wight;

    private Pipe(){}

    public static Pipe[] getPipes(float minY, float maxY, float x, Texture pipeUp, Texture pipeDown, float itemHeight){
        Pipe[] pipes = new Pipe[2];
        float realH = maxY-minY;
        float h = realH - realH/MAX_Y;

        for(int i = 0; i < 2; i++){
            Pipe pipe = new Pipe();
            pipe.pos = new Vector2();
            pipe.pos.x = x;
            pipe.height = h;
            pipe.wight = pipeUp.getWidth();
            if(i == 0){ // pipe up
                float len = MathUtils.random(maxY/2, h);
                pipe.pos.y = len;
                pipe.img = new TextureRegion(pipeUp);
            }else{
                pipe.pos.y = pipes[0].pos.y-GAP*itemHeight-h;
                pipe.img = new TextureRegion(pipeDown);
            }
            pipes[i] = pipe;
        }
        return pipes;
    }
}