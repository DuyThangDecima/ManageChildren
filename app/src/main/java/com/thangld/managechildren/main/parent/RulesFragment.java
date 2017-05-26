package com.thangld.managechildren.main.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.TransferService;
import com.thangld.managechildren.entry.AppEntry;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.RuleParentModel;
import com.thangld.managechildren.utils.DialogCustom;

import java.util.ArrayList;
import java.util.List;


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

    private Dialog mWaitingSyncDialog;
    private AppAdapter appAdapter;

    private boolean isSetTimeLimitAppOld;
    private long minuteOld;
    private long hoursOld;

    private String childId;

    private UIBroadcast mUIBroadcast;
    private int countNumberBroadcastReceive = 0;
    /**
     * Lưu danh sách trước khi vào giao diện
     */
    private List<AppEntry> appEntryBefore;
    /**
     * Lưu danh sách khi thoát giao diện.
     */
    private List<AppEntry> appEntryAfter;
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


    public void download() {
        TransferService.startActionDownload(mActivity, TransferService.DOWNLOAD_APP);
        TransferService.startActionDownload(mActivity, TransferService.DOWNLOAD_RULE_PARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rules, container, false);
        mActivity = getActivity();
        // Khoi tao lai 2 bien, appEntryBefore & appEntryAfter
        if (appEntryBefore != null) {
            appEntryBefore.clear();
        } else {
            appEntryBefore = new ArrayList<>();
        }
        if (appEntryAfter != null) {
            appEntryAfter.clear();
        } else {
            appEntryAfter = new ArrayList<>();
        }
        // Thuc hien download dau tien
        download();
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

        childId = ChildModel.QueryHelper.getChildIdActive(mActivity);

//        isSetTimeLimitAppOld = RuleParentModel.RulesParentHelper.isSetTimeLimitApp(mActivity, childId);

        Cursor cursor = mActivity.getContentResolver().query(
                RuleParentModel.Contents.CONTENT_URI,
                null,
                RuleParentModel.Contents.ID_CHILD + " = ?",
                new String[]{childId},
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int value = cursor.getInt(cursor.getColumnIndex(RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP));
            if (value == 1) {
                isSetTimeLimitAppOld = true;
            } else {
                isSetTimeLimitAppOld = false;
            }
            long limitTime = RuleParentModel.RulesParentHelper.getTimeLimitTimeApp(mActivity, childId);
            hoursOld = limitTime / 60;
            minuteOld = limitTime % 60;
            hoursSpinner.setSelection((int) hoursOld);
            minuteSpinner.setSelection((int) minuteOld);
            switchSetTime.setChecked(isSetTimeLimitAppOld);
        } else {
            PreferencesController preferencesController = new PreferencesController(mActivity);

            if (preferencesController.getPrivilege() == PreferencesController.PRIVILEGE_CHILD) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(RuleParentModel.Contents.ID_CHILD, childId);
                contentValues.put(RuleParentModel.Contents.IS_SET_TIME_LIMIT_APP, 1);
                contentValues.put(RuleParentModel.Contents.TIME_LIMIT_APP, 120);
                contentValues.put(RuleParentModel.Contents.IS_BACKUP, Constant.BACKUP_FALSE);
                mActivity.getContentResolver().insert(
                        RuleParentModel.Contents.CONTENT_URI,
                        contentValues
                );
                // Trả về -1 nghĩa là không get được
                isSetTimeLimitAppOld = true;
            } else if (preferencesController.getPrivilege() == PreferencesController.PRIVILEGE_PARENT) {
                // Neu la bo me ko co thì cho download
                if (mWaitingSyncDialog == null) {
                    mWaitingSyncDialog = DialogCustom.showLoadingDialog(mActivity, getString(R.string.performing_data_sync));
                } else {
                    if (!mWaitingSyncDialog.isShowing()) {
                        mWaitingSyncDialog.show();
                    }
                }
            }
        }


        /**
         * Danh sach list app
         */
        if (childId != null) {
            //        mCursor = mActivity.getContentResolver().query(SmsModel.Contents.CONTENT_URI, null, null, null, null);
            mCursor = getAppEntry();
            if (mCursor != null) {
                mCursor.setNotificationUri(mActivity.getContentResolver(), AppModel.Contents.CONTENT_URI);
                Log.d("mc_log", "SmsFragmen getCount()" + mCursor.getCount());
                mCursor.setNotificationUri(mActivity.getContentResolver(), RuleParentModel.Contents.CONTENT_URI);
                if (mCursor.moveToNext()) {
                    do {
                        String packageName = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.PACKAGENAME));
                        String appName = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.APP_NAME));
                        String idServer = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.ID_SERVER));
                        String idChild = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.ID_CHILD));
                        String type = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.TYPE));
                        AppEntry appEntry = new AppEntry(packageName, appName, idServer, idChild, type);
                        appEntryBefore.add(appEntry);
                    } while (mCursor.moveToNext());
                }
            }
            appAdapter = new AppAdapter(mActivity, mCursor, true);
            lvApp.setAdapter(appAdapter);
        }
    }


    private Cursor getAppEntry() {
        Cursor cursor;
        DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
        String cmdSql =
                "SELECT * "
                        + "FROM " + AppModel.Contents.TABLE_NAME + " "
                        + "WHERE " + AppModel.Contents.ID_CHILD + "= '" + childId + "'";
        cursor = databaseHelper.getWritableDatabase().rawQuery(cmdSql, null);
        return cursor;
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
            Drawable d;
            try {
                d = packageManager.getApplicationIcon(packageName);

            } catch (PackageManager.NameNotFoundException e) {
                d = getResources().getDrawable(R.drawable.app_default);
            }
            imIconApp.setImageDrawable(d);
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

        if (before != isSetLimitTime || hoursOld != hours || minuteOld != minute) {
            Log.d("mc_log","isTimeChanged = true");
            long time = hours * 60 + minute;
            final String childId = ChildModel.QueryHelper.getChildIdActive(mActivity);
            RuleParentModel.RulesParentHelper.setLimitAppTime(mActivity, childId, isSetLimitTime, time);
            TransferService.startActionUpload(mActivity, TransferService.UPLOAD_RULE_PARENT);
        }
        mCursor = getAppEntry();
        if (mCursor.moveToNext()) {
            do {
                String packageName = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.PACKAGENAME));
                String appName = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.APP_NAME));
                String idServer = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.ID_SERVER));
                String idChild = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.ID_CHILD));
                String type = mCursor.getString(mCursor.getColumnIndex(AppModel.Contents.TYPE));
                AppEntry appEntry = new AppEntry(packageName, appName, idServer, idChild, type);
                appEntryAfter.add(appEntry);
            } while (mCursor.moveToNext());
        }
        boolean isChanged = false;
        if (appEntryBefore.size() == appEntryAfter.size()) {
            for (int i = 0; i < appEntryBefore.size(); i++) {
                if (appEntryBefore.get(i).getType() != appEntryAfter.get(i).getType()) {
                    isChanged = true;
                    break;
                }
            }
        } else {
            isChanged = true;
        }
        Log.d("mc_log","isAppChanged = " + isChanged);
        if (isChanged) {
            // Thuc hien backup
            TransferService.startActionUpload(mActivity, TransferService.UPLOAD_APP);
        }
    }


    public class UIBroadcast extends BroadcastReceiver {
        public static final String ACTION_RULE_PARENT = "action_rule_parent";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("mc_log", "UIBroadcastReceiver " + action);
            if (ACTION_RULE_PARENT.equals(action)) {
                countNumberBroadcastReceive++;
                if (mWaitingSyncDialog != null && mWaitingSyncDialog.isShowing() && countNumberBroadcastReceive >= 2) {
                    mWaitingSyncDialog.dismiss();
                    countNumberBroadcastReceive = 0;
                }
                setViews();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUIBroadcast == null) {
            mUIBroadcast = new UIBroadcast();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UIBroadcast.ACTION_RULE_PARENT);
        getActivity().registerReceiver(mUIBroadcast, intentFilter);

    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.unregisterReceiver(mUIBroadcast);
    }
}
