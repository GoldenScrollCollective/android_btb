package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class Organization {
    @SerializedName("id")
    String id;

    @SerializedName("uniq_id")
    String uniqId;

    @SerializedName("name")
    String name;

    @SerializedName("picture")
    String picture;

    @SerializedName("timezone")
    String timezone;

    @SerializedName("widget_token")
    String widgetToken;

    @SerializedName("widget_settings")
    WidgetSettings widgetSettings;

    @SerializedName("qr_code")
    String qrCode;

    @SerializedName("user_monthly_spend")
    String userMonthlySpend;

    @SerializedName("tango_fund_threshold")
    String tangoFundThreshold;

    @SerializedName("tango_fund_amount")
    String tangoFundAmount;

    @SerializedName("tango_credit_card")
    String tangoCreditCard;

    @SerializedName("created")
    String created;

    @SerializedName("tango_account")
    TangoAccount tangoAccount;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setUniqId(String uniqId) {
        this.uniqId = uniqId;
    }
    public String getUniqId() {
        return uniqId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getPicture() {
        return picture;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    public String getTimezone() {
        return timezone;
    }

    public void setWidgetToken(String widgetToken) {
        this.widgetToken = widgetToken;
    }
    public String getWidgetToken() {
        return widgetToken;
    }

    public void setWidgetSettings(WidgetSettings widgetSettings) {
        this.widgetSettings = widgetSettings;
    }
    public WidgetSettings getWidgetSettings() {
        return widgetSettings;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public String getQrCode() {
        return qrCode;
    }

    public void setUserMonthlySpend(String userMonthlySpend) {
        this.userMonthlySpend = userMonthlySpend;
    }
    public String getUserMonthlySpend() {
        return userMonthlySpend;
    }

    public void setTangoFundThreshold(String tangoFundThreshold) {
        this.tangoFundThreshold = tangoFundThreshold;
    }
    public String getTangoFundThreshold() {
        return tangoFundThreshold;
    }

    public void setTangoFundAmount(String tangoFundAmount) {
        this.tangoFundAmount = tangoFundAmount;
    }
    public String getTangoFundAmount() {
        return tangoFundAmount;
    }

    public void setTangoCreditCard(String tangoCreditCard) {
        this.tangoCreditCard = tangoCreditCard;
    }
    public String getTangoCreditCard() {
        return tangoCreditCard;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    public String getCreated() {
        return created;
    }

    public void setTangoAccount(TangoAccount tangoAccount) {
        this.tangoAccount = tangoAccount;
    }
    public TangoAccount getTangoAccount() {
        return tangoAccount;
    }


}
