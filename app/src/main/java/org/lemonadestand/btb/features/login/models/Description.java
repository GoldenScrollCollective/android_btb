package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Description {
    @SerializedName("type")
    String type;

    @SerializedName("content")
    List<Content> content;


    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }
    public List<Content> getContent() {
        return content;
    }


}
