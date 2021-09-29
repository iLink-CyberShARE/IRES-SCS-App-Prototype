package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Map Fragment </h1>
 *
 * Inflation of map fragment for display of user reports
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.HTTPHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private HTTPHandler httpHandler = new HTTPHandler();

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        WebView mapWebView = (WebView)view.findViewById(R.id.webView);
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mapWebView.loadUrl(httpHandler.getMapUrl());
        // Inflate the layout for this fragment
        return view;
    }

}
