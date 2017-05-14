package com.thangld.managechildren.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.cloud.resource.ChildResource;
import com.thangld.managechildren.cloud.resource.TokenFcmResource;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.account.AccountActivity;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.main.parent.AudioFragment;
import com.thangld.managechildren.main.parent.CallLogFragment;
import com.thangld.managechildren.main.parent.ContactFragment;
import com.thangld.managechildren.main.parent.ImageFragment;
import com.thangld.managechildren.main.parent.ListChildFragment;
import com.thangld.managechildren.main.parent.LocationFragment;
import com.thangld.managechildren.main.parent.NotificationFragment;
import com.thangld.managechildren.main.parent.RulesFragment;
import com.thangld.managechildren.main.parent.SmsFragment;
import com.thangld.managechildren.main.parent.VideoFragment;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;
import com.thangld.managechildren.utils.DialogCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class PanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationView.OnClickListener {

    private NavigationView navigationView;

    private View headerNavView;
    private ImageButton redirectionMenu;
    private TextView nameExtra;
    private TextView nameActive;


    private Toolbar toolbar;
    private boolean isMenuListChild;

    private Activity mContext;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;
    private UIBroadcastReceiver mUiBroadcastReceiver;

    private ArrayList<ChildEntry> mChildList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initViews();
        setListeners();
        mContext = (Activity) this;

        refreshListChild();

        Fragment fragment = new ListChildFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_container, fragment, ListChildFragment.FRAGMENT_TAG)
                .commit();
        // TODO TEST
        // TODO Duoc su dung khi chap nhan la tai khoan cua bome
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Dong bo tai khoan

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
                    jsonObject.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(mContext));
                    jsonObject.put(UrlPattern.TOKEN_KEY, new PreferencesController(mContext).getToken());
                    jsonObject.put(UrlPattern.PRIVILEGE_KEY, UrlPattern.PRIVILEGE_PARENT);
                    AccountResource.syncAccount(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        // Chưa có dữ liệu gì về trẻ con, phải chờ đồng bộ
        // Hiển thị thông báo chờ dồng bộ
        GetListChild getListChild = new GetListChild(this);
        getListChild.execute();

    }

    private void initViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ChildEntry child;
                int countSet = 0;
                if (mChildList.size() == 0) {
                    ((ImageView) findViewById(R.id.avatar_active)).setVisibility(View.INVISIBLE);
                } else if (mChildList.size() == 1) {
                    child = mChildList.get(0);
                    nameActive.setText(child.getFullName());
                } else {
                    ((ImageView) findViewById(R.id.avatar_extra)).setVisibility(View.VISIBLE);
                    for (int i = 0; i < mChildList.size(); i++) {
                        if (countSet >= 2)
                            break;
                        child = mChildList.get(i);
                        if (child.getIsActive() == 1) {
                            nameActive.setText(child.getFullName());
                            countSet++;
                        } else {
                            nameExtra.setText(child.getFullName());
                            countSet++;
                        }
                    }
                }


//                Cursor cursor = mContext.getContentResolver().query(ChildModel.Contents.CONTENT_URI,
//                        null,
//                        ChildModel.Contents.ACTIVE + "=?",
//                        new String[]{String.valueOf(1)},
//                        null
//                );
//
//                if (cursor.getCount() <= 0) {
//
//                } else {
//                    // Lấy thông tin
//                    cursor.moveToFirst();
//                    String fullName = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.FULL_NAME));
//                    nameChoose.setText(fullName);
//
////                    if (cursor.moveToFirst()) {
////                        fullName = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.FULL_NAME));
////                        nameExtra.setText(fullName);
////                    }

