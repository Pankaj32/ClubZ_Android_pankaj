package com.clubz.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class PhoneNumberTextWatcher implements TextWatcher {

private EditText edTxt;
private boolean isDelete;
private int initialvalue=3;
private int nextvalue=6;
public static String replacer =" ";

public PhoneNumberTextWatcher(EditText edTxtPhone) {
    this.edTxt = edTxtPhone;
    edTxt.setOnKeyListener(new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                isDelete = true;
            }
            return false;
        }
    });
}

public void onTextChanged(CharSequence s, int start, int before, int count) {
}

public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
}

public void afterTextChanged(Editable s) {

    if (isDelete) {
        isDelete = false;
        return;
    }
    String val = s.toString();
    String a = "";
    String b = "";
    String c = "";
    if (val != null && val.length() > 0) {
        val = val.replace(replacer, "");
        if (val.length() >= initialvalue) {
            a = val.substring(0, initialvalue);
        } else if (val.length() < initialvalue) {
            a = val.substring(0, val.length());
        }
        if (val.length() >= nextvalue) {
            b = val.substring(initialvalue, nextvalue);
            c = val.substring(nextvalue, val.length());
        } else if (val.length() > initialvalue && val.length() < nextvalue) {
            b = val.substring(initialvalue, val.length());
        }
        StringBuilder stringBuffer = new StringBuilder();
        if (a.length() > 0) {
            stringBuffer.append(a);
            if (a.length() == initialvalue) {
                stringBuffer.append(replacer);
            }
        }
        if (b.length() > 0) {
            stringBuffer.append(b);
            if (b.length() == initialvalue) {
                stringBuffer.append(replacer);
            }
        }
        if (c.length() > 0) {
            stringBuffer.append(c);
        }
        edTxt.removeTextChangedListener(this);
        edTxt.setText(stringBuffer.toString());
        edTxt.setSelection(edTxt.getText().toString().length());
        edTxt.addTextChangedListener(this);
    } else {
        edTxt.removeTextChangedListener(this);
        edTxt.setText("");
        edTxt.addTextChangedListener(this);
    }

 }
}
