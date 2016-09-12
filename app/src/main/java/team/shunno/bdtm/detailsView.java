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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

/**
 * Details View Code File
 * <p/>
 * Strategy:
 * Map, Text and Image layout all embedded in details_view.xml file.
 * visibility property will used to show needed views.
 */

public class detailsView extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String[] strLatLng;
    TextView textView;
    /**
     * Database manager object
     */
    private DBManager dbManager;
    /**
     * Map Object
     */
    private GoogleMap mMap;
    private String place_id, Division_Name, District_Name, Place_Name, Imp_Number, Hotel_Info, Food_Info, Recent_Info,
            Images, GMap_Loc, Rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_left);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton imageButtonGetDirection = (ImageButton) findViewById(R.id.btn_get_direction);
        imageButtonGetDirection.setOnClickListener(detailsView.this);

        /**
         * Init Database
         */
        dbManager = new DBManager(detailsView.this);
        dbManager.open();

        /**
         * Get the place location
         */
        Intent intent = getIntent();
        place_id = intent.getStringExtra("place_id");

        initDetailsViews();

    }

    void initDetailsViews() {

        Cursor cursor = dbManager.getPlaceInfoByPlaceId(Integer.parseInt(place_id));
        if (cursor != null) {
            /**
             * Get data from the Cursor
             */
            Division_Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Division_Name));
            District_Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.District_Name));
            Place_Name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Place_Name));
            Imp_Number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Imp_Number));
            Hotel_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Hotel_Info));
            Food_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Food_Info));
            Recent_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Recent_Info));
            Images = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Images));
            GMap_Loc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GMap_Loc));
            Rating = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Rating));

            /**
             * Set datas
             */
            ((TextView) findViewById(R.id.textViewDivision)).setText(Division_Name);
            ((TextView) findViewById(R.id.textViewDistrict)).setText(District_Name);

            /**
             * Obtain the SupportMapFragment and get notified when the map is ready to be used.
             */
            strLatLng = GMap_Loc.split(",");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            textView = (TextView) findViewById(R.id.textView);

            /**
             * Set Image Gallery
             */
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            //image size will be half of the screen size.
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels / 2);

            LinearLayout div_layout = (LinearLayout) findViewById(R.id.imgGallery);

            String[] imgNames = Images.split(",");

            for (String imgName : imgNames) {
                ImageView imageView = new ImageView(detailsView.this);

                int drawableId = getResources().getIdentifier(imgName, "drawable", getPackageName());

                /**
                 * Using Picasso
                 */
                Picasso.with(detailsView.this)
                        .load(drawableId)
                        .resize(metrics.widthPixels, metrics.heightPixels / 2)
                        .centerCrop()
                        .into(imageView);

                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                div_layout.addView(imageView);
            }

            /**
             * Set Title
             */
            setTitle(Place_Name);

            /**
             * Finally specify which will show default
             */
            showLocationInfo();

        } else {
            throw new Error("Error! Data not found!");
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /**
         * http://stackoverflow.com/questions/27261670/convert-string-to-latlng
         */

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(Double.valueOf(strLatLng[0]), Double.valueOf(strLatLng[1]));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Kalurghat Bridge"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            finish();

        } else if (id == R.id.nav_location) {

            showLocationInfo();

        } else if (id == R.id.nav_hotel) {

            if (!TextUtils.isEmpty(Hotel_Info))
                textView.setText(Hotel_Info);
            else
                textView.setText(getString(R.string.no_info_found));

            showTextInfo();

        } else if (id == R.id.nav_police) {

            if (!TextUtils.isEmpty(Imp_Number))
                textView.setText(Imp_Number);
            else
                textView.setText(getString(R.string.no_info_found));

            showTextInfo();

        } else if (id == R.id.nav_recent_event) {

            Toast.makeText(detailsView.this, "Comming Soon...", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_images) {

            showImageInfo();

        } else if (id == R.id.nav_prev_tours) {

            Toast.makeText(detailsView.this, "Comming Soon...", Toast.LENGTH_LONG).show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showLocationInfo() {
        findViewById(R.id.mapLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.textLayout).setVisibility(View.GONE);
        findViewById(R.id.imageLayout).setVisibility(View.GONE);

    }

    void showImageInfo() {
        findViewById(R.id.mapLayout).setVisibility(View.GONE);
        findViewById(R.id.textLayout).setVisibility(View.GONE);
        findViewById(R.id.imageLayout).setVisibility(View.VISIBLE);
    }

    void showTextInfo() {
        findViewById(R.id.mapLayout).setVisibility(View.GONE);
        findViewById(R.id.textLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.imageLayout).setVisibility(View.GONE);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_direction:

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + GMap_Loc);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

                break;
        }
    }
}
