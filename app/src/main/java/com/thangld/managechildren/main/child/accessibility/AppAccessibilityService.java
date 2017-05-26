package com.thangld.managechildren.main.child.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.thangld.managechildren.main.BanActivity;
import com.thangld.managechildren.main.PasswordActivity;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.RuleParentModel;

import java.util.List;

public class AppAccessibilityService extends AccessibilityService {
    public AppAccessibilityService() {
    }

    // Đánh dấu thời gian bắt đầu sử dụng của những app bị giới hạn về thời gian
    public static long startTimeLimitAppUse = 0;
    // Đánh dấu là app vừa truy cập có phải nằm trong list app hay khôg
    public static boolean latestAppIsLimitApp = false;

    public static boolean isLock = true;
    public static String packageNameLock = "";
    public static boolean isShowLock = false;

    public static boolean isLockRecentTask = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
//        Log.d("mc_log", "onAccessibilityEvent: " + event.getPackageName() + " " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(event);
//                Log.d("mc_log", "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED: " + event.getPackageName());
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
//                Log.d("mc_log", "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED: " + event.getPackageName());
                break;
        }
    }

    private void onViewClicked(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(this.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }

        String myAppName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        if ("com.android.settings".equals(packageName)) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                int count = source.getChildCount();
                boolean isRequest = false;
                for (int i = 0; i < count; i++) {
                    if (myAppName.equals(source.getChild(i).getText())) {
                        isRequest = true;
                    }
                }
                if (isRequest) {
                    isLockRecentTask = true;
                    showRequestPassword(isLockRecentTask);
                } else {
                    isLockRecentTask = false;
                }
            } else {
                isLockRecentTask = false;
            }
        }
    }

    private void onWindowStateChanged(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        String childId = ChildModel.QueryHelper.getChildIdActive(this);
        String typeApp = AppModel.AppHelper.getTypeApp(this, childId, packageName);
//        Log.d("mc_log", "typeApp " + typeApp);
//        Log.d("mc_log", "packageNameLock " + packageNameLock);
        if (!packageName.equals(packageNameLock)) {

            isLock = true;
            packageNameLock = "";
        }
        if (isLock) {
            if (AppModel.Contents.TYPE_BAN_APP.equals(typeApp)) {
                latestAppIsLimitApp = false;
                // Nếu đó là app bị cấm sử dụng. hiển thị
                showAppBan(packageName);
                return;
            } else if ("com.android.systemui".equals(packageName)) {
                // Khi nguoi dung an recent app
                if (isLockRecentTask) {
                    showRequestPassword(isLockRecentTask);
                }
            }
            if (AppModel.Contents.TYPE_LIMIT_TIME_APP.equals(typeApp)) {

                latestAppIsLimitApp = true;
                // Lấy thời gian sử dụng hiện tại
                if (startTimeLimitAppUse == 0) {
                    // Nếu đây là lần đầu tiên sử dụng thì không làm gì cả
                    startTimeLimitAppUse = System.currentTimeMillis();
                    return;
                }
                startTimeLimitAppUse = System.currentTimeMillis();
//                Log.d("mc_log", "startTimeLimitAppUse " + startTimeLimitAppUse);
                long timeUseInDay = new PreferencesController(this).getTimeUseInDay();
                long limitTimeUse = RuleParentModel.RulesParentHelper.getTimeLimitTimeApp(this, childId) * 60 * 1000;

                if (timeUseInDay > limitTimeUse) {
                    // Hiển thị thông báo đã quá thời gian sử dụng
                    showLimitTimeUse(packageName);
                }
            } else {
                if (latestAppIsLimitApp) {
                    latestAppIsLimitApp = false;
                    // limit app đã được sử dụng trước đó
                    // thêm thời gian
                    long extraTime = System.currentTimeMillis() - startTimeLimitAppUse;
                    long timeUseInDay = new PreferencesController(this).getTimeUseInDay();
                    timeUseInDay += extraTime;
//                    Log.d("mc_log", "timeUseInDay " + timeUseInDay);
                    new PreferencesController(this).putTimeUseInDay(timeUseInDay);
                }
            }

//            Log.d("mc_log", "TYPE_WINDOW_STATE_CHANGED: " + event.getPackageName());
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );
            }
        }
    }


    public void showLimitTimeUse(String packageName) {
        Intent dialogIntent = new Intent(this, BanActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(BanActivity.TYPE_EXTRA, BanActivity.TYPE_LIMIT_TIME_APP);

        dialogIntent.putExtra(BanActivity.PACKAGE_NAME_EXTRA, packageName);
        startActivity(dialogIntent);
    }

    public void showRequestPassword(boolean isLockRecentApp) {
        isShowLock = true;
        Intent dialogIntent = new Intent(this, PasswordActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(PasswordActivity.TYPE_EXTRA, PasswordActivity.TYPE_REQUIRE_PASSWORD);
        dialogIntent.putExtra(PasswordActivity.IS_LOCK_RECENT_APP_EXTRA, isLockRecentApp);
        startActivity(dialogIntent);
    }



    public void showAppBan(String packageName) {
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(this.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : packageName);

        Intent dialogIntent = new Intent(this, BanActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(BanActivity.TYPE_EXTRA, BanActivity.TYPE_BAN_APP);
        dialogIntent.putExtra(BanActivity.APP_NAME_EXTRA, applicationName);
        dialogIntent.putExtra(BanActivity.PACKAGE_NAME_EXTRA, packageName);
        startActivity(dialogIntent);
    }

    @Override
    public void onInterrupt() {

    }


    public static void unlock(String packageName) {
        packageNameLock = packageName;
        isLock = false;
    }

    public static void unlockRecentApp() {
        isLockRecentTask = false;
    }

    //


}
