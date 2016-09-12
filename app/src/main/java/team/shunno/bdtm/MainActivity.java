package team.shunno.bdtm;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener,
        View.OnClickListener {

    final String[] from = new String[]{
            DatabaseHelper.place_ID,
            DatabaseHelper.Place_Name
    };
    final int[] to = new int[]{
            R.id.textViewPlaceID,
            R.id.textViewPlaceName
    };
    ListView listView;
    SimpleCursorAdapter adapter;
    String _placeId, _gmapLoc;

    int sImageWidth = 0, sImageHeight = 0;

    /**
     * Database manager object
     */
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /**
         * Init Database
         */
        DatabaseHelper.DB_PATH = getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();
        dbManager = new DBManager(MainActivity.this);
        dbManager.open();

        /**
         * Init ListView
         */
        Cursor cursor = null;
        listView = (ListView) findViewById(R.id.resultList);
        listView.setOnItemClickListener(MainActivity.this);
        listView.setEmptyView(findViewById(R.id.empty)); // for empty view
        adapter = new SimpleCursorAdapter(this, R.layout.place_result_layout, cursor, from, to, 0);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        /**
         * Init Treding place corner button
         */
        ImageButton btn_more_1 = (ImageButton) findViewById(R.id.btn_more_1);
        ImageButton btn_more_2 = (ImageButton) findViewById(R.id.btn_more_2);
        ImageButton btn_more_3 = (ImageButton) findViewById(R.id.btn_more_3);
        btn_more_1.setOnClickListener(MainActivity.this);
        btn_more_2.setOnClickListener(MainActivity.this);
        btn_more_3.setOnClickListener(MainActivity.this);

        setTradings();
    }

    void setTradings() {

        showTrendingViews();

        /**
         * https://developer.android.com/reference/android/util/DisplayMetrics.html
         * Get display size
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        sImageWidth = metrics.widthPixels;
        sImageHeight = metrics.heightPixels / 2;

        //image size will be half of the screen size.
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(sImageWidth, sImageHeight);

        /**
         * Loop 3 times to fill 3 trend place
         *
         * Must Start loop from 1, because the view ids name are started from 1.
         */
        for (int j = 1; j <= 3; j++) {
            Cursor cursor = dbManager.getPlaceInfoByPlaceId(j);

            int layoutID = getResources().getIdentifier("div_" + String.valueOf(j), "id", getPackageName());
            int cornerButtonId = getResources().getIdentifier("btn_more_" + String.valueOf(j), "id", getPackageName());

            LinearLayout div_layout = (LinearLayout) findViewById(layoutID);
            ImageButton imageButton = (ImageButton) findViewById(cornerButtonId);

            /**
             * Saving the place id to Corner Image Button tag property
             */
            imageButton.setTag(R.string.key_place_id, cursor.getString(cursor.getColumnIndex(DatabaseHelper.place_ID)));
            imageButton.setTag(R.string.key_gmap_loc, cursor.getString(cursor.getColumnIndex(DatabaseHelper.GMap_Loc)));

            if (cursor != null) {
                int id = getResources().getIdentifier("div_title_" + String.valueOf(j), "id", getPackageName());
                TextView textView = (TextView) findViewById(id);
                textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Place_Name)));

                String imgStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Images));

                String[] imgNames = imgStr.split(",");

                //int totalImg = imgNames.length;
                /**
                 * 3 x 5 = 15 pics load make Overflow of memory! so we just load 2 imgs of every place
                 */
                for (int k = 0; k < 2; k++) {
                    ImageView imageView = new ImageView(MainActivity.this);

                    /**
                     * http://stackoverflow.com/questions/21856260/how-can-i-convert-string-to-drawable
                     * Image drawable names img_placeID_imageNumber. e.g., img_1_1 etc.
                     *
                     * https://developer.android.com/guide/practices/screens_support.html
                     * Max image size should be 2560px x 1600px.
                     * In app image width will be the screen width.
                     */
                    int drawableId = getResources().getIdentifier(imgNames[k], "drawable", getPackageName());

                    /**
                     * Using Picasso
                     */
                    Picasso.with(MainActivity.this)
                            .load(drawableId)
                            .resize(sImageWidth, sImageHeight)
                            .centerCrop()
                            .into(imageView);

                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    div_layout.addView(imageView);
                }
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trends) {

            showTrendingViews();

            adapter.changeCursor(null);

        } else if (id == R.id.nav_barishal) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Barishal"));

            showResultViews();

        } else if (id == R.id.nav_chittagong) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Chittagong"));

            showResultViews();

        } else if (id == R.id.nav_dhaka) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Dhaka"));

            showResultViews();

        } else if (id == R.id.nav_khulna) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Khulna"));

            showResultViews();

        } else if (id == R.id.nav_mymensingh) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Mymensingh"));

            showResultViews();

        } else if (id == R.id.nav_rajshahi) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Rajshahi"));

            showResultViews();

        } else if (id == R.id.nav_rangpur) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Rangpur"));

            showResultViews();

        } else if (id == R.id.nav_sylhet) {

            adapter.changeCursor(dbManager.getPlaceNamesByDivision("Sylhet"));

            showResultViews();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showTrendingViews() {
        findViewById(R.id.treadingLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewTrending).setVisibility(View.VISIBLE);
        findViewById(R.id.resultLayout).setVisibility(View.GONE);
    }

    void showResultViews() {
        findViewById(R.id.treadingLayout).setVisibility(View.GONE);
        findViewById(R.id.textViewTrending).setVisibility(View.GONE);
        findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.resultList:

                TextView idTextView = (TextView) view.findViewById(R.id.textViewPlaceID);

                String placeId = idTextView.getText().toString();

                showDetailsView(placeId);

                break;
        }
    }

    void showDetailsView(String placeId) {
        Intent details_view_intent = new Intent(getApplicationContext(), detailsView.class);

        /**
         * Send the place id
         */
        details_view_intent.putExtra("place_id", placeId);

        startActivity(details_view_intent);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more_1:
            case R.id.btn_more_2:
            case R.id.btn_more_3:

                /**
                 * In the tag property of Trend Corner button, we saved the place IDs.
                 *
                 * Saving the place id in global variable: _placeId
                 */
                _placeId = (String) v.getTag(R.string.key_place_id);
                _gmapLoc = (String) v.getTag(R.string.key_gmap_loc);

                //Creating the instance of PopupMenu
                PopupMenu popupOpts = new PopupMenu(MainActivity.this, findViewById(v.getId()));
                //Inflating the Popup using xml file
                popupOpts.getMenuInflater()
                        .inflate(R.menu.popup_trend_place, popupOpts.getMenu());

                //registering popup with OnMenuItemClickListener
                popupOpts.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.item_get_direction:

                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + _gmapLoc);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                                break;
                            case R.id.item_view_details:

                                showDetailsView(_placeId);

                                break;
                        }

                        return true;
                    }
                });

                popupOpts.show();

                break;
        }
    }

//    public void loadBitmap(int resId, ImageView imageView) {
//        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
//        task.execute(resId);
//    }
//
//    /**
//     * Processing Bitmaps Off the UI Thread
//     * https://developer.android.com/training/displaying-bitmaps/process-bitmap.html
//     */
//    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
//        private final WeakReference<ImageView> imageViewReference;
//        private int data = 0;
//
//        public BitmapWorkerTask(ImageView imageView) {
//            // Use a WeakReference to ensure the ImageView can be garbage collected
//            imageViewReference = new WeakReference<ImageView>(imageView);
//        }
//
//        // Decode image in background.
//        @Override
//        protected Bitmap doInBackground(Integer... params) {
//            data = params[0];
//            return utils.decodeSampledBitmapFromResource(getResources(), data, sImageWidth, sImageHeight);
//        }
//
//        // Once complete, see if ImageView is still around and set bitmap.
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (imageViewReference != null && bitmap != null) {
//                final ImageView imageView = imageViewReference.get();
//                if (imageView != null) {
//
//                    imageView.setImageBitmap(bitmap);
//                    //todo
//                }
//            }
//        }
//    }

}
