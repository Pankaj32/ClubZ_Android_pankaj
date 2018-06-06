package com.clubz.ui.newsfeed.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.clubz.R;
import java.util.List;

public abstract class AdapterAutoTextView extends BaseAdapter implements Filterable{

    private List<String> originalList;
    private CustomFilter filter;
    private LayoutInflater mInflater;

    public AdapterAutoTextView(Context context, List<String> list){
        this.originalList = list;
        filter = new CustomFilter();
        mInflater = LayoutInflater.from(context);
    }

    public abstract void getFilterItemFromServer(String txt);

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Object getItem(int position) {
        return originalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final ViewHolder vh;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.simple_dropdown_item, parent, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        vh.label.setText(originalList.get(position));
        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class ViewHolder {
        TextView label;
        public ViewHolder(View view){
            label = view.findViewById(R.id.textView1);
        }
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            originalList.clear();

            // Check if the Original List and Constraint aren't null.
            if (constraint != null) {
                if(!TextUtils.isEmpty(constraint)){
                    getFilterItemFromServer(constraint.toString());
                }
            }
            // Create new Filter Results and return this to publishResults;
            FilterResults results = new FilterResults();
            results.values = originalList;
            results.count = originalList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
