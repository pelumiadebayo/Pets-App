package com.example.android.pets;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.Data.PetContract;
import com.example.android.pets.Data.PetContract.PetEntry;
import com.example.android.pets.Data.PetCursorAdapter;
import com.example.android.pets.Data.PetDbHelper;

import static android.R.attr.value;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PET_LOADER = 0;
    PetCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView displayListView = (ListView) findViewById(R.id.list_view_pet);
        View emptyView = findViewById(R.id.empty_view);
        displayListView.setEmptyView(emptyView);
        //no pet data until loader finishes, so cursor is null for now
        mCursorAdapter = new PetCursorAdapter(this, null);
        displayListView.setAdapter(mCursorAdapter);

        displayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        //kicking off the loader
        getLoaderManager().initLoader(PET_LOADER, null, null);

    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        displayDatabaseInfo();
//    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
//    private void displayDatabaseInfo() {
//         To access our database, we instantiate our subclass of SQLiteOpenHelper
//         and pass the context, which is the current activity.
//
//         Perform this raw SQL query "SELECT * FROM pets"
//         to get a Cursor that contains all rows from the pets table.
//        String[] projection={
//                PetContract.PetEntry._ID,
//                PetContract.PetEntry.COLUMN_NAME,
//                PetContract.PetEntry.COLUMN_BREED,
//                PetContract.PetEntry.COLUMN_GENDER,
//                PetContract.PetEntry.COLUMN_WEIGHT
//        };
//
//        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,
//                projection,null,null,null,null);
//        try {
//             Display the number of rows in the Cursor (which reflects the number of rows in the
//             pets table in the database).
//            ListView displayListView = (ListView) findViewById(R.id.list_view_pet);
//            PetCursorAdapter adapter = new PetCursorAdapter(this, cursor);
//            displayListView.setAdapter(adapter);
//            displayView.setText("The Pets Table contains " + cursor.getCount() + "Pets");
//            displayView.append(PetEntry._ID + "_"+
//                    PetEntry.COLUMN_NAME + "_"+
//                    PetEntry.COLUMN_BREED + "_"+
//                    PetEntry.COLUMN_GENDER + "_"+
//                    PetEntry.COLUMN_WEIGHT+ "\n");
//
//            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
//            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_BREED);
//            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_GENDER);
//            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_WEIGHT);
//
//            while (cursor.moveToFirst()) {
//                int currentId = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                String currentBreed = cursor.getString(breedColumnIndex);
//                int currentGender = cursor.getInt(genderColumnIndex);
//                int currentWeight = cursor.getInt(weightColumnIndex
//                );
//
//                displayView.append("\n" + currentId + "_" + currentName + "_" + currentBreed +
//                        "_" + currentGender + "_" + currentWeight);
//            }
//
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_WEIGHT, 7);
        Uri newRowUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED,
        };
        return new CursorLoader(this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}