package edu.utep.cs.cs4330.ires10.Utils;

/**
 * <h1> Http Client </h1>
 *
 * Client connection and use of openweathermap for weather report
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.google.gson.JsonObject;

import id.zelory.compressor.*;

public class HttpClient {
    private final String TAG="Security";
    private final String ACTIVITY="HttpClient: ";

    //private String urlStr;
    private String APPID;
    private HTTPHandler httpHandler;

    public HttpClient(){
        this.APPID="enterYourAppID";
        this.httpHandler = new HTTPHandler();

    }

    public String connection(String method, String API, Map<String,String> params) {
        String line=null;
        StringBuffer buffer=new StringBuffer();
        HttpURLConnection con = null;
        try {
            URL url=null;
            if (API.contains("updateConfirm")){
                url= new URL(httpHandler.getReportsUrl()+API);
            }
            else if (API.contains("updateDeny")){
                url= new URL(httpHandler.getReportsUrl()+API);
            }
            else{
                switch(API){
                    case "/weather":
                    case "/forecast":
                        url= new URL("http://api.openweathermap.org/data/2.5"+API+"?"+params2Str(params)+"&APPID="+APPID);
                        break;
                    case "/reports/retrieve":
                        url= new URL(httpHandler.getMapUrl()+API+"?"+params2Str(params));
                        break;
                    default:
                        url= new URL(httpHandler.getMapUrl()+API);
                        break;
                }
            }

            Log.d(TAG,ACTIVITY+url.toString());
            con = (HttpURLConnection) url.openConnection();
            // optional default is GET
            con.setRequestMethod(method);
            con.setRequestProperty("User-Agent", "...");
            if(method.equals("POST") || method.equals("PUT")){
                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(params2Str(params));
                wr.flush();
                wr.close();
            }
            String encoding = con.getContentEncoding();
            if (encoding == null) { encoding = "utf-8"; }
            InputStreamReader reader = null;
            if ("gzip".equals(encoding)) { // gzipped document?
                reader = new InputStreamReader(new GZIPInputStream(con.getInputStream()));
            } else {
                reader = new InputStreamReader(con.getInputStream(), encoding);
            }
            BufferedReader in = new BufferedReader(reader);
            while ((line = in.readLine()) != null ) {
                buffer.append(line + "\r\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            Log.d(TAG,ACTIVITY+buffer.toString());
            return buffer.toString();
        }
    }

    public String uploadImage(Context context, String imgPath){
        String urlString =httpHandler.getReportsUrl()+"/upload";
        File file=new File(imgPath);
        File compressedImgFile = new File(imgPath);
        try {
            compressedImgFile = new Compressor(context).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Ion.with(context)
                .load(urlString)
                .setMultipartFile("file", "image/*", compressedImgFile)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(TAG,ACTIVITY+"Successful"+result.toString());
                    }
                });
        return file.getName();
    }

    public Drawable loadImage(String imgName){
        String urlString =httpHandler.getReportsUrl()+"/download/"+imgName;
        Drawable image = null;
        Log.d(TAG,ACTIVITY+urlString);
        try {
            URL url = new URL(urlString);
            InputStream is = (InputStream)url.getContent();
            image = Drawable.createFromStream(is, "src");
        } catch (MalformedURLException e) {
            // handle URL exception
            image = null;
        } catch (IOException e) {
            // handle InputStream exception
            image = null;
        }
        return image;
    }

    private String params2Str(Map<String,String>params){
        // using for-each loop for iteration over Map.entrySet()
        String paramsStr="";
        for (Map.Entry<String,String> entry : params.entrySet()){
            paramsStr+=(entry.getKey()+"="+entry.getValue()+"&");
        }
        paramsStr=paramsStr.substring(0,paramsStr.length()-1);
        return paramsStr;
    }

}
