package com.bobozhu.httpapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/2.
 */

public class BAlterDialog {
    static AlertDialog alertDialog;

    public BAlterDialog(Context context, String msg, DialogClickListener clickListener) {
        this(context, msg, "取消", "确认", clickListener);
    }

    public BAlterDialog(Context context, String msg, String left, String right, DialogClickListener clickListener) {
        this(context, null, msg, left, right, clickListener);
    }

    public BAlterDialog(Context context, String title, String msg, String left, String right, final DialogClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);
        builder.setMessage(msg).setNegativeButton(left, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickListener.doLeft();
            }
        }).setPositiveButton(right, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickListener.doRight();
            }
        });
        alertDialog = builder.create();
    }

    public  void show() {
        alertDialog.show();
    }

    public  void dismiss() {
        alertDialog.dismiss();
    }

    public void setLeftColor(int color) {
        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (button == null)
            throw new NullPointerException("the button is not available until the dialog is showed");

        button.setTextColor(color);
    }


    public void setRightColor(int color) {
        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (button == null)
            throw new NullPointerException("the button is not available until the dialog is showed");

        button.setTextColor(color);
    }

    public void setMessageColor(int color) {
        TextView msgTv = (TextView) alertDialog.getWindow().findViewById(android.R.id.message);
        if (msgTv == null)
            throw new NullPointerException("the textView is not available until the dialog is showed");

        msgTv.setTextColor(color);
    }

    public void setTitleColor(int color) {
        TextView titleTv = (TextView) alertDialog.getWindow().findViewById(R.id.alertTitle);
        if (titleTv == null)
            throw new NullPointerException("the textView is not available until the dialog is showed");
        titleTv.setTextColor(color);
    }
}
