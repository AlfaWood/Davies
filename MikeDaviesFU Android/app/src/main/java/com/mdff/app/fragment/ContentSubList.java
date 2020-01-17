package com.mdff.app.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.adapter.CustomContentListItemAdapter;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentSubList extends Fragment {

    ProgressDialog pDialog;
    //private RecyclerView rv_resource;
    ListView lv_resourcesubList;
    private LinearLayoutManager linearLayoutManager;
    private View view, verticalLine;


    int totalHeight = 0;
    private TextView tv_header_title;
    private int connectStatus;
    private AppUtil appUtil;
    private CustomContentListItemAdapter customContentListItemAdapter;
    private AlertMessage alertMessage;
    View view_bottom;
    LinearLayout backLayout;
    private ArrayList<ResourceItemContent> resourceItemContents;


    public ContentSubList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content_sub_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
//        Bundle bundle = this.getArguments();

        appUtil = new AppUtil(getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        resourceItemContents = (ArrayList<ResourceItemContent>) getArguments().getSerializable("contentsubList");
        linkUIElements(this.view);
    }

    private void linkUIElements(View view) {
        backLayout = (LinearLayout) view.findViewById(R.id.backLayout);
        view_bottom = (View) view.findViewById(R.id.view_bottom);
        tv_header_title = (TextView) view.findViewById(R.id.tv_header_title);

        lv_resourcesubList = (ListView) view.findViewById(R.id.lv_resourcesubList);
        tv_header_title.setText(resourceItemContents.get(0).getTopic());
        setListAdapter();
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }
    private void setListAdapter() {
        customContentListItemAdapter = new CustomContentListItemAdapter(resourceItemContents, getActivity(), getActivity().getSupportFragmentManager());
        lv_resourcesubList.setAdapter(customContentListItemAdapter);
    }

}
