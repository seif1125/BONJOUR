package com.example.bonjour.ROOMDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bonjour.Entity.User;

import java.util.List;

@Dao
public interface DbDAO {

    @Insert
    public void insert(User... users);


    @Query("SELECT * FROM user")
    public List<User> getUsers();
    @Query("SELECT * FROM user WHERE email = :email")
    public User getContactWithEmail(String email);

    @Query("UPDATE user SET userimage= :url WHERE email = :email")
    public void updateContactWithEmail(String email,String url);
    @Delete
    public void delete(User...users);


}
