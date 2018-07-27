package com.clubz.ui.cv;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubz.R;
import com.loopeer.shadow.ShadowView;


/**
 * Created by mindiii on 9/5/17.
 */

public abstract class ChipEditText extends RelativeLayout{
    public boolean addAsotherView;
    Context context;
    EditText label;
    ImageView delete_button;
   // String Id;
    private boolean isDeletable = true;

    // ChipDeleteListner chipDeleteListner;
    public ChipEditText(Context context/*, String Id*/) {
        super(context);
        this.context = context;
        //this.Id = Id;
        initiateview();
    }

   /* public ChipEditText(Context context, String Id, boolean isDeletable) {
        super(context);
        this.context = context;
        this.Id = Id;
        this.isDeletable = isDeletable;
        initiateview();
    }*/

    void initiateview() {
        /*int layoutId = getLayout();
        if (layoutId == 0) */ int layoutId = R.layout.chip_edit_text;
        // View view = inflate(context, R.layout.z_cus_chip_view, this);
        View view = inflate(context, layoutId, this);

        label = view.findViewById(R.id.label);
        label.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setDone(textView.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }


    public void setText(String text) {
        label.setText(text);
        /*System.out.println();*/
    }

    public String getText(){
        return label.getText().toString();
    }

    public TextView getTextView() {
        return label;
    }

    public abstract void setDone(String text);

    /*public String getIdChip() {
        return Id;
    }*/


    /*public abstract int getLayout();*/
}
