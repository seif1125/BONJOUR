package com.example.bonjour.AsyncTasks;

import android.os.AsyncTask;

import com.example.bonjour.ROOMDB.DbDAO;

public class updateUser extends AsyncTask<String,Void,Void> {
    private DbDAO dbDAO;

    public updateUser(DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }
    @Override
    protected Void doInBackground(String... strings) {
        dbDAO.updateContactWithEmail(strings[0],strings[1]);
        return null;
    }
}
