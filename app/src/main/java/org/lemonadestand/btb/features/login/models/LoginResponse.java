package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    boolean status;

    @SerializedName("message")
    String message;

    @SerializedName("user")
    User user;


    public void setStatus(boolean status) {
        this.status = status;
    }
    public boolean getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

}
