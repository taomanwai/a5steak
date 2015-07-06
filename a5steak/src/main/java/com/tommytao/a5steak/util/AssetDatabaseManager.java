package com.tommytao.a5steak.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.HashMap;

public class AssetDatabaseManager extends Foundation {

    private static AssetDatabaseManager instance;

    public static AssetDatabaseManager getInstance() {

        if (instance == null)
            instance = new AssetDatabaseManager();

        return instance;
    }

    private AssetDatabaseManager() {

    }


    // --

    public interface Listener {

        public void onComplete(Cursor cursor);

    }

    private static final int DATABASE_VERSION = 1;


    private HashMap<String, SQLiteDatabase> hashMapSqliteDatabase = new HashMap<String, SQLiteDatabase>();


    private SQLiteDatabase getDatabase(String databaseName) {

        if (hashMapSqliteDatabase.containsKey(databaseName)){
            return hashMapSqliteDatabase.get(databaseName);
        }

        SQLiteDatabase database = new SQLiteAssetHelper(appContext, databaseName, null, DATABASE_VERSION).getReadableDatabase();

        hashMapSqliteDatabase.put(databaseName, database);

        return database;


    }

    public void query(String databaseName, final String query, final String[] selectionArgs, final Listener listener){

        if (null == listener)
            return;

        final SQLiteDatabase database = getDatabase(databaseName);

        new Thread() {
            @Override
            public void run() {
                super.run();

                final Cursor cursor = database.rawQuery(query, selectionArgs);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        listener.onComplete(cursor);

                    }
                });

            }
        }.start();




    }




}
