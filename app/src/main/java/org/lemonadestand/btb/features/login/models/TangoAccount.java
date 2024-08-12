package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class TangoAccount {
    @SerializedName("accountIdentifier")
    String accountIdentifier;

    @SerializedName("accountNumber")
    String accountNumber;

    @SerializedName("displayName")
    String displayName;

    @SerializedName("currencyCode")
    String currencyCode;

    @SerializedName("currentBalance")
    int currentBalance;

    @SerializedName("createdAt")
    String createdAt;

    @SerializedName("status")
    String status;

    @SerializedName("contactEmail")
    String contactEmail;


    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }
    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public String getContactEmail() {
        return contactEmail;
    }


}
