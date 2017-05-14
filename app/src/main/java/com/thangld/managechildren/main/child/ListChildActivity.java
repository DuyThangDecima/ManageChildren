package com.thangld.managechildren.main.child;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.cloud.resource.ChildResource;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.LoadingFragment;
import com.thangld.managechildren.main.NetworkErrorFragment;
import com.thangld.managechildren.main.account.AccountActivity;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.main.account.Utils;
import com.thangld.managechildren.storage.model.ChildModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListChildActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private ArrayList<ChildEntry> mListChild = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_child);
        initView();
        setListeners();

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new LoadingFragment(),
                            LoadingFragment.FRAGMENT_TAG).commit();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.add_child_title) + "</font>"));
        exeGetChildTask(this, fragmentManager);


    }

    public void exeGetChildTask(Context context, FragmentManager fragmentManager) {

        GetChildTask getChildTask = new GetChildTask(context, fragmentManager);
        getChildTask.execute();
    }


    public void setListeners() {

    }

    public void initView() {

    }

    @Override
    public void onClick(View view) {

    }

    public ArrayList<ChildEntry> getListChild() {
        return this.mListChild;
    }

    public void addListChild(ChildEntry child) {
        if (mListChild != null) {
            mListChild.add(child);
        }
    }

    /**
     * Thực hiện query danh sách child
     */
    public class GetChildTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private FragmentManager fragmentManager;

        public GetChildTask(Context context, FragmentManager fragmentManager) {
            super();
            this.fragmentManager = fragmentManager;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... Void) {
            return new ChildResource(context).get(null);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.length() == 0) {
                // Kiểm tra kết nối mạng và thử lại
                new CustomToast().showToast(context, R.drawable.error, getString(R.string.no_internet));
                finish();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.getInt(UrlPattern.STATUS_KEY) == 1) {
                        JSONArray childArray = json.getJSONArray(UrlPattern.DATA_KEY);

                        ContentResolver contentResolver = context.getContentResolver();
                        String full_name,id_server;
                        int birth;
                        Cursor cursor;
                        for (int i = 0; i < childArray.length(); i++) {
                            full_name = childArray.getJSONObject(i).getString(UrlPattern.FULL_NAME_KEY);
                            birth = childArray.getJSONObject(i).getInt(UrlPattern.BIRTH_KEY);
                            id_server = childArray.getJSONObject(i).getString(UrlPattern.ID_SERVER);
                            ChildEntry child = new ChildEntry(full_name, birth, 0,id_server);
                            // Kiểm tra trong db đã có chưa

                            cursor = contentResolver.query(ChildModel.Contents.CONTENT_URI,
                                    null,
                                    ChildModel.Contents.ID_SERVER + " = ?",
                                    new String[]{id_server},
                                    null
                                    );
                            if (cursor != null && cursor.getCount() <= 0){
                                // Neu table child chua co tai khoan child nay thi them vao
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ChildModel.Contents.ID_SERVER,id_server);
                                contentValues.put(ChildModel.Contents.BIRTH,birth);
                                contentValues.put(ChildModel.Contents.FULL_NAME,full_name);
                                contentResolver.insert(ChildModel.Contents.CONTENT_URI,contentValues);
                            }
                            mListChild.add(child);
                        }
                        this.fragmentManager
                                .beginTransaction()
                                .replace(R.id.frameContainer, new ChildFragment(),
                                        Utils.Login_Fragment).commit();
                    } else {
                        finish();
                        if (json.getString(UrlPattern.MSG_KEY).equals(UrlPattern.ERROR_AUTH)) {
                            // Tài khoản trên điện thoại này đã bị out, yêu cầu đăng nhập
                            AccountResource.setLogout(context);
                            new CustomToast().showToast(context, R.drawable.error, getString(R.string.session_expired_login_again));
                            Intent intent = new Intent(context, AccountActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            new CustomToast().showToast(context, R.drawable.error, getString(R.string.error_try_again));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Quay về màn hình đăng nhập và hiển thị lỗi
         *
         * @param content
         */
    }

    private void replaceNetworkErrorFragment() {
        Fragment networkErrorFragment = new NetworkErrorFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frameContainer, networkErrorFragment)
                .setCustomAnimations(R.anim.right_enter, R.anim.left_out);
        transaction.addToBackStack(null);
        transaction.commit();

    }


}
