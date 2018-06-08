package com.clubz.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.clubz.R;

public class InfoViewDialog extends DialogFragment{

    /*Dialog dialog;

    private Context context;
    private Callback callback;
*/
    public interface Callback{
        void onClick();
    }

   /* public InfoViewDialog(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }
*/
    public static InfoViewDialog newInstance(String title, String description) {
        InfoViewDialog frag = new InfoViewDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title","Terms and conditions");
        String desc = getArguments().getString("description","");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_infoview, null);

        TextView mTitle =  view.findViewById(R.id.mTitle);
        TextView mDesc =  view.findViewById(R.id.mDesc);

        mTitle.setText(title);
        mDesc.setText(desc);
        setCancelable(false);

        view.findViewById(R.id.mClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
