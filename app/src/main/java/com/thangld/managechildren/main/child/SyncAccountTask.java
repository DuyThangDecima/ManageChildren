package com.thangld.managechildren.main.child;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.cloud.resource.TokenFcmResource;
import com.thangld.managechildren.main.PasswordActivity;
import com.thangld.managechildren.main.account.AccountActivity;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.DeviceInfoUtils;
import com.thangld.managechildren.utils.DialogCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by thangld on 03/05/2017.
 */


public class SyncAccountTask extends AsyncTask<Void, String, String> {
    private Activity mActivity;
    private String mChildId;
    private Dialog dialog;

    public SyncAccountTask(Activity activity, String childId) {
        mActivity = activity;
        mChildId = childId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogCustom.showLoadingDialog(mActivity, mActivity.getString(R.string.performing_data_sync));
    }

    @Override
    protected String doInBackground(Void... voids) {
        JSONObject jsonObject = new JSONObject();
        String respond = null;
        try {
            jsonObject.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
            jsonObject.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(mActivity));
            jsonObject.put(UrlPattern.TOKEN_KEY, new PreferencesController(mActivity).getToken());

            jsonObject.put(UrlPattern.CHILD_ID_KEY, ChildModel.QueryHelper.getChildIdActive(mActivity));
            jsonObject.put(UrlPattern.PRIVILEGE_KEY, UrlPattern.PRIVILEGE_CHILD);
            respond = AccountResource.syncAccount(jsonObject);

            // Gui token len
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            new TokenFcmResource(mActivity).sendTokenFcm(refreshedToken);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return respond;

    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (data == null || data.length() == 0) {
            DialogCustom.showDialog(mActivity, 0, mActivity.getString(R.string.re_try), mActivity.getString(R.string.error_try_again));

        } else {
            try {
                JSONObject jsonData = new JSONObject(data);
                if (jsonData.getInt(UrlPattern.STATUS_KEY) == 1) {
                    // TODO tất cả được thực hiện ở đây, đến activity pannel
                    // Thong bao dong bo hoan tat


                    PanelParentInChildDeviceActivity.startMonitorForDevice(mActivity, mChildId);
                    Intent intent = new Intent(mActivity, PasswordActivity.class);
                    intent.putExtra(PasswordActivity.TYPE_EXTRA, PasswordActivity.TYPE_PARENT_SETTINGS);
                    mActivity.startActivity(intent);
                    mActivity.finish();


                } else {
                    String error_id = jsonData.getString(UrlPattern.MSG_KEY);
                    if (UrlPattern.ERROR_AUTH.equals(error_id)) {
                        new CustomToast().showToast(mActivity, R.drawable.error, mActivity.getString(R.string.session_expired_login_again));
                        AccountResource.setLogout(mActivity);
                        Intent intentLogin = new Intent(mActivity, AccountActivity.class);
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intentLogin);
                        new CustomToast().showToast(mActivity, R.drawable.error, mActivity.getString(R.string.session_expired_login_again));

                    } else {
                        DialogCustom.showDialog(mActivity, R.drawable.error, mActivity.getString(R.string.re_try), mActivity.getString(R.string.error_try_again));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}