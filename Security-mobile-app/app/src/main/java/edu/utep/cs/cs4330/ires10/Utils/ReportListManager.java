package edu.utep.cs.cs4330.ires10.Utils;

/**
 * <h1> Report List Manager </h1>
 *
 * Manager of report information
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.ires10.Model.Report;

public class ReportListManager {

    private List<Report> reportsList;

    public ReportListManager(){
        reportsList = new ArrayList<>();
    }
    public List<Report> getReports(){
        return reportsList;
    }
    public void setItems(List<Report> itemList){
        this.reportsList = itemList;
    }

    public int size(){
        return reportsList.size();
    }
    public void addReport(long date, double latitude, double longitude){
        reportsList.add(new Report(date, latitude, longitude));
    }
    public void addReport(Report report){
        reportsList.add(report);
    }
    public boolean contains(Report report){
        return reportsList.contains(report);
    }

    public Report get(int index){
        return reportsList.get(index);
    }
    public void remove(int index){
        reportsList.remove(index);
    }
    public void clear(){
        reportsList.clear();
    }
}