package com.rebook.automart.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("avatar_type")
    @Expose
    private String avatarType;
    @SerializedName("avatar_location")
    @Expose
    private Object avatarLocation;
    @SerializedName("password_changed_at")
    @Expose
    private Object passwordChangedAt;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("confirmation_code")
    @Expose
    private Object confirmationCode;
    @SerializedName("confirmed")
    @Expose
    private Boolean confirmed;
    @SerializedName("timezone")
    @Expose
    private Object timezone;
    @SerializedName("last_login_at")
    @Expose
    private Object lastLoginAt;
    @SerializedName("last_login_ip")
    @Expose
    private Object lastLoginIp;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("full_name")
    @Expose
    private String fullName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public Object getAvatarLocation() {
        return avatarLocation;
    }

    public void setAvatarLocation(Object avatarLocation) {
        this.avatarLocation = avatarLocation;
    }

    public Object getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(Object passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Object getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(Object confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Object getTimezone() {
        return timezone;
    }

    public void setTimezone(Object timezone) {
        this.timezone = timezone;
    }

    public Object getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Object lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Object getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(Object lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}