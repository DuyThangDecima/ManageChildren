package com.thangld.managechildren.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thangld.managechildren.main.account.AccountActivity;
import com.thangld.managechildren.main.account.OptionAccountActivity;
import com.thangld.managechildren.storage.controller.DatabaseHelper;
import com.thangld.managechildren.storage.controller.PreferencesController;

public class MainActivity extends AppCompatActivity {


    DatabaseHelper mDbHelper;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesController preferencesController = new PreferencesController(this);
        boolean is_login = preferencesController.getStatusLogin();
        if (is_login) {
            String privilege = preferencesController.getPrivilege();
            if (privilege.equals(PreferencesController.PRIVILEGE_PARENT)) {
                Intent intent = new Intent(this, PanelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Đến giao diện của bố mẹ
            } else if (privilege.equals(PreferencesController.PRIVILEGE_CHILD)) {
                // Hiển thị chào child, yêu cầu nhập mật khẩu

                // Hiển thị options tùy chọn kiểu tài khoản
                Intent intent = new Intent(this, PasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(PasswordActivity.TYPE_EXTRA, PasswordActivity.TYPE_PARENT_SETTINGS);
                startActivity(intent);

            } else {
                // Hiển thị options tùy chọn kiểu tài khoản
                Intent intent = new Intent(this, OptionAccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        } else {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

//        Intent intent = new Intent(this, ListChildActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }
}
