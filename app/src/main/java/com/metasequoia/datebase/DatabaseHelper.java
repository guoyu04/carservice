package com.metasequoia.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    private static final String CREATE_POINTS_TABLE = "create table " + DbConf.TAB_RADIO + "("
            + DbConf.Freq.Points_ID
            + " integer primary key autoincrement ,"
            + DbConf.Freq.FREQUENCY + " integer,"
            + DbConf.Freq.BAND + " integer ,"
            + DbConf.Freq.PSNAME + " varchar(20) ,"
            + DbConf.Freq.INDEX + " integer ,"
//            + DbConf.Freq.AREA + " varchar(20) ,"
            + DbConf.Freq.UNINUNAME + " varchar(20) unique"
            + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + DbConf.TAB_RADIO);  //delete table
    }

    public void dropDbTable(SQLiteDatabase db, String table) {
        db.execSQL("DROP TABLE IF EXISTS " + table); //delete table
    }

    public void createDbTable(SQLiteDatabase db, String table) {
        if(table.equals(DbConf.TAB_RADIO)) {
            db.execSQL(CREATE_POINTS_TABLE);           //create table
        }
    }
}
