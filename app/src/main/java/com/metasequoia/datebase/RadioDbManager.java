package com.metasequoia.datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.metasequoia.manager.radio.bean.Frequency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RadioDbManager implements DbInterface<Frequency> {

    private static final String TAG = "RadioDbManager";
    private Context mContext;
    public DatabaseHelper dbHelper;
    public SQLiteDatabase mDatabase;
    private String tableName = DbConf.TAB_RADIO;

    private static RadioDbManager mInstance = null;

    public static RadioDbManager getInstance() {
        if(mInstance != null) return mInstance;
        synchronized (RadioDbManager.class) {
            if(mInstance == null) {
                mInstance = new RadioDbManager();
            }
        }
        return  mInstance;
    }

    private final String[] POINTS_COLUMNS = new String[] {DbConf.Freq.Points_ID,
            DbConf.Freq.FREQUENCY,
            DbConf.Freq.BAND,
            DbConf.Freq.PSNAME,
            DbConf.Freq.INDEX,
            //DbConf.Freq.AREA,
            DbConf.Freq.UNINUNAME

    };

    public void init(Context context) {
        mContext = context;
        dbHelper = new DatabaseHelper(mContext, DbConf.RADIO_NAME, null, DbConf.RADIO_VERSION);
        mDatabase = dbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getDatabase() {
        if (null == mDatabase) {
            this.mDatabase = dbHelper.getWritableDatabase();
        }
        return this.mDatabase;
    }

    @Override
    public long save(Frequency Frequency) {
        Log.i(TAG, "RadioDbManager..save:" + Frequency);
        // TODO Auto-generated method stub
        long result = -1;
        if(mDatabase != null) {
            if(!isTableExist()) {
                dbHelper.createDbTable(mDatabase, tableName);
            }
        }
        if(null == Frequency) {
            Log.i(TAG, "Frequency is empty");
            return -1;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(DbConf.Freq.FREQUENCY, Frequency.getFrequency());
            values.put(DbConf.Freq.BAND, Frequency.getBandType());
            values.put(DbConf.Freq.PSNAME, Frequency.getPSName());
            values.put(DbConf.Freq.INDEX, Frequency.getIndex());
            //values.put(DbConf.Freq.AREA, Frequency.getArea());
            values.put(DbConf.Freq.UNINUNAME, Frequency.getUninumame());
            result = getDatabase().replace(DbConf.TAB_RADIO, null, values);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public long saveAll(Collection<Frequency> collection) {
        // TODO Auto-generated method stub
        synchronized(mDatabase) {
            long result = -1;
            if (null == collection || collection.isEmpty()) {
                Log.e(TAG, "Frequency is Empty !!!");
                return result;
            }
            try {
                getDatabase().beginTransaction();
                for (Frequency Frequency : collection) {
                    ContentValues values = new ContentValues();
                    values.put(DbConf.Freq.FREQUENCY, Frequency.getFrequency());
                    values.put(DbConf.Freq.BAND, Frequency.getBandType());
                    values.put(DbConf.Freq.PSNAME, Frequency.getPSName());
                    values.put(DbConf.Freq.INDEX, Frequency.getIndex());
                    //values.put(DbConf.Freq.AREA, Frequency.getArea());
                    values.put(DbConf.Freq.UNINUNAME, Frequency.getUninumame());
                    result = getDatabase().insert(DbConf.TAB_RADIO, null, values);
                }
                getDatabase().setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getDatabase().endTransaction();

            }
            return result;
        }
    }

    @Override
    public List<Frequency> queryAll(Frequency table) {
        // TODO Auto-generated method stub
        Cursor cursor = null;
        try {
            List<Frequency> list = new ArrayList<>();
            cursor = getDatabase().query(tableName, null, null, null, null, null, null);
            if(cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    list.add(parsePoints(cursor));
                }
            }
            return list;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public List<Frequency> queryAll(String bandType) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getDatabase();
            if(bandType.equals("FM")) {
                cursor = db.query(tableName, null, DbConf.Freq.BAND + " <=? ", new String[]{"2"}, null, null, DbConf.Freq.BAND + " ASC, " + DbConf.Freq.INDEX + " ASC");
            } else {
                cursor = db.query(tableName, null, DbConf.Freq.BAND + " >? ", new String[]{"2"}, null, null, DbConf.Freq.BAND + " ASC, " + DbConf.Freq.INDEX + " ASC");
            }
            if (cursor.getCount() > 0) {
                List<Frequency> orderList = new ArrayList<Frequency>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Frequency freq = parsePoints(cursor);
                    Log.i(TAG, "freq:" + freq);
                    orderList.add(parsePoints(cursor));
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return null;
    }

    @Override
    public Frequency queryById(Class<Frequency> table, Object id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getDatabase();

            cursor = db.query(tableName,
                    POINTS_COLUMNS,
                    DbConf.Freq.Points_ID + " LIKE ? ",
                    new String[]{"%" + id + "%"},
                    null, null, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    return parsePoints(cursor);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return null;
    }


    @Override
    public int delete(Class table, int id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;
        int result = 0;
        try {
            db = getDatabase();
            //result = db.delete(tableName, DbConf.Freq.BAND + "=?", new String[]{Integer.toString(id)});
            db.execSQL("delete from "+tableName+" where " + DbConf.Freq.BAND + "='"+id+"'");
            //db.execSQL("delete from "+tableName);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Frequency parsePoints(Cursor cursor) {
        Frequency entity = new Frequency(0,0,0,(String)null,0, /*0,*/ (String)null);
        entity.setId(cursor.getInt(cursor.getColumnIndex(DbConf.Freq.Points_ID)));
        entity.setFrequency(cursor.getInt(cursor.getColumnIndex(DbConf.Freq.FREQUENCY)));
        entity.setBandType(cursor.getInt(cursor.getColumnIndex(DbConf.Freq.BAND)));
        entity.setPSName(cursor.getString(cursor.getColumnIndex(DbConf.Freq.PSNAME)));
        entity.setIndex(cursor.getInt(cursor.getColumnIndex(DbConf.Freq.INDEX)));
        //entity.setArea(cursor.getInt(cursor.getColumnIndex(DbConf.Freq.AREA)));
        entity.setUninumame(cursor.getString(cursor.getColumnIndex(DbConf.Freq.UNINUNAME)));
        return entity;
    }

    private boolean isTableExist( ) {
        boolean isTableExist = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mDatabase;
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    isTableExist = true;
                }
            }
        } catch (Exception e) {

        }
        return isTableExist;
    }
}
