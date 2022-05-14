package me.neolong.game.flappybird.component;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by longhai on 2017/1/25.
 */
public abstract class Button {
    public TextureRegion img;

    public boolean isClicked(int x, int y){
        int minX = img.getRegionX();
        int minY = img.getRegionY();
        int maxX = minX + img.getRegionWidth();
        int maxY = minY + img.getRegionHeight();
        return x>=minX && x<=maxX && y>=minY && y<=maxY;
    }
    public abstract void doClick();
}