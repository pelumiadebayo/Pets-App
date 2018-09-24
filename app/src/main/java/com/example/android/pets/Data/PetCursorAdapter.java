package com.example.android.pets.Data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.pets.Data.PetContract.PetEntry;

import com.example.android.pets.R;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //the layout to inflate
        return LayoutInflater.from(context).inflate(R.layout.pet_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //getting the textView to be inflated
        TextView nameListItem = (TextView) view.findViewById(R.id.name_list_item);
        TextView breedListItem = (TextView) view.findViewById(R.id.breed_list_item);

        //getting the column of pet attribute of name and breed
        int nameColumnIndex = cursor.getColumnIndexOrThrow(PetEntry.COLUMN_NAME);
        int breedColumnIndex = cursor.getColumnIndexOrThrow(PetEntry.COLUMN_BREED);

        //read the attribute from cursor for the current pet
        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        //update the textView with the attribute of the current pet
        nameListItem.setText(petName);
        breedListItem.setText(petBreed);


    }
}
