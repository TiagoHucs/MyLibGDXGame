package com.mygdx.game.basic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Ator extends Rectangle {

    public Texture getImg() {
        return img;
    }

    public Texture img;

    public Ator(String textureFileName) {
        this.img = new Texture(textureFileName);
        this.x = 50;
        this.y = 50;
        this.width = 40;
        this.height = 40;
    }
}
