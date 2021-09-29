package edu.utep.cs.cs4330.ires10.Model;

/**
 * <h1> Report </h1>
 *
 * Parameters for the user reports
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.graphics.drawable.Drawable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Report {

    private String userID;
    private int categoryID;
    private long date;
    private int incident;
    private double latitude;
    private double longitude;
    private String description;
    private String imageName;
    private String reportID;
    private long confirms;
    private long denies;
    private boolean hasConfirmed;
    private boolean hasDenied;
    private long severityWeight;
    private ArrayList<String> usersConfirmed;
    private ArrayList<String> usersDenied;
    private Drawable imageDrawable=null;

    //Alert constructor
    public Report(long date, double latitude, double longitude){
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //All type of Reports constructor
    public Report(int categoryID, long date, int incident, double latitude, double longitude, String description, String imageName, String reportID, long confirms, long denies, boolean hasConfirmed, boolean hasDenied, long severityWeight, String userID, ArrayList<String> usersConfirmed, ArrayList<String> usersDenied){
        this.categoryID = categoryID;
        this.date = date;
        this.incident = incident;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageName= imageName;
        this.reportID = reportID;
        this.confirms = confirms;
        this.denies = denies;
        this.hasDenied = hasDenied;
        this.hasConfirmed = hasConfirmed;
        this.severityWeight = severityWeight;
        this.userID = userID;
        this.usersConfirmed = usersConfirmed;
        this.usersDenied = usersDenied;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategory(int category) {
        this.categoryID = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getIncident() {
        return incident;
    }

    public void setIncident(int incident) {
        this.incident = incident;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable=imageDrawable;
    }

    public String getReportID() {return reportID;}

    public void setReportID(String reportID) {this.reportID = reportID;}

    public void setConfirms(long confirms) {this.confirms = confirms;}

    public long getConfirms() {return confirms;}

    public void setDenies(long denies) {this.denies = denies;}

    public long getDenies() {return denies;}

    public void setHasConfirmed(boolean value) {this.hasConfirmed = value;}

    public boolean getHasConfirmed() {return this.hasConfirmed;}

    public void setHasDenied(boolean value) {this.hasDenied = value;}

    public boolean getHasDenied() {return this.hasDenied;}

    public void setSeverityWeight(long severityWeight) {this.severityWeight = severityWeight;}

    public long getSeverityWeight() {return severityWeight;}

    public String getUserID() {return this.userID;}

    public void setUserID(String userID) {this.userID = userID;}

    public ArrayList<String> getUsersConfirmed() {return usersConfirmed;}

    public void setUsersConfirmed(ArrayList<String> usersConfirmed) {this.usersConfirmed = usersConfirmed;}

    public ArrayList<String> getUsersDenied() {return usersDenied;}

    public void setUsersDenied(ArrayList<String> usersDenied) {this.usersDenied = usersDenied;}


}
