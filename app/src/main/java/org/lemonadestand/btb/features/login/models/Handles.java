package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Handles {
    @SerializedName("linkedin")
    String linkedin;

    @SerializedName("fb")
    String fb;

    @SerializedName("twitter")
    String twitter;

    @SerializedName("instagram")
    String instagram;


    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
    public String getLinkedin() {
        return linkedin;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }
    public String getFb() {
        return fb;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
    public String getTwitter() {
        return twitter;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
    public String getInstagram() {
        return instagram;
    }

}
