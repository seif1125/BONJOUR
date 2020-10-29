package com.example.bonjour.ROOMDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bonjour.Entity.User;

@Database(entities = {User.class}, version = 1,exportSchema = false)
public abstract class RoomAppDatabase extends RoomDatabase {
    public abstract DbDAO getDbDAO();
}


