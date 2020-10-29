package com.example.bonjour.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @NonNull
    private String password;
    @NonNull
    private String userimage;
    @PrimaryKey
    @NonNull
    private String email;

    public User(@NonNull String password, @NonNull String userimage, @NonNull String email) {
        this.password = password;
        this.userimage = userimage;
        this.email = email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @NonNull
    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(@NonNull String userimage) {
        this.userimage = userimage;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }
}


