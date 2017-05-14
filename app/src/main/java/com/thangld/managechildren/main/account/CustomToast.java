package com.thangld.managechildren.main.account;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thangld.managechildren.R;

/**
 * Create by ThangLD
 */

public class CustomToast {

    public static long last_show;
    public static String last_error;

    public void showToast(Context context, int idIcon, String error) {
        // Để tránh người dùng ấn submit liên tục, hiển thị toast quá nhiều.
        if (!error.equals(last_error) || System.currentTimeMillis() - last_show > 2000) {
            //Inflater cho view
            // Layout Inflater for inflating custom view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // inflate the layout over view
            View layout = inflater.inflate(R.layout.custom_toast, null);

            // Get TextView id and set error
            TextView text = (TextView) layout.findViewById(R.id.toast_error);
            text.setText(error);

            if (idIcon != R.drawable.error) {
                text.setCompoundDrawablesWithIntrinsicBounds(idIcon, 0, 0, 0);
                text.setTextColor(context.getResources().getColor(R.color.background_color));
            }

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);

            // set view cho toast
            toast.setView(layout);
            toast.show();
            last_show = System.currentTimeMillis();
        }
        last_error = error;

    }

}
