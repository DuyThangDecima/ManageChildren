package com.thangld.managechildren.main.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.thangld.managechildren.R;
import com.thangld.managechildren.main.PanelActivity;
import com.thangld.managechildren.main.PasswordActivity;
import com.thangld.managechildren.main.child.ListChildActivity;
import com.thangld.managechildren.storage.controller.PreferencesController;

public class OptionAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static CheckBox belongParent, belongChild;
    private static RelativeLayout layoutParent, layoutChild;
    private static Button btnContinue;

    private static final int CODE_REQUEST_CREATE_PASSWORD = 1;
    private static final int CODE_REQUEST_CONFIRM_PASSWORD = 2;
    private String mPasswordCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_account);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.who) + "</font>"));


        initViews();
        setListeners();
    }

    // Initiate Views
    private void initViews() {
        belongChild = (CheckBox) findViewById(R.id.belong_child);
        belongParent = (CheckBox) findViewById(R.id.belong_parent);
        layoutParent = (RelativeLayout) findViewById(R.id.layout_parent);
        layoutChild = (RelativeLayout) findViewById(R.id.layout_child);
        btnContinue = (Button) findViewById(R.id.continueBtn);
        // Setting text selector over textviews

    }

    // Set Listeners
    private void setListeners() {
        layoutParent.setOnClickListener(this);
        layoutChild.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (belongChild.isChecked() || belongParent.isChecked()) {
            btnContinue.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_child:
                belongChild.setChecked(true);
                belongParent.setChecked(false);
                btnContinue.setEnabled(true);
                break;
            case R.id.layout_parent:
                belongChild.setChecked(false);
                belongParent.setChecked(true);
                btnContinue.setEnabled(true);
                break;
            case R.id.continueBtn:
                if (belongChild.isChecked()) {
                    // Kiểm tra đã tạo mật khẩu chưa
                    String passwordChild = new PreferencesController(this).getPasswordChild();
                    Log.d("mc_log", "passwordChild " + passwordChild);
                    if (passwordChild == null || passwordChild.length() == 0) {
                        // passwordChild == null nghĩa là chưa tạo password bào giờ
                        Intent intent = new Intent(this, PasswordActivity.class);
                        intent.putExtra(PasswordActivity.TYPE_EXTRA, PasswordActivity.TYPE_CREATE_PASSWORD);
                        startActivity(intent);
                    }
                    else{
                        // Da tao mat khau roi thi bo qua
                        Intent intent = new Intent(this, ListChildActivity.class);
                        startActivity(intent);
                    }
                } else if (belongParent.isChecked()) {
                    Intent intent = new Intent(this, PanelActivity.class);
                    startActivity(intent);
                }
        }
    }

}
