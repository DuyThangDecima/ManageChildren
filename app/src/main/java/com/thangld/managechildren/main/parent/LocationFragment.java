package com.thangld.managechildren.main.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.resource.LocationResource;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.LocationModel;
import com.thangld.managechildren.utils.DateUtils;
import com.thangld.managechildren.utils.DialogCustom;

import java.util.Locale;

public class LocationFragment extends Fragment implements View.OnClickListener {

    private GridView listViewContainer;
    private View view;
    private Activity mActivity;
    private Cursor mCursor;
    private CursorAdapter cursorAdapter;
    private FloatingActionButton btnRequestLocation;
    private Dialog dialogRetrieving;
    private UIBroadcastReceiver uiBroadcastReceiver;

    public LocationFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mActivity = getActivity();
        view = inflater.inflate(R.layout.fragment_location, container, false);
        registerBroadcastUi();
        initViews();
        setClickListeners();
        setAdapterContent();
        return view;
    }

    private void initViews() {
        listViewContainer = (GridView) view.findViewById(R.id.lv_nav_container);
        listViewContainer = (GridView) view.findViewById(R.id.lv_nav_container);
        btnRequestLocation = (FloatingActionButton) view.findViewById(R.id.btn_request_location);

    }

    private void registerBroadcastUi() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UIBroadcastReceiver.ACTION_UI);
        uiBroadcastReceiver = new UIBroadcastReceiver();
        mActivity.registerReceiver(uiBroadcastReceiver, intentFilter);

    }

    protected void setAdapterContent() {
        String childId = ChildModel.QueryHelper.getChildIdActive(mActivity);

        if (childId != null) {
            //        mCursor = mActivity.getContentResolver().query(SmsModel.Contents.CONTENT_URI, null, null, null, null);
            DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);

            mCursor = mActivity.getContentResolver().query(
                    LocationModel.Contents.CONTENT_URI,
                    null,
                    null,
                    null,
                    LocationModel.Contents.DATE + " DESC"
            );
            Log.d("mc_log", "Location getCount()" + mCursor.getCount());

            mCursor.setNotificationUri(getContext().getContentResolver(), LocationModel.Contents.CONTENT_URI);
            cursorAdapter = new LocationAdapter(mActivity, mCursor, true);
            cursorAdapter.swapCursor(mCursor);
            listViewContainer.setAdapter(cursorAdapter);
        }
    }

    protected void setClickListeners() {
        btnRequestLocation.setOnClickListener(this);
        listViewContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_location:
                if (dialogRetrieving != null && dialogRetrieving.isShowing()) {
                    dialogRetrieving.dismiss();
                    dialogRetrieving = null;
                }
                dialogRetrieving = DialogCustom.showLoadingDialog(mActivity, getString(R.string.retrieving_location));

                GetLocationTask task = new GetLocationTask(mActivity);
                task.execute();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogRetrieving != null && dialogRetrieving.isShowing()) {
                            dialogRetrieving.dismiss();
                            new CustomToast().showToast(mActivity, R.drawable.error, getString(R.string.error_try_again));
                        }
                    }
                }, 3 * 60 * 1000);
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(uiBroadcastReceiver);

    }

    public class UIBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_UI = "ACTION_UI";

        public static final String EXTRA_TYPE = "extra_type";
        public static final String SYNC_ACCOUNT_TYPE = "sync_account_type";
        public static final String UPDATE_LOCATION = "update_location";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Khi nhan duoc goi tin nay
            String action = intent.getAction();
            Log.d("mc_log", "UIBroadcastReceiver " + action);
            if (ACTION_UI.equals(action)) {
                if (dialogRetrieving != null && dialogRetrieving.isShowing()) {
                    dialogRetrieving.dismiss();
                }
                // Hien thi cap
                String address = intent.getExtras().getString(LocationModel.Contents.ADDRESS);
                final String latitude = intent.getExtras().getString(LocationModel.Contents.LATITUDE);
                final String longitude = intent.getExtras().getString(LocationModel.Contents.LONGITUDE);

                Log.d("mc_log", "UIBroadcastReceiver " + address);
                final Dialog dialog = new Dialog(mActivity);

                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewInFlater = inflater.inflate(R.layout.dialog_confirm, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(viewInFlater);

                // Get TextView id and set error
                TextView text = (TextView) viewInFlater.findViewById(R.id.msg);
                text.setText(getString(R.string.current_location, address));
                Button btnCancel = (Button) viewInFlater.findViewById(R.id.cancel);
                Button btnOk = (Button) viewInFlater.findViewById(R.id.ok);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            // Lay vi tri hien thi
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                            Intent intentGoogleMap = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            mActivity.startActivity(intentGoogleMap);
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
        }
    }


    public class GetLocationTask extends AsyncTask<Void, Void, Void> {

        private Context mActivity;

        public GetLocationTask(Context context) {
            super();
            this.mActivity = context;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Gui request server, yeu cau lay vi tri cua child nay
            new LocationResource(mActivity).requestLocation();
            return null;
        }
    }

    public class LocationAdapter extends CursorAdapter {
        protected static final String TITLE = "title";
        protected static final String INFO = "infor";


        public LocationAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_lv_nav_container, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView info = (TextView) view.findViewById(R.id.info);
            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            imageView.setImageResource(R.drawable.location);
            // Extract properties from cursor
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(LocationModel.Contents.DATE));

            info.setText(DateUtils.getDate(date, "dd/MM/yyyy hh:mm:ss"));
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow(LocationModel.Contents.ADDRESS)));
        }
    }
}
