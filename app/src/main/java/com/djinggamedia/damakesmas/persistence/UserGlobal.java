package com.djinggamedia.damakesmas.persistence;

import android.content.Context;

import com.djinggamedia.damakesmas.helper.UserSQLiteHandler;


/**
 * Created by Startup on 2/1/17.
 */

public class UserGlobal {

    private static UserSQLiteHandler db;
    public static double balance;

    public static User getUser(Context context)
    {
        db = new UserSQLiteHandler(context);
        return db.getUserDetails();

    }

    public static void setUser(Context context, User user)
    {
        db = new UserSQLiteHandler(context);
        db.updateUser(user);
    }





}
