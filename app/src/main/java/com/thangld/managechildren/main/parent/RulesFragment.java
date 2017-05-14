package com.thangld.managechildren.main.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.RuleParentModel;


/**
 * Fragment nay duoc su dung cho ca tre con va bo me
 */
public class RulesFragment extends Fragment {

    private View view;
    private Activity mActivity;
    private Switch switchSetTime;
    private Spinner hoursSpinner, minuteSpinner;
    private LinearLayout layoutSetTime;
    private ListView lvApp;
    private Cursor mCursor;

    private AppAdapter appAdapter;

    private boolean isSetTimeLimitAppOld;
    private long minuteOld;
    private long hoursOld;

    /**
     * quyen cua tre con hay bo me
     */
    private int privilege;


    public RulesFragment() {
        // Required empty public constructor
    }

    public static RulesFragment newInstance(String param1, String param2) {
        RulesFragment fragment = new RulesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rules, container, false);
        mActivity = getActivity();
        initViews();
        setViews();
        setListeners();
        // TODO child upload
        //        TransferService.startActionUpload(mActivity,TransferService.UPLOAD_RULE_PARENT);
        //        TransferService.startActionUpload(mActivity,TransferService.UPLOAD_APP);
        return view;
    }

    public void setListeners() {
        switchSetTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    layoutSetTime.setVisibility(View.VISIBLE);
                } else {
                    layoutSetTime.setVisibility(View.GONE);
                }
            }
        });

        lvApp.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, final View viewItem, final int i, final long id) {
                        // Dialog thong bao logout tai khoan

                        final String childId = ChildModel.QueryHelper.getChildIdActive(mActivity);

                        final Dialog dialog = new Dialog(mActivity);
                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewInFlater = inflater.inflate(R.layout.dialog_choose_type_app, null);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(viewInFlater);

                        // Get TextView id and set error
                        Button btnLimitTimeApp = (Button) viewInFlater.findViewById(R.id.limit_time_app);
                        Button btnBlockApp = (Button) viewInFlater.findViewById(R.id.ban_app);
                        Button btnNormalApp = (Button) viewInFlater.findViewById(R.id.normal_app);
                        btnLimitTimeApp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                    AppModel.AppHelper.setTypeApp(mActivity, childId, id, AppModel.Contents.TYPE_LIMIT_TIME_APP);
//                                    ((ImageView)viewItem.findViewById(R.id.type_app)).setImageResource(R.drawable.time_app);
                                    mCursor.requery();
                                    appAdapter.notifyDataSetChanged();
                                }

                            }
                        });
                        btnBlockApp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                    AppModel.AppHelper.setTypeApp(mActivity, childId, id, AppModel.Contents.TYPE_BAN_APP);
