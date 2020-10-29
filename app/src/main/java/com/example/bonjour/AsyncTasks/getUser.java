package com.example.bonjour.AsyncTasks;

import android.os.AsyncTask;

import com.example.bonjour.Entity.User;
import com.example.bonjour.ROOMDB.DbDAO;

import java.util.List;

public class getUser extends AsyncTask<Void,Void, List<User>> {

    private DbDAO dbDAO;

    public getUser(DbDAO dbDAO) {
        this.dbDAO = dbDAO;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {

        return  dbDAO.getUsers();
    }
}
