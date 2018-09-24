package com.example.android.pets.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.Data.PetContract.PetEntry;
import static android.os.FileObserver.CREATE;

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Pets.db";
    public static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRY = "CREATE_TABLE" + PetEntry.TABLE_NAME +
                "(" + PetEntry._ID  +"PRIMARY KEY AUTOINCREMENT," +
                PetEntry.COLUMN_NAME + "TEXT NOT NULL" +
                PetEntry.COLUMN_BREED + "TEXT" +
                PetEntry.COLUMN_GENDER + "INTEGER NOT NULL," +
                PetEntry.COLUMN_WEIGHT + "INTEGER NOT NULL DEFAULT 0" + ");";
            db.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
