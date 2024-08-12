package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class AddressShipping {
    @SerializedName("street")
    String street;

    @SerializedName("street2")
    String street2;

    @SerializedName("city")
    String city;

    @SerializedName("state")
    String state;

    @SerializedName("country")
    String country;

    @SerializedName("postal")
    String postal;


    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet() {
        return street;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    public String getStreet2() {
        return street2;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }
    public String getPostal() {
        return postal;
    }

}
