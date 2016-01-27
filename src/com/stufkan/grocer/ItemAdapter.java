package com.stufkan.grocer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {


    Context context;
    int layoutResourceId;
    List<Item> data = null;
    ItemsFilter dataFilter;

    public ItemAdapter(Context context, int resource,
                       List<Item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, null);

            holder = new ItemHolder();
            holder.selectedButton = (ToggleButton) convertView.findViewById(R.id.toggleButton1);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.textView1);
            Log.w("grocer", "inflated " + holder.toString());

            holder.selectedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int getPosition = (Integer) compoundButton.getTag();
                    data.get(getPosition).setSelected(compoundButton.isChecked());
                    Log.w("Grocer", (b ? "Checked" : "Unchecked"));
                }
            });

            convertView.setTag(holder);
            convertView.setTag(R.id.textView1, holder.txtTitle);
            convertView.setTag(R.id.toggleButton1, holder.selectedButton);
        } else {
            holder = (ItemHolder) convertView.getTag();
            Log.w("grocer", "used already inflated convertView " + holder.toString());
        }
        holder.selectedButton.setTag(position);

        holder.txtTitle.setText(data.get(position).getName());
        holder.selectedButton.setChecked(data.get(position).getSelected());

        return convertView;
    }


    static class ItemHolder {
        TextView txtTitle;
        ToggleButton selectedButton;
    }

    //filterable

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Item getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getPosition(Item item) {
        return data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (dataFilter == null) {
            dataFilter = new ItemsFilter();
        }
        return dataFilter;
    }

    class ItemsFilter extends Filter {
        final Object lock = new Object();
        ArrayList<Item> mOriginalValues;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // Initiate our results object

            Log.d("perform","data før" + data.toString());

            FilterResults results = new FilterResults();
            // If the adapter array is empty, check the actual items array and use it
            if (mOriginalValues == null) {
                synchronized (lock) { // Notice the declaration above
                    mOriginalValues = new ArrayList<Item>(data);
                }
            }
            // No prefix is sent to filter by so we're going to send back the original array
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<Item> list = new ArrayList<Item>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // Compare lower case strings
                String prefixString = prefix.toString().toLowerCase();
                // Local to here so we're not changing actual array
                final ArrayList<Item> items = (ArrayList<Item>) mOriginalValues;
                final int count = items.size();
                final ArrayList<Item> newItems = new ArrayList<Item>(count);

                for (int i = 0; i < count; i++) {
                    final Item item = items.get(i);
                    final String itemName = item.getName().toString().toLowerCase();
                    // First match against the whole, non-splitted value
                    if (itemName.startsWith(prefixString)) {
                        newItems.add(item);
                    } else {
                    } /* This is option and taken from the source of ArrayAdapter
                               final String[] words = itemName.split(" ");
	                           final int wordCount = words.length;
	                           for (int k = 0; k < wordCount; k++) {
	                               if (words[k].startsWith(prefixString)) {
	                                   newItems.add(item);
	                                   break;
	                               }
	                           }
	                       } */
                }
                // Set and return
                results.values = newItems;
                results.count = newItems.size();
            }

            Log.d("perform", prefix.toString());
            Log.d("perform","morig"+ mOriginalValues.toString());
            Log.d("perform", "data efter" + data.toString());
            Log.d("perform",results.values.toString());

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            data = (ArrayList<Item>) results.values;
            // Let the adapter know about the updated list

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
