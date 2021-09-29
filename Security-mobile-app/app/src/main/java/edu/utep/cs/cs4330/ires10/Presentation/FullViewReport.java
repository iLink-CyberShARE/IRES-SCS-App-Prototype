package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Full View Report </h1>
 *
 * View of detailed report information once report is selected
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.HttpClient;

public class FullViewReport extends AppCompatActivity {

    private HttpClient API=new HttpClient();

    private final String ACTIVITY="FullViewReport: ";

    //This function speaks to Reports Server to use the javascript functions for updating the confirms field of the report.
    public void updateConfirm(String id, Bundle bundle){
        //Retrieve data from database
        String login = bundle.getString("login");
        new Thread(()->{
            Map<String, String> params = new HashMap<String, String>();
            params.put("userID", login);
            String jsonStr=API.connection("PUT","/updateConfirm/" + id, params);
        }).start();
    }

    //This function speaks to Reports Server to use the javascript functions for updating the denies field of the report.
    public void updateDeny(String id){
        //Retrieve data from database
        Bundle bundle = getIntent().getExtras();
        String login = bundle.getString("login");
        new Thread(()->{
            Map<String, String> params = new HashMap<String, String>();
            params.put("userID", login);
            String jsonStr=API.connection("PUT","/updateDeny/" + id, params);
        }).start();
    }

    private static final String TAG = "ConfirmClick";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_view_report);
        Bundle bundle = getIntent().getExtras();
        String login = bundle.getString("login");
        TextView textView = findViewById(R.id.textView);
        textView.setText("Category: " + bundle.getInt("categoryID") + "\n" + "Date: " + "\"" + bundle.getLong(("date")) + "\n" + "Incident: " + bundle.getInt("incident") + "\n" + "Latitude: " + bundle.getDouble("latitude") + "\n" + "Longitude: " + bundle.getDouble("longitude") + "\n" + "Description: " + "\"" + bundle.getString("description") + "\"" + "\n" + "Confirms: " + bundle.getLong("confirms") + "\n" + "Denies: " + bundle.getLong("denies") + "\n"  + "\n" + "\n" + "Severity Index of Report: " + bundle.getLong("severityWeight"));
        Button confirmButton = (Button) findViewById(R.id.confirmButton);

        //This method defines the behavior of the confirm button.
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bundle.getStringArrayList("usersConfirmed").isEmpty()) {
                    updateConfirm(bundle.getString("reportID"), bundle);
                    Toast.makeText(FullViewReport.this, "Report confirmed.", Toast.LENGTH_SHORT).show();
                    //Finish activity
                    finish();
                }
                else if (bundle.getStringArrayList("usersConfirmed").get(0).contains(login) == false) {
                    updateConfirm(bundle.getString("reportID"), bundle);
                    Toast.makeText(FullViewReport.this, "Report confirmed.", Toast.LENGTH_SHORT).show();
                    //Finish activity
                    finish();
                }
                else {
                    Toast.makeText(FullViewReport.this, "Confirm unsuccessful. You have already confirmed the event.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        Button denyButton = (Button) findViewById(R.id.denyButton);

        //This code defines the function of the deny button.
        denyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (bundle.getStringArrayList("usersDenied").isEmpty()) {
                    updateDeny(bundle.getString("reportID"));
                    Toast.makeText(FullViewReport.this, "Report denied. ->" + login, Toast.LENGTH_SHORT).show();
                    //Finish activity
                    finish();
                }
                else if (bundle.getStringArrayList("usersDenied").get(0).contains(login) == false) {
                    updateDeny(bundle.getString("reportID"));
                    Toast.makeText(FullViewReport.this, "Deny confirmed.", Toast.LENGTH_SHORT).show();
                    //Finish activity
                    finish();
                }
                else {
                    Toast.makeText(FullViewReport.this, "Deny unsuccessful. You have already denied the event.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
