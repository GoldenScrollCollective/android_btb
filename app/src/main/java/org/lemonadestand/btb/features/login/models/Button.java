package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Button {
    @SerializedName("position")
    String position;

    @SerializedName("x")
    int x;

    @SerializedName("y")
    int y;

    @SerializedName("append")
    String append;


    public void setPosition(String position) {
        this.position = position;
    }
    public String getPosition() {
        return position;
    }

    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public void setAppend(String append) {
        this.append = append;
    }
    public String getAppend() {
        return append;
    }


}
