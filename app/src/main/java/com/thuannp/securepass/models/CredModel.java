package com.thuannp.securepass.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cred_table")
public class CredModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String provider;

    private String providerName;

    private String email;

    private String cat;

    public CredModel(String provider, String providerName, String email, String cat) {
        this.provider = provider;
        this.providerName = providerName;
        this.email = email;
        this.cat = cat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCat() {
        return cat;
    }
}
