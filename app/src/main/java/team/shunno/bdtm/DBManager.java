/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.shunno.bdtm;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {

        dbHelper = new DatabaseHelper(context);

        try {

            dbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        dbHelper.openDataBase();

        //Log.v("soa", "getWritable");
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

//    public long insert(String _word, String _meaning, String _synonyms) {
//
//        if (_synonyms.equals(""))
//            _synonyms = null;
//
//        ContentValues contentValue = new ContentValues();
//
//        contentValue.put(DatabaseHelper.SO_PRON, _word);
//        contentValue.put(DatabaseHelper.SO_MEANING, _meaning);
//        contentValue.put(DatabaseHelper.SO_SYNONYMS, _synonyms);
//        contentValue.put(DatabaseHelper.SO_NEW, true);
//
//        // if error return -1
//        return database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
//    }


    public Cursor getPlaceInfoByPlaceId(int placeID) {
        String[] tableColumns = new String[]{
                DatabaseHelper.place_ID, //_id column must needed for cursor adaptor.. O.o
                DatabaseHelper.Place_Name,
                DatabaseHelper.Division_Name,
                DatabaseHelper.District_Name,
                DatabaseHelper.Place_Desc,
                DatabaseHelper.Imp_Number,
                DatabaseHelper.Hotel_Info,
                DatabaseHelper.Food_Info,
                DatabaseHelper.Recent_Info,
                DatabaseHelper.Images,
                DatabaseHelper.GMap_Loc,
                DatabaseHelper.Rating
        };

        String whereClause = DatabaseHelper.place_ID + " = ?";

        String[] whereArgs = new String[]{
                String.valueOf(placeID)
        };

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, tableColumns, whereClause, whereArgs,
                null, null, null, null);

        if (cursor != null) {
            try {

                cursor.moveToFirst();

            } catch (Exception ignored) {
            }
        }
        return cursor;
    }

    public Cursor getPlaceNamesByDivision(String divisionName) {
        String[] tableColumns = new String[]{
                DatabaseHelper.place_ID, //_id column must needed for cursor adaptor.. O.o
                DatabaseHelper.Place_Name
        };

        String whereClause = DatabaseHelper.Division_Name + " = ?";

        String[] whereArgs = new String[]{
                String.valueOf(divisionName)
        };

        /**
         * Sorted by "Place_Name"
         */
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, tableColumns, whereClause, whereArgs,
                null, null, DatabaseHelper.Place_Name, null);

        if (cursor != null) {
            try {

                cursor.moveToFirst();

            } catch (Exception ignored) {
            }
        }
        return cursor;
    }


//    public int update(String _word, String _meaning, String _synonyms) {
//
//        if (_synonyms.equals(""))
//            _synonyms = null;
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.SO_PRON, _word);
//        contentValues.put(DatabaseHelper.SO_MEANING, _meaning);
//        contentValues.put(DatabaseHelper.SO_SYNONYMS, _synonyms);
//        contentValues.put(DatabaseHelper.SO_MODIFY, true);
//
//        return database.update(DatabaseHelper.TABLE_NAME, contentValues,
//                DatabaseHelper._WORD + " = \"" + so_tools.removeSymbolFromText(_word) + "\"", null);
//    }

//    public int delete(String _word) {
//        //Must use double quotation mark for string logic in sql language.
//        return database.delete(DatabaseHelper.TABLE_NAME,
//                DatabaseHelper._WORD + " = \"" + so_tools.removeSymbolFromText(_word) + "\"", null);
//    }


}



















