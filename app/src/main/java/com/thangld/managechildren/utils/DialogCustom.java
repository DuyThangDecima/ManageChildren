package com.thangld.managechildren.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thangld.managechildren.R;

/**
 * Created by thangld on 07/04/2017.
 */

public class DialogCustom {
    /**
     * Hiển thị 1 dialog màu trắng,
     * có icon
     * có nội dụng
     * và 1 nút cancel
     *
     * @param activity
     * @param idIcon
     * @param buttonName
     * @param content
     */
    public static void showDialog(Activity activity, int idIcon, String buttonName, String content) {

        final Dialog dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_retry, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        // Get TextView id and set error
        TextView text = (TextView) view.findViewById(R.id.msg);
        text.setText(content);
        if (idIcon != R.drawable.error) {
            text.setCompoundDrawablesWithIntrinsicBounds(idIcon, 0, 0, 0);
        }
        Button btnCancel = (Button) view.findViewById(R.id.cancel);
        btnCancel.setText(buttonName);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        dialog.show();

    }


    public static Dialog showLoadingDialog(Activity activity, String content) {
        final Dialog dialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);

        TextView tvContent = (TextView) view.findViewById(R.id.msg);
        tvContent.setText(content);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

}
