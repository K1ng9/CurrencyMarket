package com.keybool.vkluchak.economic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vkluc_000 on 14.02.2015.
 */
public class DB {

    final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "mydbb";
    private static final int DB_VERSION = 2;
    private static final String DB_TABLE = "currency";

    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COURSE = "course";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CLICKS = "clicks";


    private static final String DB_CREATE =
            "create table " + DB_TABLE + " ("
                    + COLUMN_ID     + " integer primary key autoincrement, " +
                    COLUMN_NAME     + " text, " +
                    COLUMN_COURSE   + " real, " +
                    COLUMN_AMOUNT   + " text, " +
                    COLUMN_PHONE    + " text, " +
                    COLUMN_LOCATION + " text, " +
                    COLUMN_STATUS   + " int, "  +
                    COLUMN_CLICKS   + " int" + ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx){
        mCtx = ctx;
    }
    // открить подключение
    public void open(){
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }
    // закрить подлючение
    public void close(){
        if(mDBHelper!=null)
            mDBHelper.close();
    }

    //---------------------------- робота с таблицей currency
    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(){
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // добавить запись
    public void addRec(String name, float course, String amount, String phone, String location, int status, int clicks ) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COURSE, course);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_PHONE, phone);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_CLICKS, clicks);
        long rowID = mDB.insert(DB_TABLE, null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);
        //mDB.insert(DB_TABLE, null, cv);
    }

    public Cursor selectCurrent(String spinner){
        if(spinner.trim().length() > 0 ) { //работает проверка на пустоту что такое trim() ?
            Log.d(LOG_TAG, "Whare -" + spinner+"-");
            String[] str= {spinner};
            return mDB.rawQuery("SELECT * FROM " +DB_TABLE+ " WHERE " +COLUMN_NAME+ " = ?" , str );
            //return mDB.query(DB_TABLE, null, COLUMN_NAME + " = " + spinner , null, null, null, null);
        }else return null;
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        int clearCount =  mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
    }
    public void delAll(){Log.d(LOG_TAG, "--- Clear mytable: ---");
        // удвляем id записb
        mDB.delete(DB_TABLE, null, null);

    }


    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


}
