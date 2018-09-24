package com.example.android.pets.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.Data.PetContract.PetEntry;
import com.example.android.pets.Data.PetDbHelper;

import static android.R.attr.value;

public class PetProvider extends ContentProvider {

    public static final String LOD_TAG = PetProvider.class.getSimpleName();
    //database helper object
    private PetDbHelper mDbHelper;

    //uri codes to ensure valid uri
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    //new uri matcher method
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //adding uri paths
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PET_PATH, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PET_PATH + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        //called to initialize an instance of the mDbHelper(database)
        //onCreate should create the database
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Override
    //Reading from the database
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
       //Reading from database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        //result from reading
        Cursor cursor ;
        //uri matcher

        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                //all the content uri
                cursor = database.query(PetEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //single item in the content
                cursor = database.query(PetEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
            throw new IllegalArgumentException("cannot query unknown uri:" + uri);
        }
        //set notification for any changes in the data for this uri, so to update cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Cursor cursor ;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("inserting is not supported for:" + uri);
        }
    }
    private Uri insertPet(Uri uri, ContentValues values){
        //check that name is not null
        String name = values.getAsString(PetEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("Pet requires a name!" );
        }
        //check that breed is not null
        String breed = values.getAsString(PetEntry.COLUMN_BREED);
        if (name == null){
            throw new IllegalArgumentException("Pet requires a breed!" );
        }
        //check that gender is valid
        Integer gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender!" );
        }
        //check that weight is not null and not equal to 0
        Integer weight = values.getAsInteger(PetEntry.COLUMN_WEIGHT);
        if (weight == null && weight < 0){
            throw new IllegalArgumentException("Pet requires valid weight!" );
        }
        //getting to write to the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long newRowId = database.insert(PetEntry.TABLE_NAME, null, values);
        if (newRowId == -1){
            Toast.makeText(getContext(), "Error with saving pet.", Toast.LENGTH_SHORT).show();
            return null;
        }
        //notify all listener(eg cursorLoader) that data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return update(uri, values, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("inserting is not supported for:" + uri);
        }
    }
    private Integer updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetEntry.COLUMN_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //uri of updated row
        int cursor = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs );
        //notify all listener(eg cursorLoader) that data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
