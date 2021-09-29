package edu.utep.cs.cs4330.ires10.Utils;

/**
 * <h1> My Async Task </h1>
 *
 * Sending of report information
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import edu.utep.cs.cs4330.ires10.Utils.HTTPHandler;
import edu.utep.cs.cs4330.ires10.Utils.HttpClient;

public class MyAsyncTask extends AsyncTask<Object,String,String> {
    private final String TAG="Security";
    private final String ACTIVITY="MyAsyncTask: ";

    private Context context;
    String data = null;
    private HTTPHandler httpHandler;
    private HttpClient httpClient=new HttpClient();

    public MyAsyncTask(Context context) {  // can take other params if needed
        this.context = context;
        this.httpHandler = new HTTPHandler();

    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            if (params[0].equals("alert")) {
                // POST Request
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("latitude", params[1]);
                postDataParams.put("longitude", params[2]);
                postDataParams.put("timestamp", params[3]);

                return HTTPHandler.sendPost(httpHandler.getReportsUrl() + "/alert", postDataParams);
            }
            else if(params[0].equals("GET")){
                //GET Request
                return data = HTTPHandler.sendGet(httpHandler.getReportsUrl() + "/reports?categoryID=" + params[1] ); // params[1] contains the report type; to query all reports '/^'
            }
            else {
                // POST Request for all other type of reports
                JSONObject postDataParams = putInJSON(params);
                Log.d(TAG,ACTIVITY+postDataParams.toString());
                return HTTPHandler.sendPost(httpHandler.getReportsUrl() + "/createReport", postDataParams); // params[0] contain the type of report
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            data = s;
        }
    }

    private JSONObject putInJSON(Object...params){
        JSONObject postDataParams = null;
        String imgName = "";
        if(!params[6].equals("")){
            Log.d(TAG,ACTIVITY+params[6]);
            imgName=httpClient.uploadImage(context, (String) params[6]);
            Log.d(TAG,ACTIVITY+imgName);
        }
        try {
            postDataParams = new JSONObject();
            postDataParams.put("categoryID", params[0]);
            postDataParams.put("latitude", params[1]);
            postDataParams.put("longitude", params[2]);
            postDataParams.put("timestamp", params[3]);
            postDataParams.put("incident", params[4]);
            postDataParams.put("description", params[5]);
            postDataParams.put("imagePath", params[6]);
            postDataParams.put("userID", params[7]);
            postDataParams.put("hasConfirmed", params[8]);
            postDataParams.put("hasDenied", params[9]);
            postDataParams.put("confirmedBy", params[10]);
            postDataParams.put("deniedBy", params[11]);
            postDataParams.put("severityWeight", params[12]);
            postDataParams.put("imageName", imgName);
            return postDataParams;
        } catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return postDataParams;
    }
}