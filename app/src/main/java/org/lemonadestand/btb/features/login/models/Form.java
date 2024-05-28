package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Form {
    @SerializedName("emailTo")
    String emailTo;

    @SerializedName("thankYouMessage")
    ThankYouMessage thankYouMessage;


    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }
    public String getEmailTo() {
        return emailTo;
    }

    public void setThankYouMessage(ThankYouMessage thankYouMessage) {
        this.thankYouMessage = thankYouMessage;
    }
    public ThankYouMessage getThankYouMessage() {
        return thankYouMessage;
    }


}
