package com.tommytao.a5steak.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.tommytao.a5steak.common.Foundation;

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


    private HashMap<String, SQLiteDatabase> hashMapSqliteDatabase = new HashMap<>();

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

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

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        listener.onComplete(cursor);

                    }
                });

            }
        }.start();




    }

    public String getCursorStr(Cursor cursor, String key) {
        try {
            return cursor.getString(cursor.getColumnIndex(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




}
