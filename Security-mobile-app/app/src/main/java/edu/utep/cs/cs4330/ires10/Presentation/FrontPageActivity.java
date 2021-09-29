package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Front Page Activity </h1>
 *
 * View of the main menu of the SCS prototype application
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.utep.cs.cs4330.ires10.R;

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = "Test: ";
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This bundle is where the login is pulled then pushed towards ReportListFragment.
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        myToolbar.setTitle(R.string.help);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 255);
            return;
        }

        loadFragment(new AlertFragment());

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    myToolbar.setTitle(R.string.help);
                    fragment = new AlertFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_reports:
                    myToolbar.setTitle(R.string.reports);
                    fragment = new ReportListFragment();
                    Bundle bundle = getIntent().getExtras();
                    fragment.setArguments(bundle);
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_map:
                    myToolbar.setTitle(R.string.map);
                    fragment = new MapFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_add_report:
                    myToolbar.setTitle(R.string.add);
                    // This bundle continues to pass the login to the FullViewReport for confirmation and denies. It gets pushed to AddReport
                    bundle = getIntent().getExtras();
                    Intent intent = new Intent(FrontPageActivity.this, AddReport.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Create options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to menu item selection
        switch (item.getItemId()) {
            case R.id.viewReports:
                startActivity(new Intent(this, AddReport.class));
                break;
            case R.id.about:
                break;
            case R.id.help:
                break;
            case R.id.exit:
                System.exit(1);
        }
        return super.onOptionsItemSelected(item);
    }
}
