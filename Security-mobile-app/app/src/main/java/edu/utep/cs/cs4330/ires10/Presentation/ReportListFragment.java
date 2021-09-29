package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Report List Fragment </h1>
 *
 * Display of user reports in a list along with creation of widgets for detailed report view
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import edu.utep.cs.cs4330.ires10.Model.Report;
import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.HttpClient;
import edu.utep.cs.cs4330.ires10.Utils.MyAsyncTask;
import edu.utep.cs.cs4330.ires10.Utils.ReportListManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportListFragment extends Fragment {
    private final String TAG="Security";
    private final String ACTIVITY="ReportListFragment: ";

    ListView listView;
    ReportListManager reportsListManager = new ReportListManager();
    JSONParser parser = new JSONParser();
    CustomAdapter customAdapter;
    int categoryID = -1; // Query all reports
    private HttpClient API=new HttpClient();
    private Handler handler;

    public ReportListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);

        //Modify Main thread
        this.handler = new Handler(getActivity().getMainLooper());

        Spinner spinner = (Spinner) view.findViewById(R.id.viewReportSpinner);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.filter_categories));

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        requestToGetReports(categoryID);
        listView = (ListView)view.findViewById(R.id.listView);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                categoryID = pos -1;
                reportsListManager.clear();
                requestToGetReports(categoryID);
                loadImages();
                customAdapter = new CustomAdapter(getActivity(), reportsListManager.getReports());
                listView.setAdapter(customAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Report reportSelected = customAdapter.getItem(position);
                        Intent intent = new Intent(getContext(), FullViewReport.class);
                        String login = getArguments().getString("id");
                        //Create the bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("login", login);
                        bundle.putInt("categoryID", reportSelected.getCategoryID());
                        bundle.putLong("date", reportSelected.getDate());
                        bundle.putInt("incident", reportSelected.getIncident());
                        bundle.putDouble("latitude", reportSelected.getLatitude());
                        bundle.putDouble("longitude", reportSelected.getLongitude());
                        bundle.putString("description", reportSelected.getDescription());
                        bundle.putString("imageName", reportSelected.getImageName());
                        bundle.putString("reportID", reportSelected.getReportID());
                        bundle.putString("userID", reportSelected.getUserID());
                        bundle.putLong("confirms", reportSelected.getConfirms());
                        bundle.putLong("denies", reportSelected.getDenies());
                        bundle.putBoolean("hasConfirmed", reportSelected.getHasConfirmed());
                        bundle.putBoolean("hasDenied", reportSelected.getHasDenied());
                        bundle.putLong("severityWeight", reportSelected.getSeverityWeight());
                        bundle.putStringArrayList("usersConfirmed", reportSelected.getUsersConfirmed());
                        bundle.putStringArrayList("usersDenied", reportSelected.getUsersDenied());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void requestToGetReports(int queryReports){
        try {
            String jsonString = new MyAsyncTask(getActivity()).execute("GET", queryReports).get();
            Log.d(TAG,ACTIVITY+jsonString);   //---------------------------------
            Object obj = parser.parse(jsonString);
            JSONArray jsonArray = (JSONArray) obj;
            for(int i = 0; i<jsonArray.size(); i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int category = 0;
                if(jsonObject.get("categoryID").getClass().getName().equals("java.lang.Long")){
                    Long buffer = (Long) jsonObject.get("categoryID");
                    category =  buffer.intValue();
                }
                Long timestampInMillis = Long.parseLong((String)jsonObject.get("timestamp"));
                int subcategory = 0;
                if(jsonObject.get("incident").getClass().getName().equals("java.lang.Long")){
                    Long buffer = (Long) jsonObject.get("incident");
                    subcategory =  buffer.intValue();
                }
                double latitude = Double.valueOf((String)jsonObject.get("latitude"));
                double longitude = Double.valueOf((String)jsonObject.get("longitude"));
                String description = (String) jsonObject.get("description");
                String imageName=(String) jsonObject.get("imageName");
                String reportID = (String) jsonObject.get("_id");
                String userID = (String) jsonObject.get("userID");
                long confirms = Long.parseLong(jsonObject.get("confirmedBy").toString());
                long denies = Long.parseLong(jsonObject.get("deniedBy").toString());
                boolean hasConfirmed = Boolean.parseBoolean(jsonObject.get("hasConfirmed").toString());
                boolean hasDenied = Boolean.parseBoolean(jsonObject.get("hasDenied").toString());
                long severityWeight = Long.parseLong(jsonObject.get("severityWeight").toString());
                JSONArray usersConfirmed = (JSONArray) jsonObject.get("usersConfirmed");
                ArrayList<String> usersConfirmedList = new ArrayList<String>();
                String usersConfirmedL = usersConfirmed.toString();
                usersConfirmedList.add(usersConfirmedL);
                JSONArray usersDenied = (JSONArray) jsonObject.get("usersDenied");
                ArrayList<String> usersDeniedList = new ArrayList<String>();
                String usersDeniedL = usersDenied.toString();
                usersDeniedList.add(usersDeniedL);
                Report report = new Report(category, timestampInMillis, subcategory, latitude, longitude, description, imageName, reportID, confirms, denies, hasConfirmed, hasDenied, severityWeight, userID, usersConfirmedList, usersDeniedList);
                reportsListManager.addReport(report);
            }
        } catch (ExecutionException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadImages(){
        for(int i=0;i<reportsListManager.size();i++){
            Report report=reportsListManager.get(i);
            String imgName=report.getImageName();
            if(!imgName.equals("")){
                new Thread(()->{
                    report.setImageDrawable(API.loadImage(imgName));
                    handler.post(()->{
                        customAdapter.notifyDataSetChanged();
                    });
                }).start();
            }
        }
    }

    private class CustomAdapter extends ArrayAdapter<Report> {
        private final String TAG="Security";
        private final String ACTIVITY="CustomAdapter: ";

        Activity context;
        List<Report> reportsList;
        private ImageView imageView;
        private String[] categories;
        private String[][] subcategories;

        public CustomAdapter(Activity context, List<Report>reportsList){
            super(context, R.layout.report_listview, reportsList);
            this.context = context;
            this.reportsList = reportsList;
            categories = context.getResources().getStringArray(R.array.categories);
            subcategories = new String[][]{
                    context.getResources().getStringArray(R.array.crime),
                    context.getResources().getStringArray(R.array.suspicious),
                    context.getResources().getStringArray(R.array.environment),
                    context.getResources().getStringArray(R.array.infrastructure),
                    context.getResources().getStringArray(R.array.mobility)
            };
        }

        public Report getItem(int position) {
            return reportsList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.report_listview, null, true);
            Report report = reportsList.get(position);

            TextView textView = row.findViewById(R.id.categoryTextV);
            textView.setText(categories[report.getCategoryID()].toUpperCase());
            textView = row.findViewById(R.id.dateTextV);
            textView.setText(toDate(report.getDate()));
            textView = row.findViewById(R.id.incidentTextV);
            textView.setText(subcategories[report.getCategoryID()][report.getIncident()].toUpperCase());
            textView = row.findViewById(R.id.locationTextV);
            Log.d(TAG, "Test"+ report.getLatitude()+ report.getLongitude());
            String address = findAddress(report.getLatitude(), report.getLongitude());
            String privateAddress = privatizeAddress(address);
            textView.setText(privateAddress);
            textView = row.findViewById(R.id.descriptionTextV);
            textView.setText(report.getDescription());
            //Paint Image View
            imageView = row.findViewById(R.id.report_image);
            String imgName=report.getImageName();
            Drawable imgDrawable=report.getImageDrawable();
            if(!imgName.equals("")){
                if(imgDrawable==null){
                    imageView.setVisibility(View.GONE);
                }else{
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(imgDrawable);
                }
            }
            return row;

        }

        private String findAddress(double latitude, double longitude){
            try {
                // Geocoder translates coordinates into a street name
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Log.d(TAG, "geocoder: " + latitude + " " + longitude);
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 2);
                Log.d(TAG, "geocoder: " + latitude + " " + longitude);
                if (addressList.size() == 0) {
                    return "Could not find address";
                }
                else {
                    return addressList.get(0).getAddressLine(0);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            return "Could not find address";
        }
        private String toDate(long time) {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(time));
        }

        private String privatizeAddress(String address) {
            String newAddress = "";
            for (int i = 0; i < address.length(); i++) {
                if (address.charAt(i) >= 48 && address.charAt(i) <= 57) {
                    newAddress = newAddress + "X";
                }
                else {
                    newAddress = newAddress + address.charAt(i);
                }
            }
            return newAddress;
        }
    }
}
