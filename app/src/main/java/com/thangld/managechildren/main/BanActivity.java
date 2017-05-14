package com.thangld.managechildren.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.main.child.accessibility.AppAccessibilityService;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.RuleParentModel;

public class BanActivity extends AppCompatActivity {


    public static final String TYPE_EXTRA = "type_extra";
    public static final String APP_NAME_EXTRA = "app_name_extra";
    public static final String PACKAGE_NAME_EXTRA = "package_name_extra";


    public static final String TYPE_BAN_APP = "ban_app";
    public static final String TYPE_LIMIT_TIME_APP = "limit_time_app";

    private Context mContext;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.app_name) + "</font>"));

        TextView textView = (TextView) findViewById(R.id.msg);
        Button passwordBtn = (Button) findViewById(R.id.password);
        mContext = this;
        passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PasswordActivity.class);
                intent.putExtra(PasswordActivity.TYPE_EXTRA, PasswordActivity.TYPE_REQUIRE_PASSWORD);
                startActivityForResult(intent, 1);
            }
        });

        String type = getIntent().getExtras().getString(TYPE_EXTRA);
        packageName = getIntent().getExtras().getString(PACKAGE_NAME_EXTRA);
        if (TYPE_BAN_APP.equals(type)) {
            String appName = getIntent().getExtras().getString(APP_NAME_EXTRA);

            textView.setText(getString(R.string.msg_ban_app, appName));
        } else if (TYPE_LIMIT_TIME_APP.equals(type)) {
            String content;
            long limitTime = RuleParentModel.RulesParentHelper.getTimeLimitTimeApp(this, ChildModel.QueryHelper.getChildIdActive(this));
            if (limitTime < 60) {
                content = limitTime + getString(R.string.minute);
            } else {
                long hours = limitTime / 60;
                long minute = limitTime % 60;
                content = hours + getString(R.string.hours) + minute;
            }
            textView.setText(getString(R.string.msg_limit_use, content));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        if (resultCode == Activity.RESULT_OK) {
            AppAccessibilityService.unlock(packageName);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        this.finish();
    }
}
