package com.thangld.managechildren.main.child.deviceadmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.thangld.managechildren.R;
import com.thangld.managechildren.main.child.ConfirmChildFragment;

public class EnableUninstallFragment extends Fragment implements View.OnClickListener {

    View view;
    private Button btnContinue;
    private Activity mContext;
    private DevicePolicyManager mDPM;
    ComponentName mAdminName;

    private FragmentManager fragmentManager;

    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_BIRTH = "birth";
    private static final String ARG_ID_SERVER = "id_server";

    public static final String FRAGMENT_TAG = "EnableUninstallFragment";


    private String mFullName;
    private String mIdServer;
    private int mBirth;

    public EnableUninstallFragment() {
        // Required empty public constructor
    }

    public static EnableUninstallFragment newInstance(String fullName, int birth, String id_server) {
        EnableUninstallFragment fragment = new EnableUninstallFragment();
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

    private void initView() {
        btnContinue = (Button) view.findViewById(R.id.btn_continue);
    }

    private void setListeners() {
        btnContinue.setOnClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enable_uninstall, container, false);
        mContext = getActivity();
        fragmentManager = getFragmentManager();
        mDPM = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(mContext, AppDeviceAdminReceiver.class);

        initView();
        setListeners();
        return view;
    }

    int REQUEST_CODE_ENABLE_ADMIN = 1;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                if (!mDPM.isAdminActive(mAdminName)) {

                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.accessibility_description));
                    mContext.startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameContainer, ConfirmChildFragment.newInstance(mFullName, mBirth, mIdServer))
                            .addToBackStack(ConfirmChildFragment.FRAGMENT_TAG)
                            .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                            .commit();//
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDPM.isAdminActive(mAdminName)) {
            // Tiếp tục vào commit
            fragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, ConfirmChildFragment.newInstance(mFullName, mBirth, mIdServer))
                    .addToBackStack(ConfirmChildFragment.FRAGMENT_TAG)
                    .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                    .commit();//
        } else {

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(mContext, "onActivityResult", Toast.LENGTH_LONG).show();


    }


    public static boolean isAdminActive(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, AppDeviceAdminReceiver.class);
        return mDPM.isAdminActive(mAdminName);
    }
}
