package com.bobozhu.httpapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/12.
 */

public class Util {

    public static boolean IsIP(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        String[] split = url.split("\\.");
        if (split.length != 4) {
            return false;
        }
        for (String sp : split) {
            if (!isNumeric(sp)) {
                return false;
            }
        }
        return true;
    }

    /**
     *  判断str是否是数字 并且在0-255之间
     * @param str
     * @return
     */
    public  static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        if (Integer.parseInt(str) <= 255 && Integer.parseInt(str) >= 0) {
            return true;
        }
        return false;
    }

    public static void copy(Context context,String msg){

        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(msg);
        Toast.makeText(context, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
    }

}
