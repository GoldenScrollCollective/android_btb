package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Values {
    @SerializedName("description")
    Description description;

    @SerializedName("items")
    List<Items> items;


    public void setDescription(Description description) {
        this.description = description;
    }
    public Description getDescription() {
        return description;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }
    public List<Items> getItems() {
        return items;
    }

}
