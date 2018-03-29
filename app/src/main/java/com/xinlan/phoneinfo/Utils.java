package com.xinlan.phoneinfo;

import android.content.Context;
import android.text.ClipboardManager;

/**
 * Created by panyi on 2018/3/29.
 */

public class Utils {
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
