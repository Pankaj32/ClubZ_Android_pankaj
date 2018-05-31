package com.clubz.ui.club.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clubz.R;
import com.clubz.data.model.Club_Potential_search;

import java.util.ArrayList;


abstract public class SearchClubName_Adapter extends RecyclerView.Adapter<SearchClubName_Adapter.Holder> {

    private Context context;
    private ArrayList<Club_Potential_search> list ;
    private String currentText = "";
    //private HomeActivity activity;

    public void setCurrentText(String currentText) {
        this.currentText = currentText;
    }

    public SearchClubName_Adapter(Context context, ArrayList<Club_Potential_search> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<Club_Potential_search> list) {
        this.list = list;
    }

    @SuppressLint("InflateParams")
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.base_search_text, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, @SuppressLint("RecyclerView") final int position) {
        holder.text_search.setText(getFilterdText(list.get(position).getClub_name(), currentText));
    }

    @Override
    public int getItemCount() {
         return list.size();
    }



    class Holder extends RecyclerView.ViewHolder{
        TextView text_search;
        Holder(View itemView) {
            super(itemView);
            text_search = itemView.findViewById(R.id.text_search);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(list.get(getAdapterPosition()));
                }
            });
        }
    }

    abstract public void onItemClick(final Club_Potential_search serch_obj);



    private CharSequence getFilterdText(String fullname , String filterText){
        final SpannableString str = new SpannableString(fullname);
        if(filterText.length()<2)str.setSpan(new ForegroundColorSpan(Color.GRAY), 0, filterText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else {  int i = 0;
                for(; i<fullname.length(); i++){
                    if(fullname.toLowerCase().charAt(i)== filterText.toLowerCase().charAt(0) && filterText.toLowerCase().equals(fullname.toLowerCase().substring(i,filterText.length()+i)))
                        break; }
                str.setSpan(new ForegroundColorSpan(Color.GRAY), i, filterText.length()+i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return str;
    }
}
