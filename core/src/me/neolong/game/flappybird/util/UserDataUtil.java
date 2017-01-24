package me.neolong.game.flappybird.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by longhai on 2017/1/24.
 */
public class UserDataUtil {
    private static FileHandle fileHandle = Gdx.files.internal("data.properties");

    public static int getMaxScore(){
        Properties properties = new Properties();
        FileReader fr = null;
        try {
            fr = new FileReader(fileHandle.file());
            properties.load(fr);
            Object score = properties.get("maxScore");
            if(null != score){
                return Integer.valueOf(score.toString().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != fr){
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public static void setMaxScore(int score){
        Properties properties = new Properties();
        FileReader fr = null;
        FileWriter fw = null;
        try{
            fr = new FileReader(fileHandle.file());
            properties.load(fr);
            fr.close();
            properties.setProperty("maxScore", String.valueOf(score));
            fw = new FileWriter(fileHandle.file());
            properties.store(fw, "");
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}