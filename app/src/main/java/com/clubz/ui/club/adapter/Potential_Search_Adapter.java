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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.clubz.ui.main.HomeActivity;
import com.clubz.R;
import com.clubz.data.model.Club_Potential_search;

import java.util.ArrayList;


abstract public class Potential_Search_Adapter extends RecyclerView.Adapter<Potential_Search_Adapter.Holder> implements Filterable {
    private Context context;
    private ArrayList<Club_Potential_search> list ;
    private ArrayList<Club_Potential_search> filteredList ;

    private CustomFilter mFilter;
    private String currentText = "";
    private HomeActivity activity;

    public Potential_Search_Adapter(Context context, ArrayList<Club_Potential_search> list , HomeActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.filteredList = new ArrayList<>();
        mFilter = new CustomFilter(Potential_Search_Adapter.this);
        //setAllAgain();
    }

    public void setList(ArrayList<Club_Potential_search> list) {
        this.list = list;
        filteredList.clear();
        mFilter = new CustomFilter(Potential_Search_Adapter.this);
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.base_search_text, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, @SuppressLint("RecyclerView") final int position) {

       try {

        holder.text_search.setText(getFilterdText(filteredList.get(position).getClub_name(), currentText));
        holder.text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(filteredList.get(position));
            }
        });}
        catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView text_search;
        Holder(View itemView) {
            super(itemView);
            text_search = itemView.findViewById(R.id.text_search);
        }
    }

    abstract public void onItemClick(final Club_Potential_search serch_obj);


    /********************** Filter ****/
    public class CustomFilter extends Filter {
        private Potential_Search_Adapter mAdapter;

        private CustomFilter(Potential_Search_Adapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }



        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            currentText = "";
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                return results;
               // filteredList.addAll(list); // this line not required here
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (final Club_Potential_search mWords : list) {
                    if(filterPattern.length()<2){if (mWords.getClub_name().toLowerCase().startsWith(filterPattern)) {
                        if(canAddd(mWords))filteredList.add(mWords);
                    }
                    }
                    else {
                        if (mWords.getClub_name().toLowerCase().contains(filterPattern)) {
                            if(canAddd(mWords))filteredList.add(mWords);
                        }
                    }
                }
                /*
                 * * This is just to show bold text on list***/
                if(filteredList.size()>0){
                    currentText = filterPattern;
                }
            }
        /**///System.out.println("Count Number " + filteredList.size());
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        /**///System.out.println("Count Number 2 " + ((ArrayList<FriendRequestBean>) filterResults.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setAllAgain(){
        filteredList.addAll(list);
    }


    private Boolean canAddd(Club_Potential_search obj) {
        return activity.isPrivate() == 0 || activity.isPrivate() == Integer.parseInt(obj.getClub_type());

    }
    /*public CharSequence linkifyHashtags(String text) {
        SpannableStringBuilder linkifiedText = new SpannableStringBuilder(text);
        Pattern pattern = Pattern.compile("@\\w");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String hashtag = text.substring(start, end);
            ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
            linkifiedText.setSpan(span, 0, hashtag.length(), 0);
        }
        return linkifiedText;
    }*/


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
