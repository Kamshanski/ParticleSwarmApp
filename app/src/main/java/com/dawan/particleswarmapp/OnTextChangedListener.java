package com.dawan.particleswarmapp;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Custom listener to avoid a lot of empty code in View code
 */
abstract class OnTextChangedListener implements TextWatcher {
    public OnTextChangedListener() {
    }

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
