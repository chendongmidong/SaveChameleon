package com.example.SaveChameleon;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by zhao on 2016/4/15.
 */
public class Animal {

    public float x, y;
//    float rotation;
    public float speed;
//    float rotationSpeed;
    public int width, height;
    public Bitmap bitmap;

    /**
     * Creates a new droidanimal in the given xRange and with the given bitmap. Parameters of
     * location, size, rotation, and speed are randomly determined.
     */
    public static Animal createAnimal(int width,int height,float x,float y,Bitmap originalBitmap) {

        Animal animal = new Animal();
        // Size each animal with a width between 5 and 55 and a proportional height
        animal.width = width;
        animal.height = height;

        // Position the animal horizontally between the left and right of the range
        animal.x = x;
        // Position the animal vertically slightly off the top of the display
        animal.y = y;

        // Each animal travels at 50-200 pixels per second
        animal.speed = 50 ;

        // Animals start at -90 to 90 degrees rotation, and rotate between -45 and 45
        // degrees per second
//        animal.rotation = (float) Math.random() * 180 - 90;
//        animal.rotationSpeed = (float) Math.random() * 90 - 45;

        // Get the cached bitmap for this size if it exists, otherwise create and cache one
        if (animal.bitmap == null) {
            animal.bitmap = Bitmap.createScaledBitmap(originalBitmap,
                    (int)animal.width, (int)animal.height, true);
        }
        return animal;
    }
    public void setX(float value) {
        this.x = value;
    }
    public float getX() {
        return x;
    }
    public void setY(float value) {
        this.y = value;
    }
    public float getY() {
        return y;
    }
}
