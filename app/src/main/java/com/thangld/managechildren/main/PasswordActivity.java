package com.thangld.managechildren.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.cipher.CipherManager;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.main.child.ListChildActivity;
import com.thangld.managechildren.main.child.PanelParentInChildDeviceActivity;
import com.thangld.managechildren.main.child.accessibility.AppAccessibilityService;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.utils.Accessibility;

public class PasswordActivity extends AppCompatActivity implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView mKeyboardView;
    private TextView mTvMsg;
    private EditText mInputPassword;
    private Context mContext;

    private String mPasswordTemp;

    // Da denn buoc xac nhan tai khoan chua
    private boolean isConfirmedPassword = false;

    private String mTypePassword;


    public static final String TYPE_EXTRA = "type_extra";
    public static final String PASSWORD_RESULT_EXTRA = "password_result_extra";
    public static final String IS_LOCK_RECENT_APP_EXTRA = "is_lock_recent_app_extra";

    public static final String TYPE_REQUIRE_PASSWORD = "require_password";
    public static final String TYPE_CREATE_PASSWORD = "create_password";
    public static final String TYPE_CONFIRM_PASSWORD = "confirm_password";
    public static final String TYPE_PARENT_SETTINGS = "type_parent_settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.app_name) + "</font>"));
        mTypePassword = getIntent().getExtras().getString(TYPE_EXTRA);
        mContext = this;
        initViews();
        setListeners();
        // Xac dinh kieu va hien thi
    }

    private void initViews() {

        mKeyboardView = (KeyboardView) findViewById(R.id.keyboard_view);
        mTvMsg = (TextView) findViewById(R.id.msg);
        mInputPassword = (EditText) findViewById(R.id.confirm_password);

        // Set cac su kien cho keybora
        mKeyboardView.setPreviewEnabled(false);
        Keyboard keyboard = new Keyboard(this, R.xml.keyboard_number);
        mKeyboardView.setKeyboard(keyboard);
        mKeyboardView.setOnKeyboardActionListener(this);

        // Set text cho msg
        if (TYPE_REQUIRE_PASSWORD.equals(mTypePassword)) {
            mTvMsg.setText(getString(R.string.require_password));
        } else if (TYPE_CONFIRM_PASSWORD.equals(mTypePassword)) {
            mTvMsg.setText(getString(R.string.confirm_password));
        } else if (TYPE_CREATE_PASSWORD.equals(mTypePassword)) {
            mTvMsg.setText(getString(R.string.create_password));
        } else if (TYPE_PARENT_SETTINGS.equals(mTypePassword)) {
            mTvMsg.setText(getString(R.string.go_settings));
        }

    }

    private void setListeners() {
        registerEditText(R.id.confirm_password);

        mInputPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    Toast.makeText(mContext, "this key del", Toast.LENGTH_LONG).show();
                    // this is for backspace
                    String current = mInputPassword.getText().toString();
                    mInputPassword.setText(current.substring(0, current.length() - 2));
                }
                return false;
            }
        });

    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int primaryCode, int[] ints) {
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                mInputPassword.setText("");
                break;
            case Keyboard.KEYCODE_DONE:
                String content = mInputPassword.getText().toString();
                if (content == null || content.length() == 0) {
                    new CustomToast().showToast(this, R.drawable.error, mContext.getString(R.string.enter_password));
                } else {
                    if (TYPE_REQUIRE_PASSWORD.equals(mTypePassword)) {
                        // Kiem tra mat khau dung khong, neu khong dung bat nhap lai
                        String passHash = CipherManager.md5(content);
                        if (passHash.equals(new PreferencesController(this).getPasswordChild())) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            AppAccessibilityService.unlockRecentApp();

//                            AppAccessibilityService.unlock();
                            finish();
                        } else {
                            mInputPassword.setText("");
                            new CustomToast().showToast(this, R.drawable.error, mContext.getString(R.string.passowrd));
                        }
                    } else if (TYPE_CREATE_PASSWORD.equals(mTypePassword)) {
                        if (isConfirmedPassword) {
                            // Kiem tra password co match hay khong
                            String passwordConfirm = mInputPassword.getText().toString();
                            if (mPasswordTemp.equals(passwordConfirm)) {

                                String passHash = CipherManager.md5(mPasswordTemp);
                                Log.d("mc_log", "passHash " + passHash);
                                new PreferencesController(mContext).putPasswordChild(passHash);
                                new CustomToast().showToast(mContext, R.drawable.success, getString(R.string.create_password_success));
                                // Neu thanh cong, di den list child activity luon
                                Intent intent = new Intent(this, ListChildActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                mInputPassword.setText("");
                                new CustomToast().showToast(this, R.drawable.error, getString(R.string.confirm_password_not_match));
                            }
                        } else {
                            if (content.length() <= 3) {
                                new CustomToast().showToast(mContext, R.drawable.error, getString(R.string.min_4_characters));
                                mInputPassword.setText("");
                            } else {
                                // chuyen trang thai sang lan nhap tiep theo la cap nhat password
                                isConfirmedPassword = true;
                                mTvMsg.setText(mContext.getString(R.string.confirm_password));
                                mPasswordTemp = mInputPassword.getText().toString();
                                mInputPassword.setText("");
                            }
                        }
                    } else if (TYPE_PARENT_SETTINGS.equals(mTypePassword)) {
                        String passHash = CipherManager.md5(content);
                        if (passHash.equals(new PreferencesController(this).getPasswordChild())) {
                            Intent intentAc = new Intent(mContext, PanelParentInChildDeviceActivity.class);
                            startActivity(intentAc);
                            finish();
                        } else {
                            mInputPassword.setText("");
                            new CustomToast().showToast(this, R.drawable.error, mContext.getString(R.string.confirm_password_not_match));
                        }
                    }
                }
        }
        long eventTime = System.currentTimeMillis();
        KeyEvent event = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
        dispatchKeyEvent(event);

    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }


    public void registerEditText(int resid) {
        // Find the EditText 'res_id'
        EditText edittext = (EditText) findViewById(resid);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                CharSequence mS = editable.subSequence(0, editable.length());
                if (!mS.toString().equals("") || mS.toString() != null) {
                    if (editable.length() > 0 && mS.toString().contains("=")) {
                        editable.replace(editable.length() - 1, editable.length(), "");
                    }
                }
            }
        });
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
    }

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null) {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Người dùng không nhập được mật khẩu thì về home
     */
    @Override
    public void onBackPressed() {


//        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        startActivity(intent);
//
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        this.finish();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}
