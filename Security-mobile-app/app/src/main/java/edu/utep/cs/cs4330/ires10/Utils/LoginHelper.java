package edu.utep.cs.cs4330.ires10.Utils;

/**
 * <h1> Login Helper </h1>
 *
 * Registration of users, retrieval of user login information and retrieval of database data
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class LoginHelper {
    private final String TAG="Security";
    private final String ACTIVITY="LoginHelper: ";

    private static LoginHelper loginHelper=null;
    private HttpClient API=new HttpClient();
    private JSONParser parser=new JSONParser();
    private LoginHelper.LoginHelperListener listener;

    public interface LoginHelperListener{
        void loginReturned(String result, String ID);
        void registerReturned(boolean authentication);

    }

    private LoginHelper() {
    }

    public static LoginHelper getInstance(){
        if(loginHelper==null){
            loginHelper=new LoginHelper();
        }
        return loginHelper;
    }

    public void setLoginHelperListener(LoginHelper.LoginHelperListener listener) {
        this.listener=listener;
    }

    public void tryLogin( Map<String,String> params){
        //Retrieve data from database
        new Thread(()->{
            String jsonStr=API.connection("POST","/users/login",params);
            Log.d(TAG,ACTIVITY+jsonStr); //------------------------------
            String result=jsonStringToResult(jsonStr);
            listener.loginReturned(result , params.get("betaTesterID"));
        }).start();
    }

    public void registerUser(Map<String,String> params){
        //Register a user in the database
        new Thread(()->{
            String jsonStr=API.connection("POST","/users/register",params);
            Log.d(TAG,ACTIVITY+jsonStr);
            boolean result = jsonStringToResult(jsonStr).equals("true") ? true : false;
            listener.registerReturned(result);
        }).start();
    }

    private String jsonStringToResult(String jsonStr){
        String result="";
        String message="";
        try {
            Object obj = parser.parse(jsonStr);
            JSONObject jsonObject = (JSONObject)obj;
            Log.d(TAG,ACTIVITY+jsonObject.toString());
            message=(String)jsonObject.get("message");
            result=(String)jsonObject.get("result");
            Log.d(TAG,ACTIVITY+message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;

    }
}
