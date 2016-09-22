package team.shunno.bdtm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
 * <p>
 * Strategy:
 * Map, Text and Image layout all embedded in details_view.xml file.
 * visibility property will used to show needed views.
 */

public class detailsView extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {

    String[] strLatLng;
    TextView textView;
    int shortAnimTime;

    View mapLayout, textLayout, imageLayout;
    float Rating;
    /**
     * Database manager object
     */
    private DBManager dbManager;
    /**
     * Map Object
     */
    private GoogleMap mMap;
    /**
     * Variables for storing the Data form database
     */
    private String place_id, Division_Name, District_Name, Place_Name, Place_Description, Imp_Number, Hotel_Info, Food_Info, Recent_Info,
            Images, GMap_Loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_view);

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

        /**
         * Set animation speed
         */
        shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

        initLeftPanel();

        initDetailsViews();

    }

    void initLeftPanel() {
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnLocation = (ImageButton) findViewById(R.id.btnLocation);
        ImageButton btnHotel = (ImageButton) findViewById(R.id.btnHotel);
        ImageButton btnPolice = (ImageButton) findViewById(R.id.btnPolice);
        ImageButton btnRecentEvent = (ImageButton) findViewById(R.id.btnRecentEvent);
        ImageButton btnImages = (ImageButton) findViewById(R.id.btnImages);
        ImageButton btnPrevTours = (ImageButton) findViewById(R.id.btnPrevTours);
        ImageButton btnRating = (ImageButton) findViewById(R.id.btnRating);

        btnHome.setOnClickListener(detailsView.this);
        btnLocation.setOnClickListener(detailsView.this);
        btnHotel.setOnClickListener(detailsView.this);
        btnPolice.setOnClickListener(detailsView.this);
        btnRecentEvent.setOnClickListener(detailsView.this);
        btnImages.setOnClickListener(detailsView.this);
        btnPrevTours.setOnClickListener(detailsView.this);
        btnRating.setOnClickListener(detailsView.this);

        /**
         * The Views
         */
        mapLayout = findViewById(R.id.mapLayout);
        textLayout = findViewById(R.id.textLayout);
        imageLayout = findViewById(R.id.imageLayout);
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
            Place_Description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Place_Desc));
            Imp_Number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Imp_Number));
            Hotel_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Hotel_Info));
            Food_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Food_Info));
            Recent_Info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Recent_Info));
            Images = cursor.getString(cursor.getColumnIndex(DatabaseHelper.Images));
            GMap_Loc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.GMap_Loc));
            Rating = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.Rating));

            /**
             * Setting data
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

            /**
             * Keeping ratio 16:9
             */
            int imageHeight = (9 * metrics.widthPixels) / 16;

            //image size will be half of the screen size.
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(metrics.widthPixels, imageHeight);

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
                        .resize(metrics.widthPixels, imageHeight)
                        .centerCrop()
                        .into(imageView);

                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                div_layout.addView(imageView);
            }

            /**
             * Set Title, place name & place Description
             */
            setTitle(Place_Name);
            ((TextView) findViewById(R.id.textViewPlaceName)).setText(Place_Name);
            ((TextView) findViewById(R.id.textViewDescription)).setText(Place_Description);

            /**
             * Set default ratings
             */
            ((RatingBar) findViewById(R.id.ratingBar)).setRating(Rating);

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
        mMap.addMarker(new MarkerOptions().position(latLng).title(Place_Name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }


    void showLocationInfo() {

        textLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textLayout.setVisibility(View.GONE);
            }
        });

        imageLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageLayout.setVisibility(View.GONE);
            }
        });

        mapLayout.setAlpha(0);
        mapLayout.setVisibility(View.VISIBLE);
        mapLayout.animate().setDuration(shortAnimTime).alpha(
                1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

    }

    void showImageInfo() {

        mapLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mapLayout.setVisibility(View.GONE);
            }
        });

        textLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textLayout.setVisibility(View.GONE);
            }
        });

        imageLayout.setAlpha(0);
        imageLayout.setVisibility(View.VISIBLE);
        imageLayout.animate().setDuration(shortAnimTime).alpha(
                1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

    }

    void showTextInfo() {

        mapLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mapLayout.setVisibility(View.GONE);
            }
        });

        imageLayout.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageLayout.setVisibility(View.GONE);
            }
        });

        textLayout.setAlpha(0);
        textLayout.setVisibility(View.VISIBLE);
        textLayout.animate().setDuration(shortAnimTime).alpha(
                1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

    }

    void showRating() {
        AlertDialog.Builder alert = new AlertDialog.Builder(detailsView.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View inf = inflater.inflate(R.layout.rating_layout, null);
        alert.setView(inf);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        Button btnSubmit = (Button) inf.findViewById(R.id.btnSubmitRating);
        Button btnCancel = (Button) inf.findViewById(R.id.btnCancel);
        RatingBar ratingBar = (RatingBar) inf.findViewById(R.id.ratingBarUser);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                Toast.makeText(detailsView.this, String.valueOf(v), Toast.LENGTH_SHORT).show();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(detailsView.this, "Thanks For the Rating", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
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
            case R.id.btnHome:

                finish();

                break;
            case R.id.btnLocation:

                showLocationInfo();

                break;
            case R.id.btnHotel:

                if (!TextUtils.isEmpty(Hotel_Info))
                    textView.setText(Hotel_Info);
                else
                    textView.setText(getString(R.string.no_info_found));

                showTextInfo();

                break;
            case R.id.btnPolice:

                if (!TextUtils.isEmpty(Imp_Number))
                    textView.setText(Imp_Number);
                else
                    textView.setText(getString(R.string.no_info_found));

                showTextInfo();

                break;
            case R.id.btnRecentEvent:

                Toast.makeText(detailsView.this, "Comming Soon...", Toast.LENGTH_LONG).show();

                break;
            case R.id.btnImages:

                showImageInfo();

                break;
            case R.id.btnPrevTours:

                Toast.makeText(detailsView.this, "Comming Soon...", Toast.LENGTH_LONG).show();

                break;
            case R.id.btnRating:

                showRating();

                break;

        }
    }
}
