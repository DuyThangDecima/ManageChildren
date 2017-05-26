package com.thangld.managechildren.main.account;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.cipher.CipherManager;
import com.thangld.managechildren.utils.DialogCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by:  ThangLD
 */
public class SignUpFragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber, location,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        getActivity().setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.sign_up_title) + "</font>"));
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Call checkValidation method
                if (checkValidation()) {
                    SingUpTask singUpTask = new SingUpTask(getActivity());

                    String getFullName = fullName.getText().toString();
                    String getEmail = emailId.getText().toString();
                    String getPassword = password.getText().toString();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(UrlPattern.EMAIL_KEY, getEmail);
                        jsonObject.put(UrlPattern.PASSWORD_KEY, CipherManager.md5(getPassword));
                        jsonObject.put(UrlPattern.FULL_NAME_KEY, getFullName);
                        singUpTask.execute(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.already_user:
                // Replace login fragment
                new AccountActivity().replaceLoginFragment();
                break;
        }
    }

    // Check hợp lệ của các trường
    private boolean checkValidation() {

        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.all_field_required));

            // Check email hợp lệ
        else if (!m.find())
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.email_invalid));

            // Check password 8 ký tự
        else if (getPassword.length() < 8)
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.minimum_required_password));

        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.confirm_password_not_match));

            // check click terms
        else if (!terms_conditions.isChecked())
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.select_terms_conditions));

            // Thoả mãn tất cả các điều kiện thì trả về true
        else {
            return true;
        }
        return false;
    }

    /**
     * Thực hiện yêu cầu đăng ký ở asynctask.
     */
    public class SingUpTask extends AsyncTask<JSONObject, Void, String> {
        private Context context;

        public SingUpTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Fragment connectFragment = new ConnectFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.frameContainer, connectFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        @Override
        protected String doInBackground(JSONObject... hashMaps) {

            JSONObject jsonObject = hashMaps[0];
            String respond = "";
            try {
                respond = AccountResource.signUp(jsonObject);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.d("mc_log", "respond" + respond);
            return respond;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.length() == 0) {
                notifyError(context.getString(R.string.error_try_again));
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    int status = json.getInt(UrlPattern.STATUS_KEY);
                    if (status == UrlPattern.STATUS_SUCCESS) {
                        // Đăng ký tài khoản thành công, thông báo và đưa về giao diện đăng nhập
                        new CustomToast().showToast(context, R.drawable.success, getString(R.string.register_success));
                        new AccountActivity().replaceLoginFragment();

                    } else {
                        String error_id = json.getString(UrlPattern.MSG_KEY);
                        if (error_id.equals(UrlPattern.ERROR_DB_ACTION)) {
                            getFragmentManager().popBackStack();
                            DialogCustom.showDialog(getActivity(), R.drawable.error, getString(R.string.cancel), getString(R.string.error_try_again));
                        } else if (error_id.equals(UrlPattern.ERROR_EXIST)) {
                            getFragmentManager().popBackStack();
                            DialogCustom.showDialog(getActivity(), R.drawable.error, getString(R.string.cancel), getString(R.string.email_already_registered));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyError(context.getString(R.string.error_try_again));
                }
            }
        }

        /**
         * Quay về màn hình đăng nhập và hiển thị lỗi
         *
         * @param content
         */
        private void notifyError(String content) {
            new CustomToast().showToast(context, R.drawable.error, content);
            getFragmentManager().popBackStack();
        }
    }

}
