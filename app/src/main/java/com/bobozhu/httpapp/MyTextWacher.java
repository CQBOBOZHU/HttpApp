package com.bobozhu.httpapp;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Administrator on 2017/8/12.
 */

public abstract class MyTextWacher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