//                                    ((ImageView)viewItem.findViewById(R.id.type_app)).setImageResource(R.drawable.ban_app);
                                    mCursor.requery();
                                    appAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        btnNormalApp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialog != null && dialog.isShowing()) {
                                    AppModel.AppHelper.setTypeApp(mActivity, childId, id, AppModel.Contents.TYPE_NORMAL_APP);
//                                    ((ImageView)viewItem.findViewById(R.id.type_app)).setImageDrawable(null);
                                    mCursor.requery();
                                    appAdapter.notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            }
                        });
                        dialog.show();

                    }
                });
    }

    public void initViews() {
        hoursSpinner = (Spinner) view.findViewById(R.id.spinner_hours);
        minuteSpinner = (Spinner) view.findViewById(R.id.spinner_minute);
        switchSetTime = (Switch) view.findViewById(R.id.sw_limit_time_use);
        layoutSetTime = (LinearLayout) view.findViewById(R.id.layout_set_time);
        lvApp = (ListView) view.findViewById(R.id.lv_app);


    }

    public void setViews() {

        // Spinner cho hours va minute
        int index = 24;
        String[] hours = new String[index];
        for (int i = 0; i < index; i++) {
            hours[i] = String.valueOf(i);
        }

        index = 60;
        String[] minutes = new String[index];
        for (int i = 0; i < index; i++) {
            minutes[i] = String.valueOf(i);
        }

        ArrayAdapter spinnerHoursAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, hours);
        spinnerHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        hoursSpinner.setAdapter(spinnerHoursAdapter);


        ArrayAdapter spinnerMitunesAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, minutes);
        spinnerMitunesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        minuteSpinner.setAdapter(spinnerMitunesAdapter);


        /**
         * lấy thời gian được setup
         */

        String childId = ChildModel.QueryHelper.getChildIdActive(mActivity);

        isSetTimeLimitAppOld = RuleParentModel.RulesParentHelper.isSetTimeLimitApp(mActivity, childId);

        long limitTime = RuleParentModel.RulesParentHelper.getTimeLimitTimeApp(mActivity, childId);
        hoursOld = limitTime / 60;
        minuteOld = limitTime % 60;
        hoursSpinner.setSelection((int) hoursOld);
        minuteSpinner.setSelection((int) minuteOld);
        switchSetTime.setChecked(isSetTimeLimitAppOld);

        /**
         * Danh sach list app
         */
        if (childId != null) {
            //        mCursor = mActivity.getContentResolver().query(SmsModel.Contents.CONTENT_URI, null, null, null, null);
            DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
            String cmdSql =
                    "SELECT * "
                            + "FROM " + AppModel.Contents.TABLE_NAME + " "
                            + "WHERE " + AppModel.Contents.ID_CHILD + "= '" + childId + "'";
            mCursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);
            Log.d("mc_log", "SmsFragmen getCount()" + mCursor.getCount());

            mCursor.setNotificationUri(getContext().getContentResolver(), RuleParentModel.Contents.CONTENT_URI);
            appAdapter = new AppAdapter(mActivity, mCursor, true);

//            appAdapter.swapCursor(mCursor);

            lvApp.setAdapter(appAdapter);
        }
    }


    public class AppAdapter extends CursorAdapter {
        protected PackageManager packageManager;

        public AppAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            packageManager = context.getPackageManager();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView info = (TextView) view.findViewById(R.id.info);
            TextView tvAppName = (TextView) view.findViewById(R.id.title);
            ImageView imIconApp = (ImageView) view.findViewById(R.id.icon);
            ImageView imType = (ImageView) view.findViewById(R.id.type_app);
            // Extract properties from cursor
            String infoContent = cursor.getString(cursor.getColumnIndexOrThrow(AppModel.Contents.CATEGORY));
            String appName = cursor.getString(cursor.getColumnIndexOrThrow(AppModel.Contents.APP_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(AppModel.Contents.TYPE));

            tvAppName.setText(appName);
            String packageName = cursor.getString(cursor.getColumnIndexOrThrow(AppModel.Contents.PACKAGENAME));

            try {
                Drawable d = packageManager.getApplicationIcon(packageName);
                imIconApp.setImageDrawable(d);
            } catch (PackageManager.NameNotFoundException e) {
            }
            if (AppModel.Contents.TYPE_LIMIT_TIME_APP.equals(type)) {
                imType.setImageResource(R.drawable.time_app);
            } else if (AppModel.Contents.TYPE_BAN_APP.equals(type)) {
                imType.setImageResource(R.drawable.ban_app);
            } else {
                imType.setImageDrawable(null);
            }
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Luu
        Log.d("mc_log", "RulesFragment" + "onPause");
        int before = isSetTimeLimitAppOld == true ? 1 : 0;
        int isSetLimitTime = switchSetTime.isChecked() == true ? 1 : 0;
        long hours = Long.valueOf(hoursSpinner.getSelectedItem().toString());
        long minute = Long.valueOf(minuteSpinner.getSelectedItem().toString());

        if (before == isSetLimitTime & hoursOld == hours & minuteOld == minute) {
            return;
        } else {
            long time = hours * 60 + minute;
            final String childId = ChildModel.QueryHelper.getChildIdActive(mActivity);
            RuleParentModel.RulesParentHelper.setLimitAppTime(mActivity, childId, isSetLimitTime, time);
        }

    }
}
