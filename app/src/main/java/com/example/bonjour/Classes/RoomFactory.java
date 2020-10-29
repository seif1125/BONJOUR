package com.example.bonjour.Classes;

import android.content.Context;

import androidx.room.Room;

import com.example.bonjour.ROOMDB.RoomAppDatabase;

public class RoomFactory {
public static RoomAppDatabase mRoomAppDatabase;

  public static RoomAppDatabase getDB(Context context){

        if(mRoomAppDatabase==null){

            mRoomAppDatabase= Room.databaseBuilder(context,RoomAppDatabase.class,"User_db").build();
        }


        return mRoomAppDatabase;
    }
}
