package com.example.bonjour.AsyncTasks;

import android.os.AsyncTask;

import com.example.bonjour.Entity.User;
import com.example.bonjour.ROOMDB.DbDAO;

public class retreivesingleuser extends AsyncTask<String,Void, User> {
    private DbDAO dbDAO;

    public retreivesingleuser(DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    @Override
    protected User doInBackground(String... strings) {
        return dbDAO.getContactWithEmail(strings[0]);
    }
}
