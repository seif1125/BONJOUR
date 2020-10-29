package com.example.bonjour.AsyncTasks;

import android.os.AsyncTask;

import com.example.bonjour.Entity.User;
import com.example.bonjour.ROOMDB.DbDAO;

public class insertUser extends AsyncTask<User,Void,Void> {

private DbDAO dbDAO;

    public insertUser(DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    @Override
    protected Void doInBackground(User... users) {
        dbDAO.insert(users);

        return null;
    }
}
