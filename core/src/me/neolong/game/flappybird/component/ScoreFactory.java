package me.neolong.game.flappybird.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by longhai on 2017/1/15.
 */
public class ScoreFactory {
    private static Texture[] numberImgs = new Texture[]{
        new Texture("number_score_00.png"),
        new Texture("number_score_01.png"),
        new Texture("number_score_02.png"),
        new Texture("number_score_03.png"),
        new Texture("number_score_04.png"),
        new Texture("number_score_05.png"),
        new Texture("number_score_06.png"),
        new Texture("number_score_07.png"),
        new Texture("number_score_08.png"),
        new Texture("number_score_09.png")
    };

    private static Score cachedScore;
    private static int _score = -1;
    private static float _x = -1;
    private static float _y = -1;

    public static void dispose(){
        for(Texture t : numberImgs){
            t.dispose();
        }
    }

    public static Score getScore(int score, Vector2 center){
        if(score != _score || _x!=center.x || _y!=center.y){
            cachedScore = new Score(score, center, new Rectangle(0,0,numberImgs[0].getWidth(),numberImgs[0].getHeight()));
            _score = score;
            _x = center.x;
            _y = center.y;
        }
        return cachedScore;
    }

    public static class Score{
        public Vector2 center;
        public Rectangle size;
        public Array<Texture> numbers = new Array<Texture>();

        public Score(int score, Vector2 center, Rectangle size){
            this.center = center;
            this.size = size;
            if(score < 10){
                this.numbers.add(numberImgs[score]);
            }else{
                while(score>0){
                    int num = score%10;
                    score = (score-num)/10;
                    this.numbers.add(numberImgs[num]);
                }
            }
        }

        private Rectangle _rect = new Rectangle();
        public void draw(SpriteBatch batch){
            float y = center.y-size.height;
            float x = center.x-numbers.size*size.width/2;
            float offset = 0;
            for(int i = numbers.size-1; i >= 0; i--){
                batch.draw(numbers.get(i), x+offset, y, size.width, size.height);
                offset += size.width;
            }
        }
    }
}