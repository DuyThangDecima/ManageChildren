package com.thangld.managechildren.main.child;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.storage.model.ChildModel;


public class ConfirmChildFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String FRAGMENT_TAG = "ConfirmChildFragment";

    private static final String ARG_FULL_NAME = "full_name";
    private static final String ARG_BIRTH = "birth";
    private static final String ARG_ID_SERVER = "id_server";

    private String mFullName;
    private String mIdServer;
    private int mBirth;

    private View view;
    private Activity mContext;
    private FragmentManager fragmentManager;


    private final int CODE_REQUEST_CONTACT = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_REQUEST_CONTACT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.grant_permission_to_continue));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            default:
                break;

        }
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

    public static ConfirmChildFragment newInstance(String fullName, int birth, String id_server) {
        ConfirmChildFragment fragment = new ConfirmChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FULL_NAME, fullName);
        args.putInt(ARG_BIRTH, birth);
        args.putString(ARG_ID_SERVER, id_server);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_confirm_child, container, false);
        fragmentManager = getFragmentManager();
        Button btnContinue = (Button) view.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Yêu cầu quyền
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS}, CODE_REQUEST_CONTACT
                    );
                    return;
                }

                // Dialog thong bao logout tai khoan
                final Dialog dialog = new Dialog(mContext);

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewInFlater = inflater.inflate(R.layout.dialog_confirm, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(viewInFlater);

                // Get TextView id and set error
                TextView text = (TextView) viewInFlater.findViewById(R.id.msg);
                text.setText(R.string.logout_other);
                Button btnCancel = (Button) viewInFlater.findViewById(R.id.cancel);
                Button btnOk = (Button) viewInFlater.findViewById(R.id.ok);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            ChildModel.QueryHelper.setChildIdActive(mContext, mIdServer);
                            SyncAccountTask syncAccountTask = new SyncAccountTask(mContext, mIdServer);
                            syncAccountTask.execute();

                        }

                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return view;
    }


}
