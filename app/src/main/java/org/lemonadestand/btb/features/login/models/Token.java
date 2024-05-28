package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("iss")
    String iss;

    @SerializedName("user_id")
    String userId;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("sub")
    String sub;

    @SerializedName("iat")
    int iat;

    @SerializedName("exp")
    int exp;

    @SerializedName("raw_token")
    String rawToken;

    @SerializedName("refresh_token")
    String refreshToken;


    public void setIss(String iss) {
        this.iss = iss;
    }
    public String getIss() {
        return iss;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
    public String getSub() {
        return sub;
    }

    public void setIat(int iat) {
        this.iat = iat;
    }
    public int getIat() {
        return iat;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    public int getExp() {
        return exp;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }
    public String getRawToken() {
        return rawToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }


}