//                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerNavView = navigationView.getHeaderView(0);


        redirectionMenu = (ImageButton) headerNavView.findViewById(R.id.redirection_menu);
        nameActive = (TextView) headerNavView.findViewById(R.id.name_active);
        nameExtra = (TextView) headerNavView.findViewById(R.id.name_extra);
        isMenuListChild = false;


    }

    private void setListeners() {
        redirectionMenu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();
        String device_id = ChildModel.QueryHelper.getDeviceIdChildActive(mContext);

        if (id == R.id.nav_rules) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new RulesFragment();
            }
            replaceFragmentInNavContainer(fragment);
        } else if (id == R.id.nav_sms) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new SmsFragment();
            }
            replaceFragmentInNavContainer(fragment);
        } else if (id == R.id.nav_contact) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new ContactFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_callog) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new CallLogFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_location) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new LocationFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_photo) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new ImageFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_video) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new VideoFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_audio) {
            if (device_id == null || ChildModel.Contents.NO_LOGIN_BEFORE.equals(device_id)) {
                fragment = new NotificationFragment();
            } else {
                fragment = new AudioFragment();
            }
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_logout) {
            AccountResource.setLogout(mContext);
        } else if (id == R.id.nav_setting) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragmentInNavContainer(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.navigation_container, fragment)
                    .commit();
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redirection_menu:
                isMenuListChild = !isMenuListChild;
                Menu menu = navigationView.getMenu();
                menu.clear();
                if (isMenuListChild) {
                    redirectionMenu.setImageResource(R.drawable.arrow_up);
                    for (ChildEntry child : mChildList) {
                        menu.add(child.getFullName()).setIcon(R.drawable.child_drak_grey);
                    }
                } else {
                    redirectionMenu.setImageResource(R.drawable.arrow_down);
                    navigationView.inflateMenu(R.menu.activity_panel_drawer);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUiBroadcastReceiver == null) {
            mUiBroadcastReceiver = new UIBroadcastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(UIBroadcastReceiver.ACTION_UI);
        registerReceiver(mUiBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUiBroadcastReceiver != null) {
            unregisterReceiver(mUiBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class UIBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_UI = "ACTION_UI";

        public static final String EXTRA_TYPE = "extra_type";
        public static final String SYNC_ACCOUNT_TYPE = "sync_account_type";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Khi nhan duoc goi tin nay
            String type = intent.getStringExtra(EXTRA_TYPE);
            if (SYNC_ACCOUNT_TYPE.equals(SYNC_ACCOUNT_TYPE)) {

//                // Kiểm tra xem trạng thái đăng nhập
//                PreferencesController preferences = new PreferencesController(context);
//                boolean is_login_now = intent.getExtras().getBoolean("is_login");
//                if (!is_login_now) {
//                    new CustomToast().showToast(mContext, R.drawable.error, getString(R.string.session_expired_login_again));
//                    AccountResource.setLogout(context);
//                    Intent intentLogin = new Intent(context, AccountActivity.class);
//                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intentLogin);
//                } else {
//                    // Kiểm tra xem đã có tài khoản nào đã active chưa
//                }
                // Đồng bộ tài khoản
            }

        }
    }

    private class GetListChild extends AsyncTask<Void, String, String> {
        private Activity mActivity;
        private Dialog dialog;

        public GetListChild(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mChildList.size() <= 0) {
                // Khi không có đứa trẻ nào thì mới hiển thị
                //dialog = DialogCustom.showLoadingDialog(mActivity, getString(R.string.performing_data_sync));
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String list_child = new ChildResource(mActivity).get(null);
                String result = processRespondListChild(list_child);
                if ("error".equals(result) || "error_auth".equals(result))
                    return result;
                Cursor cursor = mActivity.getContentResolver().query(
                        ChildModel.Contents.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                if (cursor.moveToNext()) {
                    do {
                        String child_id = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.ID_SERVER));
                        String respondLast = AccountResource.getLatestDeviceChild(mActivity, child_id);
                        result = processLatestDeviceChild(respondLast, child_id);
                        Log.d("mc_log", "latest_device_child: " + respondLast);
                        if ("error".equals(result) || "error_auth".equals(result))
                            return result;

                    } while (cursor.moveToNext());
                }
                // Gui token len
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                new TokenFcmResource(mActivity).sendTokenFcm(refreshedToken);
                return "success";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if ("error".equals(data)) {
                DialogCustom.showDialog(mActivity, R.drawable.error, getString(R.string.re_try), getString(R.string.error_try_again));
            } else if ("error_auth".equals(data)) {
                new CustomToast().showToast(mContext, R.drawable.error, getString(R.string.session_expired_login_again));
                AccountResource.setLogout(mContext);
                Intent intentLogin = new Intent(mContext, AccountActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentLogin);
                new CustomToast().showToast(mContext, R.drawable.error, getString(R.string.session_expired_login_again));
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new ImageResource(mContext).download();
//                    new VideoResource(mContext).download();
//                    new AudioResource(mContext).download();
//                }
//            }).start();

        }

        private String processErrorPacket(JSONObject jsonData) throws JSONException {
            String error_id = jsonData.getString(UrlPattern.MSG_KEY);
            if (UrlPattern.ERROR_AUTH.equals(error_id)) {
                return "error_auth";
            } else {
                return "error";
            }
        }

        private String processLatestDeviceChild(String contentPacket, String childId) throws JSONException {
            if (contentPacket == null || contentPacket.length() == 0) {
                return "error";
            } else {
                JSONObject jsonData = new JSONObject(contentPacket);
                if (jsonData.getInt(UrlPattern.STATUS_KEY) == 1) {
                    String deviceId = null;
                    Log.d("mc_log", "jsonData.getInt(UrlPattern.IS_HAVE) " + jsonData.getInt(UrlPattern.IS_HAVE));
                    if (jsonData.getInt(UrlPattern.IS_HAVE) > 0) {
                        deviceId = jsonData.getString(UrlPattern.DEVICE_ID);
                        Log.d("mc_log", "devide_id " + deviceId);
                        ChildModel.QueryHelper.updateChildDeviceId(mContext, childId, deviceId);
                    } else {
                        ChildModel.QueryHelper.updateChildDeviceId(mContext, childId, ChildModel.Contents.NO_LOGIN_BEFORE);
                    }
                    return deviceId;
                } else {
                    return processErrorPacket(jsonData);
                }
            }
        }

        private String processRespondListChild(String contentPacket) throws JSONException {
            if (contentPacket == null || contentPacket.length() == 0) {
                return "error";
            } else {
                JSONObject jsonData = new JSONObject(contentPacket);
                if (jsonData.getInt(UrlPattern.STATUS_KEY) == 1) {
                    // Lay danh sach
                    JSONArray listChild = jsonData.getJSONArray(UrlPattern.DATA_KEY);
                    // Cập nhật trong bảng child
                    ContentResolver content = mActivity.getContentResolver();
                    Cursor cursor = null;
                    String id_server_child = null;
                    for (int i = 0; i < listChild.length(); i++) {
                        JSONObject childObject = listChild.getJSONObject(i);
                        cursor = content.query(ChildModel.Contents.CONTENT_URI,
                                null,
                                ChildModel.Contents.ID_SERVER + "=?",
                                new String[]{childObject.getString(UrlPattern.ID_SERVER)},
                                null
                        );
                        // Nếu có rồi thì không cần
                        if (cursor.getCount() <= 0) {
                            id_server_child = childObject.getString(UrlPattern.ID_SERVER);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(ChildModel.Contents.ID_SERVER, id_server_child);
                            contentValues.put(ChildModel.Contents.FULL_NAME, childObject.getString(ChildModel.Contents.FULL_NAME));
                            contentValues.put(ChildModel.Contents.BIRTH, childObject.getInt(ChildModel.Contents.BIRTH));
                            contentValues.put(ChildModel.Contents.ACTIVE, 0); // trạng thái active = 0
                            content.insert(ChildModel.Contents.CONTENT_URI, contentValues);
                        }
                    }
                    if (cursor != null)
                        cursor.close();
                    // Kiểm tra xem có cái nào active chưa, nếu chưa set bừa 1 cái active
                    int ACTIVE_VALUE = 1;
                    Cursor cursorCount = content.query(ChildModel.Contents.CONTENT_URI,
                            null,
                            ChildModel.Contents.ACTIVE + " = ?",
                            new String[]{String.valueOf(ACTIVE_VALUE)},
                            null
                    );

                    if (cursorCount.getCount() <= 0) {
                        // Chon bua 1 thang lam active
                        if (id_server_child != null) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(ChildModel.Contents.ID_SERVER, id_server_child);
                            contentValues.put(ChildModel.Contents.ACTIVE, ACTIVE_VALUE);

                            content.update(ChildModel.Contents.CONTENT_URI,
                                    contentValues,
                                    ChildModel.Contents.ID_SERVER + "=?",
                                    new String[]{id_server_child}
                            );
                        }
                    }
                    if (cursorCount != null)
                        cursorCount.close();

                } else {
                    return processErrorPacket(jsonData);
                }
            }
            return "success";
        }

    }

    private void setExtraChildHide() {
        ImageView avatarExtra = (ImageView) findViewById(R.id.avatar_extra);
        avatarExtra.setVisibility(View.INVISIBLE);
    }

    private void refreshListChild() {
        if (mChildList == null) {
            mChildList = new ArrayList<>();
        }
        Cursor cursor = mContext.getContentResolver().query(ChildModel.Contents.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            mChildList.clear();
            String fullName;
            int isActive;
            int birth;
            String idServer;
            do {
                fullName = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.FULL_NAME));
                isActive = cursor.getInt(cursor.getColumnIndex(ChildModel.Contents.ACTIVE));
                birth = cursor.getInt(cursor.getColumnIndex(ChildModel.Contents.BIRTH));
                idServer = cursor.getString(cursor.getColumnIndex(ChildModel.Contents.ID_SERVER));
                mChildList.add(new ChildEntry(fullName, birth, isActive, idServer));
            } while (cursor.moveToNext());
        }

    }

}
