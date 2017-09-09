package com.fish.sardine.sardine;

import java.io.Serializable;

/**
 * Created by GD on 9/9/2017.
 */

public class FishClass implements Serializable{
    String eng,mal,img,price;

    @Override
    public String toString() {
        return eng + " " + mal + " " + img + " " + price;
    }
}
