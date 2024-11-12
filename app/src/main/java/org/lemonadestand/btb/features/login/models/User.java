package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {
    @SerializedName("id")
    int id;

    @SerializedName("uniq_id")
    String uniqId;

    @SerializedName("org_id")
    String orgId;

    @SerializedName("username")
    String username;

    @SerializedName("status")
    String status;

    @SerializedName("status_message")
    String statusMessage;

    @SerializedName("active")
    boolean active;

    @SerializedName("last_active")
    LastActive lastActive;

    @SerializedName("created_at")
    CreatedAt createdAt;

    @SerializedName("updated_at")
    UpdatedAt updatedAt;

    @SerializedName("deleted_at")
    String deletedAt;

    @SerializedName("name")
    String name;

    @SerializedName("picture")
    String picture;

    @SerializedName("phone")
    String phone;

    @SerializedName("dob")
    String dob;

    @SerializedName("address")
    Address address;

    @SerializedName("address_shipping")
    AddressShipping addressShipping;

    @SerializedName("title")
    String title;

    @SerializedName("handles")
    Handles handles;

    @SerializedName("public")
    String public1;

    @SerializedName("give")
    String give;

    @SerializedName("spend")
    String spend;

    @SerializedName("user_hash")
    String userHash;

    @SerializedName("groups")
    List<String> groups;

    @SerializedName("permissions")
    List<String> permissions;

    @SerializedName("organization")
    Organization organization;

    @SerializedName("token")
    Token token;


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setUniqueId(String uniqId) {
        this.uniqId = uniqId;
    }
    public String getUniqueId() {
        return uniqId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgId() {
        return orgId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean getActive() {
        return active;
    }

    public void setLastActive(LastActive lastActive) {
        this.lastActive = lastActive;
    }
    public LastActive getLastActive() {
        return lastActive;
    }

    public void setCreatedAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
    }
    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
    }
    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
    public String getDeletedAt() {
        return deletedAt;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String getShortName() {
        String[] splitedNames = this.name.split(" ");
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < Math.min(splitedNames.length, 2); i++) {
            names.add(splitedNames[i].substring(0, 1).toUpperCase());
        }
        return String.join("", names);
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getPicture() {
        return picture;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    public String getDob() {
        return dob;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    public Address getAddress() {
        return address;
    }

    public void setAddressShipping(AddressShipping addressShipping) {
        this.addressShipping = addressShipping;
    }
    public AddressShipping getAddressShipping() {
        return addressShipping;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setHandles(Handles handles) {
        this.handles = handles;
    }
    public Handles getHandles() {
        return handles;
    }

    public void setPublic(String public1) {
        this.public1 = public1;
    }
    public String getPublic() {
        return public1;
    }

    public void setGive(String give) {
        this.give = give;
    }
    public String getGive() {
        return give;
    }

    public void setSpend(String spend) {
        this.spend = spend;
    }
    public String getSpend() {
        return spend;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }
    public String getUserHash() {
        return userHash;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
    public List<String> getGroups() {
        return groups;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    public List<String> getPermissions() {
        return permissions;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    public Organization getOrganization() {
        return organization;
    }

    public void setToken(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }


}
