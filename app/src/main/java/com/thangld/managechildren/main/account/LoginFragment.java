package com.thangld.managechildren.main.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.cipher.CipherManager;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.utils.DeviceInfoUtils;
import com.thangld.managechildren.utils.DialogCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment implements OnClickListener {
    public static String tag = "LoginFragment";
    private static View view;

    private static EditText email, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static ImageButton show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    private static boolean isHiddenPassword = true;
    private static boolean beforeHaveText = false;

    public LoginFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("mc_log", "onCreateView");
        view = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.login_title) + "</font>"));
        initViews();
        setListeners();


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", email.getText().toString());
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        email = (EditText) view.findViewById(R.id.login_email);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (ImageButton) view.findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        show_hide_password.setOnClickListener(this);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    if (!beforeHaveText) {
                        beforeHaveText = true;
                        show_hide_password.setVisibility(View.VISIBLE);
                    }
                } else {
                    beforeHaveText = false;
                    show_hide_password.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:

                if (checkValidation()) {
                    String getEmail = email.getText().toString();
                    String getPassword = password.getText().toString();
                    LoginTask loginTask = new LoginTask(getActivity());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email", getEmail);
                        jsonObject.put("password", getPassword);
                        loginTask.execute(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPasswordFragment(),
                                Utils.ForgotPassword_Fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.createAccount:

                Fragment signUpFragment = new SignUpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.frameContainer, signUpFragment)
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out);
                transaction.addToBackStack(null);
                transaction.commit();

                break;
            case R.id.show_hide_password:
                isHiddenPassword = !isHiddenPassword;
                if (isHiddenPassword) {

                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    show_hide_password.setImageResource(R.drawable.hide_password);

                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    show_hide_password.setImageResource(R.drawable.show_password);
                }
        }

    }

    // Kiểm tra trước khi đăng nhập
    private boolean checkValidation() {
        String getEmailId = email.getText().toString();
        String getPassword = password.getText().toString();

        // patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);
        boolean result = false;
        // Check rỗng
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.enter_email_password));
            result = false;
        }
        // Check email
        else if (!m.find()) {
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.email_invalid));
            result = false;
        } else {
            result = true;
        }
        return result;

    }

    /**
     * Thực hiện yêu cầu đăng nhập ở asynctask.
     */
    public class LoginTask extends AsyncTask<JSONObject, Void, String> {
        private Context context;

        public LoginTask(Context context) {
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
            String password = null;
            try {
                password = jsonObject.getString("password");
                password = CipherManager.md5(password);

                jsonObject.put(UrlPattern.PASSWORD_KEY, password);
                jsonObject.put(UrlPattern.IMEI_KEY, DeviceInfoUtils.getImei(context));
                jsonObject.put(UrlPattern.DEVICE_NAME_KEY, DeviceInfoUtils.getDeviceName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String respond = "";
            try {
                respond = AccountResource.login(jsonObject);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return respond;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.length() == 0) {
                // check network and
                notifyError(context.getString(R.string.error_try_again));
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    int status = json.getInt(UrlPattern.STATUS_KEY);
                    if (status == UrlPattern.STATUS_SUCCESS) {
                        // Nhận token và lưu lại
                        String token = json.getString(UrlPattern.TOKEN_KEY);
                        Log.d("token", "LoginFragment - " + token);
                        PreferencesController preferencesController = new PreferencesController(context);
                        preferencesController.putToken(token);
                        preferencesController.putStatusApp(true);
                        preferencesController.putStatusLogin(true);

                        Intent intent = new Intent(context, OptionAccountActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                    } else {
                        String error_id = json.getString(UrlPattern.MSG_KEY);
                        if (error_id.equals(UrlPattern.ERROR_DB_ACTION)) {
                            notifyError(context.getString(R.string.error_try_again));
                        } else if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                            notifyError(context.getString(R.string.user_password_incorrect));
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
            DialogCustom.showDialog(getActivity(), 0, getString(R.string.re_try), content);
//            new CustomToast().showToast(context, view, content);
            getFragmentManager().popBackStack();
        }
    }
}
