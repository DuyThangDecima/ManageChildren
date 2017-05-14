package com.thangld.managechildren.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;

import com.thangld.managechildren.R;


public class NetworkErrorFragment extends Fragment implements View.OnClickListener {
    public static String tag = "NetworkErrorFragment";
    private static View view;

    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    private Button btnRetry;

    public NetworkErrorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_network_error, container, false);

        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();
        btnRetry = (Button) view.findViewById(R.id.btn_retry);
    }

    // Set Listeners
    private void setListeners() {
        btnRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
                break;
        }
    }
}
