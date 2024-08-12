package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Items {
    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    @SerializedName("icon")
    String icon;


    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIcon() {
        return icon;
    }


}
