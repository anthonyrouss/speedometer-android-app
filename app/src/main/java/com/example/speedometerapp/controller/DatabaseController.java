package com.example.speedometerapp.controller;

import static com.example.speedometerapp.enums.SpeedViolationColumn.ID;
import static com.example.speedometerapp.enums.SpeedViolationColumn.LATITUDE;
import static com.example.speedometerapp.enums.SpeedViolationColumn.LONGITUDE;
import static com.example.speedometerapp.enums.SpeedViolationColumn.SPEED;
import static com.example.speedometerapp.enums.SpeedViolationColumn.SPEED_LIMIT;
import static com.example.speedometerapp.enums.SpeedViolationColumn.TIMESTAMP;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.speedometerapp.enums.SelectableSearchOption;
import com.example.speedometerapp.model.SpeedViolation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    private static final String DATABASE_NAME = "speedometer.db";
    private static final String TABLE_NAME = "speed_violations";

    private SQLiteDatabase database;

    public DatabaseController(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        ID.getColName() + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        LONGITUDE.getColName() + " REAL," +
                        LATITUDE.getColName() + " REAL," +
                        SPEED.getColName() + " REAL," +
                        SPEED_LIMIT.getColName() + " REAL," +
                        TIMESTAMP.getColName() + " TEXT);"
        );
    }

    public void insertViolation(@NonNull SpeedViolation entry) {
        database.execSQL("INSERT INTO " + TABLE_NAME + " (longitude, latitude, speed, speed_limit, timestamp) VALUES (?,?,?,?,?)",
                new Object[]{entry.getLongitude(), entry.getLatitude(), entry.getSpeed(), entry.getSpeedLimit(), entry.getTimestamp().toString()});
    }

    public List<SpeedViolation> selectViolations(SelectableSearchOption searchOption) {

        String sqlQuery = "SELECT * FROM " + TABLE_NAME;

        // Add where clause
        sqlQuery += searchOption.getSqlWhere();

        // Add order clause
        sqlQuery += String.format(" ORDER BY %s DESC", TIMESTAMP.getColName());

        List<SpeedViolation> entries = new ArrayList<>();
        Cursor cursor = database.rawQuery(sqlQuery, null);
        while (cursor.moveToNext()) entries.add(new SpeedViolation(cursor.getDouble(LONGITUDE.getColIndex()), cursor.getDouble(LATITUDE.getColIndex()), cursor.getDouble(SPEED.getColIndex()), cursor.getDouble(SPEED_LIMIT.getColIndex()), Timestamp.valueOf(cursor.getString(TIMESTAMP.getColIndex()))));
        return entries;
    }

}
