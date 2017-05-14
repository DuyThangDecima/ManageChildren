package com.thangld.managechildren.main.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.thangld.managechildren.R;

public class ConnectFragment extends Fragment {
    public static String tag = "LoginFragment";
    private static View view;

    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    public ConnectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    // Set Listeners
    private void setListeners() {
    }
}
