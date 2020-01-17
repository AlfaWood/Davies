package com.mdff.app.adapter;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.app_interface.ResoucrceInterface;
import com.mdff.app.model.ResourceItems;

import java.util.ArrayList;

/**
 * Created by Deepika.Mishra on 5/8/2018.
 */

public class CustomResourceSubListItemsAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private ArrayList<ResourceItems> itemList;
    private Context context;
    FragmentManager fragmentManager;
    private TextView tv_title;LinearLayout ll_top;
    private ResoucrceInterface resoucrceInterface;
    private ArrayList<ResourceItems> resourcesSubListForSearches;
    private String dataString;

    public CustomResourceSubListItemsAdapter(ArrayList<ResourceItems> itemList, Context context, FragmentManager fragmentManager,ResoucrceInterface resoucrceInterface) {
        this.resourcesSubListForSearches = itemList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.resoucrceInterface=resoucrceInterface;

    }

    @Override
    public int getCount() {
        return resourcesSubListForSearches.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.resource_item, null);

        tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        ll_top = (LinearLayout) convertView.findViewById(R.id.ll_top);
        tv_title.setText(resourcesSubListForSearches.get(position).getTopic());
        ll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (resourcesSubListForSearches.get(position).getSub_topic_id().equals("0")) {

                    resoucrceInterface.displayContent(resourcesSubListForSearches.get(position).getId(),fragmentTransaction);

                }
                else if (resourcesSubListForSearches.get(position).getSub_topic_id().equals("1")) {

                    resoucrceInterface.displaySublist(resourcesSubListForSearches.get(position), fragmentTransaction);
                }

            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                System.out.println(resourcesSubListForSearches);
                System.out.println(results.values);
                resourcesSubListForSearches = (ArrayList<ResourceItems>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
//                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<ResourceItems> FilteredArrList = new ArrayList<ResourceItems>();
                if (itemList == null) {
                    itemList = new ArrayList<ResourceItems>(resourcesSubListForSearches); // saves the original data in recordsList
                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = itemList.size();
                    results.values = itemList;
                } else {

                    constraint = constraint.toString().toLowerCase();
                    constraint = constraint.toString().replace("\n", "");
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).getTopic() != null) {
                            dataString = itemList.get(i).getTopic().trim();
                            if (dataString.toLowerCase().contains(constraint.toString().trim())) {

                                FilteredArrList.add(new ResourceItems(itemList.get(i).getTopic(), itemList.get(i).getId(),itemList.get(i).getSub_topic_id()));

                            }
                        } else {
                            dataString = itemList.get(i).getTopic();
                            if (dataString.toLowerCase().contains(constraint.toString().trim())) {
                                FilteredArrList.add(new ResourceItems(itemList.get(i).getTopic(), itemList.get(i).getId(),itemList.get(i).getSub_topic_id()));

                            }

                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;

    }
}
