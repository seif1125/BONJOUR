package com.example.bonjour.AsyncTasks;

import android.os.AsyncTask;

import com.example.bonjour.Entity.User;
import com.example.bonjour.ROOMDB.DbDAO;

import java.util.List;

public class deleteUser extends AsyncTask<User,Void,Void> {
    private DbDAO dbDAO;

    public deleteUser(DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }


    @Override
    protected Void doInBackground(User... users) {
        dbDAO.delete(users);
        return null;
    }


}
