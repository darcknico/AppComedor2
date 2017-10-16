package com.example.aldebaran.appcomedor.utils;

import com.example.aldebaran.appcomedor.MainActivity;

/**
 * Created by karen on 15/10/2017.
 */

public class Singleton {

    public static Singleton singleton = null;
    public MainActivity mainActivity;
    public int ultimoFragment;

    public Singleton(){}

    public static Singleton getInstance(){
        if(singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public int getUltimoFragment() {
        return ultimoFragment;
    }

    public void setUltimoFragment(int ultimoFragment) {
        this.ultimoFragment = ultimoFragment;
    }
}
