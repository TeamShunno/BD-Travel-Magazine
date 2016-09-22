/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package team.shunno.bdtm;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "PlaceData";

    // Table columns
    public static final String place_ID = "_id"; // It must be "_id", or many error happened.. :(
    public static final String Division_Name = "DivisionName";
    public static final String District_Name = "DistrictName";
    public static final String Place_Name = "PlaceName";
    public static final String Place_Desc = "PlaceDesc";
    public static final String Imp_Number = "ImpNumber";
    public static final String Hotel_Info = "HotelInfo";
    public static final String Food_Info = "FoodInfo";
    public static final String Recent_Info = "RecentInfo";
    public static final String Images = "Images";
    public static final String GMap_Loc = "GMLoc";
    public static final String Rating = "Rating";

    // Database Information //
    static final String DB_NAME = "bdtm.db"; // DON'T USE UNDERSCORE ("_") in database name.. :3

    /**
     * @DB_VERSION : database version
     * <p/>
     * ********[ HISTORY ]*********
     * <p/>
     * DB_version: App_version : [Total DB entry] (file_name) Comments
     * --------------------------------------------------------------------------
     * 1: 1.0
     */
    static final int DB_VERSION = 1;

    //The Android's default system path of your application database.
    static String DB_PATH; //=
    //"/data/data/team.shunno.bdtm/databases/";
    //myContext.getDatabasePath(DB_NAME).getAbsolutePath();

    private final Context myContext;

    private SQLiteDatabase myDataBase;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application
     * assets and resources.
     *
     * @param context app context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

    }


    public void createDataBase() throws IOException {

        /**
         * TODO: In Stable version CHANGE the following line with this:
         * boolean dbExist = checkDataBase();
         */
        boolean dbExist = false;

        if (dbExist) {
            //do nothing - database already exist

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);

            if (settings.getBoolean(myContext.getString(R.string.pref_update_db), false)) {

                this.getReadableDatabase();

                try {

                    copyDataBase();


                } catch (IOException e) {

                    throw new Error(e.getMessage());

                }
            }

        } else {

            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error(e.getMessage());

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH; // DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        try {

            //Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(DB_NAME);


            // Path to the just created empty db
            String outFileName = DB_PATH; // DB_PATH + DB_NAME;


            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);


            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (Exception e) {
            //Log.e("DBTM", e.getMessage());
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
        settings.edit().putBoolean(myContext.getString(R.string.pref_update_db), false).apply();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH; //DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < newVersion) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(myContext);
            settings.edit().putBoolean(myContext.getString(R.string.pref_update_db), true).apply();
            //settings.edit().putBoolean(myContext.getString(R.string.pref_is_fav_clear_notify_read), false).apply();
        }


    }

}





























