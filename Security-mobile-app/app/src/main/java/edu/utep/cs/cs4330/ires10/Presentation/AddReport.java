package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Add Report </h1>
 *
 * Gathering of user information to place a reports
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import com.google.android.gms.maps.model.LatLng;

import edu.utep.cs.cs4330.ires10.Location.GPSTracker;
import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.MyAsyncTask;

public class AddReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private final String TAG="Security";
    private final String ACTIVITY="AddReport: ";

    // Request codes
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_BROWSE = 2;
    final private int REQUEST_COURSE_ACCESS = 123;

    private final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private GPSTracker gps;
    private Uri mSelectedImageUri=null;
    private double lat, lon;
    private EditText description;
    private TextView textView;
    private int categoryArray = R.array.crime;
    private Spinner spinner;
    private Button browseButton, pictureButton;
    private ImageView mPhotoImageView;
    private RelativeLayout relativeLayout;

    private String[] categories;
    private String[][] subcategories;
    private String spinnerSelection;
    private int categoryID = 0;
    private int subcategoryID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.create);

        relativeLayout = findViewById(R.id.relativeLayout);

        categories = this.getResources().getStringArray(R.array.categories);
        subcategories = new String[][]{
                this.getResources().getStringArray(R.array.crime),
                this.getResources().getStringArray(R.array.suspicious),
                this.getResources().getStringArray(R.array.environment),
                this.getResources().getStringArray(R.array.infrastructure),
                this.getResources().getStringArray(R.array.mobility)
        };
        spinnerSelection = subcategories[0][0];

        //Image View
        mPhotoImageView = findViewById(R.id.photo_image_view);
        //Get location
        gps = new GPSTracker(this);

        // Buttons
        browseButton = findViewById(R.id.button_image_library);
        pictureButton = findViewById(R.id.button_image_camera);

        // Browse picture (open gallery) button
        browseButton.setOnClickListener(view -> {
            Log.d(TAG,ACTIVITY+"Initiation");

            if (areMediaPermissionGranted()) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Uncomment line below to filter by jpeg, png, gif, etc.
                //pickIntent.setType("image/*");
                startActivityForResult(pickIntent, REQUEST_BROWSE);
            } else {
                requestMediaPermissions();
            }

        });

        // Take picture (open camera) button
        pictureButton.setOnClickListener(v -> {
            if (areMediaPermissionGranted()) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mSelectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                requestMediaPermissions();
            }
        });
        description = (EditText)findViewById(R.id.description);
        textView = (TextView)findViewById(R.id.selection_spinner);

        //Spinner element
        spinner = (Spinner) findViewById(R.id.spinner2);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(categoryArray));

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    private boolean areMediaPermissionGranted() {
        for (String permission : requiredPermissions) {
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestMediaPermissions() {
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                REQUEST_COURSE_ACCESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BROWSE:
                    mSelectedImageUri = data.getData();
                    // Display selected photo in image view
                    mPhotoImageView.setImageURI(mSelectedImageUri);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    mPhotoImageView.setImageURI(mSelectedImageUri);
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subcategoryID= position;
        spinnerSelection = subcategories[categoryID][subcategoryID];
        textView.setText(spinnerSelection);
    }

    public void setSpinner(int category){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(category));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void crimeReport(View view){
        toastMssg(getResources().getString(R.string.criminal_category));
        setSpinner(R.array.crime);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.spinnerDefault));
        categoryID = 0;

    }
    public void suspiciousReport(View view){
        toastMssg(getResources().getString(R.string.suspicious_category));
        setSpinner(R.array.suspicious);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.grayCustom));
        categoryID = 1;
    }

    public void environmentReport(View view){
        toastMssg(getResources().getString(R.string.environment_category));
        setSpinner(R.array.environment);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.greenCustom));
        categoryID = 2;
    }

    public void infrastructureReport(View view){
        toastMssg(getResources().getString(R.string.infrastructure_category));
        setSpinner(R.array.infrastructure);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.orangeCustom));
        categoryID = 3;
    }

    public void mobilityReport(View view){
        toastMssg(getResources().getString(R.string.mobility_category));
        setSpinner(R.array.mobility);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.blueCustom));
        categoryID = 4;
    }
    public void submit(View view){
        LatLng coord = getCoordinates();
        lat = coord.latitude;
        lon = coord.longitude;
        Bundle bundle = getIntent().getExtras();
        String imagePath= mSelectedImageUri!=null ? getRealPathFromURI(mSelectedImageUri) : "";
        Random rand = new Random();
        long severityWeight = rand.nextInt((4 - 1) + 1) + 1;
        long confirms = 0;
        long denies = 0;
        boolean hasConfirmed = false;
        boolean hasDenied = false;
        new MyAsyncTask(this).execute(categoryID, lat, lon, System.currentTimeMillis(), subcategoryID, description.getText(), imagePath, bundle.getString("id"), hasConfirmed, hasDenied, confirms, denies, severityWeight);
        Toast.makeText(AddReport.this, getResources().getString(R.string.thanks), Toast.LENGTH_SHORT).show();
        //Finish activity
        finish();
    }

    public void toastMssg(String mssg){
        Toast.makeText(AddReport.this, getResources().getString(R.string.selected) + " " + mssg, Toast.LENGTH_SHORT).show();
    }

    public LatLng getCoordinates() {
        Double lat = null;
        Double lon = null;
        // Get Location
        // Check if GPS enabled
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        } else {
            // Can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            // Default UTEP coordinates
            gps.showSettingsAlert();
            lat = 31.76771355145084;
            lon = -106.50184997506044;
        }
        return new LatLng(lat,lon);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
