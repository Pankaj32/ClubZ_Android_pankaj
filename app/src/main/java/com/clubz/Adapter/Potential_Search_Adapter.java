package com.clubz.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.clubz.Home_Activity;
import com.clubz.R;
import com.clubz.model.Club_Potential_search;

import java.util.ArrayList;

/**
 * Created by mindiii on ३१/३/१८.
 */

abstract public class Potential_Search_Adapter extends RecyclerView.Adapter<Potential_Search_Adapter.Holder> implements Filterable {
    Context context;
    ArrayList<Club_Potential_search> list ;
    ArrayList<Club_Potential_search> filteredList ;

    private CustomFilter mFilter;
    String currentText = "";
    Home_Activity activity;

    public Potential_Search_Adapter(Context context, ArrayList<Club_Potential_search> list , Home_Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.filteredList = new ArrayList<Club_Potential_search>();
        mFilter = new CustomFilter(Potential_Search_Adapter.this);
        //setAllAgain();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.base_search_text, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

       try {
           final SpannableString str = new SpannableString(currentText + (filteredList.get(position).getClub_name().substring(currentText.length())));
        str.setSpan(new ForegroundColorSpan(Color.GRAY), 0, currentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.text_search.setText(str);
        holder.text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(filteredList.get(position));
            }
        });}catch (StringIndexOutOfBoundsException e){
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView text_search;
        public Holder(View itemView) {
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
                    if (mWords.getClub_name().toLowerCase().startsWith(filterPattern)) {
                        if(canAddd(mWords))filteredList.add(mWords);
                    }
                }
                /*** This is just to show bold text on list***/
                if(filteredList.size()>0){
                    currentText = charSequence.toString();
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
       /**/// Log.v("RecyclerAdapter", mFilter.toString());
        return mFilter;
    }

    public void setAllAgain(){
        filteredList.addAll(list);
    }


    public Boolean canAddd(Club_Potential_search obj){
        if(activity.isPrivate()==0) return true;
        if(activity.isPrivate()==Integer.parseInt(obj.getClub_type()))return true;
        return false;

    }
    /*String boldText = "id";
String normalText = "name";
SpannableString str = new SpannableString(boldText + normalText);
str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
textView.setText(str);
*/
}
