package com.example.mzm.activity_inspector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mzm.activity_inspector.Models.activity_model;

import java.util.ArrayList;
import java.util.List;

public class db_helper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ACTIVITY";
    private static final String TABLE_NAME = "ACTIVITY";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_ACC = "acc";
    private static final String COLUMN_SPEED = "speed";
    private static final String COLUMN_DIST = "dist";
    private static final String COLUMN_STANCE = "stance";
    private SQLiteDatabase db;
    private static final String TABLE_CREATE = "create table "+ TABLE_NAME + "(id integer primary key not null ," +
            " start_time text not null, end_time text not null, acc text not null, speed text not null, dist text not null, stance text not null );";



    public db_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(TABLE_CREATE);
        this.db = sqLiteDatabase;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String query = "drop table if exists " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

    public void insert_data(activity_model item){
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(COLUMN_START_TIME,item.start_time);
        data.put(COLUMN_END_TIME,item.end_time);
        data.put(COLUMN_ACC,item.acc);
        data.put(COLUMN_SPEED,item.speed);
        data.put(COLUMN_DIST,item.dist);
        data.put(COLUMN_STANCE,item.stance);

        db.insert(TABLE_NAME, null, data);
        db.close();
    }

    public  ArrayList<activity_model> get_data(){
        db = this.getReadableDatabase();
        ArrayList<activity_model> list = new ArrayList<>();
        String query = "select * from "+ TABLE_NAME +" order by id desc limit 5;";
        Cursor cursor = db.rawQuery(query, null);
        List rows = new ArrayList<>();
        while(cursor.moveToNext()) {

            activity_model item = new activity_model();
            item.start_time = cursor.getString(cursor.getColumnIndex("start_time"));
            item.end_time = cursor.getString(cursor.getColumnIndex("end_time"));
            item.dist = cursor.getString(cursor.getColumnIndex("dist"));
            item.acc = cursor.getString(cursor.getColumnIndex("acc"));
            item.speed = cursor.getString(cursor.getColumnIndex("speed"));
            item.stance = cursor.getString(cursor.getColumnIndex("stance"));

            list.add(item);

        }
        cursor.close();

        return list;
    }


}
