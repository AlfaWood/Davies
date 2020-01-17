package com.mdff.app.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentManager;
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
import com.mdff.app.model.ResourceItemContent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Swati.Gupta on 7/26/2018.
 */

public class CustomContentListItemAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private ArrayList<ResourceItemContent> itemList;
    ArrayList<ResourceItemContent> FilteredArrList;
    private ArrayList<ResourceItemContent> resourcesSubContentListForSearches;
    private Context context;
    private String dataString;
    FragmentManager fragmentManager;
    private TextView tv_title;LinearLayout ll_top;
    private ResoucrceInterface resoucrceInterface;

    public CustomContentListItemAdapter(ArrayList<ResourceItemContent> itemList, Context context, FragmentManager fragmentManager) {
//        this.itemList = itemList;
        this.resourcesSubContentListForSearches = itemList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return resourcesSubContentListForSearches.size();
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
        if(FilteredArrList!=null)
            tv_title.setText(FilteredArrList.get(position).getTopic());
        else
        tv_title.setText(resourcesSubContentListForSearches.get(position).getTitle());
        ll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Call Content Fragment*************************

               /* Bundle bundle = new Bundle();
                bundle.putSerializable("resourcesubList", resourcesSubContentListForSearches.get(position));
                Fragment fragment = new ResourceContentItem();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter
                            ,
                            R.animator.exit,
                            R.animator.left_to_right,
                            R.animator.right_to_left
                    );
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }*/
                Intent i=new Intent(context, com.mdff.app.activity.ResourceContentItem.class);
                i.putExtra("resourcesubList", (Serializable)resourcesSubContentListForSearches.get(position));
               context.startActivity(i);

            }
        });

        return convertView;
    }

    // Added by Anurag Mishra on 18-08-2018
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            String s="";
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                System.out.println(resourcesSubContentListForSearches);
                System.out.println(results.values);
                resourcesSubContentListForSearches = (ArrayList<ResourceItemContent>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
//                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                FilteredArrList = new ArrayList<ResourceItemContent>();
                if (itemList == null) {
                    itemList = new ArrayList<ResourceItemContent>(resourcesSubContentListForSearches); // saves the original data in recordsList
                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = itemList.size();
                    results.values = itemList;
                } else {

                    constraint = constraint.toString().toLowerCase();
                    constraint = constraint.toString().replace("\n", "");
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).getTitle() != null) {
                            dataString = itemList.get(i).getTitle().trim();
                            if (dataString.toLowerCase().contains(constraint.toString().trim())) {

                                FilteredArrList.add(new ResourceItemContent(itemList.get(i).getTitle(), itemList.get(i).getId(), itemList.get(i).getTopic_id(),itemList.get(i).getThumbnail_url(),itemList.get(i).getTitle(),itemList.get(i).getVideo_url(),itemList.get(i).getLocation(),itemList.get(i).getDescription(),itemList.get(i).getCreated_at(),itemList.get(i).getImage(),itemList.get(i).getType()));
                            }
                        } else {
                            dataString = itemList.get(i).getTitle();
                            if (dataString.toLowerCase().contains(constraint.toString().trim())) {
                                FilteredArrList.add(new ResourceItemContent(itemList.get(i).getTitle(), itemList.get(i).getId(), itemList.get(i).getTopic_id(),itemList.get(i).getThumbnail_url(),itemList.get(i).getTitle(),itemList.get(i).getVideo_url(),itemList.get(i).getLocation(),itemList.get(i).getDescription(),itemList.get(i).getCreated_at(),itemList.get(i).getImage(),itemList.get(i).getType()));
                            }

                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                resourcesSubContentListForSearches=FilteredArrList;


                return results;
            }
        };
        return filter;
    }
}
