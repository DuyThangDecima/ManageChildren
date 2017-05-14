package com.thangld.managechildren.main.child.accessibility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thangld.managechildren.R;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.child.ConfirmChildFragment;
import com.thangld.managechildren.main.child.deviceadmin.EnableUninstallFragment;
import com.thangld.managechildren.utils.Accessibility;

public class EnableAccessibilityFragment extends Fragment
        implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "EnableAccessibilityFragment";

    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_BIRTH = "birth";
    private static final String ARG_ID_SERVER = "id_server";
    private Activity mContext;

    private String mFullName;
    private String mIdServer;
    private int mBirth;

    private View view;
    private Button btnContinue;

    private ChildEntry mChildSelected;


    private int CODE_REQUEST_ACCESSIBILITY = 0;
    private int CODE_REQUEST_DEVICE_ADMIN = 1;
    private FragmentManager fragmentManager;

    public EnableAccessibilityFragment() {

    }

    public static EnableAccessibilityFragment newInstance(String fullName, int birth, String id_server) {
        EnableAccessibilityFragment fragment = new EnableAccessibilityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FULL_NAME, fullName);
        args.putInt(ARG_BIRTH, birth);
        args.putString(ARG_ID_SERVER, id_server);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFullName = getArguments().getString(ARG_FULL_NAME);
            mBirth = getArguments().getInt(ARG_BIRTH);
            mIdServer = getArguments().getString(ARG_ID_SERVER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enable_monitor, container, false);
        mContext = getActivity();
        fragmentManager = getFragmentManager();
        initView();
        setListeners();
        return view;
    }

    private void initView() {
        btnContinue = (Button) view.findViewById(R.id.btn_continue);
    }

    private void setListeners() {
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                // Hiển thị enable accessibily
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, CODE_REQUEST_ACCESSIBILITY);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Accessibility.isAccessibilitySettingsOn(mContext)) {
            // Khi bật accessibility thành công
            if (EnableUninstallFragment.isAdminActive(mContext)) {
                // check device admin da duoc bat chua,neu chua hien thi bat
                fragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, ConfirmChildFragment.newInstance(mFullName, mBirth, mIdServer))
                        .addToBackStack(ConfirmChildFragment.FRAGMENT_TAG)
                        .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                        .commit();

            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, EnableUninstallFragment.newInstance(mFullName, mBirth, mIdServer))
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                        .commit();
            }
            // Nếu chưa thì hiển thị
        } else {
            // giu nguyen activity, khong lam gif
        }
    }


}
