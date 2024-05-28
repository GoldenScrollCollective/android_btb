package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class CreatedAt {
    @SerializedName("date")
    String date;

    @SerializedName("timezone_type")
    int timezoneType;

    @SerializedName("timezone")
    String timezone;


    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public void setTimezoneType(int timezoneType) {
        this.timezoneType = timezoneType;
    }
    public int getTimezoneType() {
        return timezoneType;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    public String getTimezone() {
        return timezone;
    }

}
