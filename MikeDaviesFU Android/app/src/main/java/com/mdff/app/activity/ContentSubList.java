package com.mdff.app.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.adapter.CustomContentListItemAdapter;
import com.mdff.app.fragment.Resources;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;

import java.util.ArrayList;

public class ContentSubList extends AppCompatActivity {
    ListView lv_resourcesubList;
    private EditText et_search;
    private TextView tv_header_title;
    private int connectStatus;
    private AppUtil appUtil;
    private CustomContentListItemAdapter customContentListItemAdapter;
    private AlertMessage alertMessage;
    View view_bottom;
    LinearLayout backLayout;Activity activity;
    private ArrayList<ResourceItemContent> resourceItemContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_content_sub_list);
        activity=ContentSubList.this;
        appUtil = new AppUtil(activity);
        connectStatus = ConnectivityReceiver.isConnected(activity);
//        resourceItemContents = (ArrayList<ResourceItemContent>) getIntent().getSerializableExtra("contentsubList");
        try {
            if (getIntent().getStringExtra("comeFrom").equalsIgnoreCase("resource")) {
                resourceItemContents = Resources.resourceItemContentsStatic;
                Resources.resourceItemContentsStatic = null;
            } else {

                resourceItemContents = ResourceSubList.resourceItemContentsStatic;
                ResourceSubList.resourceItemContentsStatic = null;
            }
        }
        catch (Exception e)
        {

        }

        linkUIElements();        
    }

    private void linkUIElements() {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        view_bottom = (View) findViewById(R.id.view_bottom);
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setTextColor(getResources().getColor(R.color.black));
        et_search.setCursorVisible(false);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/HelveticaNeue-Light.ttf");
        et_search.setTypeface(face);
        lv_resourcesubList = (ListView) findViewById(R.id.lv_resourcesubList);
        if(!resourceItemContents.get(0).getTopic().equals(""))
        tv_header_title.setText(resourceItemContents.get(0).getTopic());
        setListAdapter();
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    appUtil.hideSoftKeyboard(ContentSubList.this);
                    handled = true;
                }
                return handled;
            }
        });
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                et_search.setCursorVisible(true);


            }
        });
        //search topic on edittext
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                try {
                    if (et_search.hasFocus() && !cs.toString().isEmpty()) {
                        //    customResourceListAdapter.getFilter().filter(cs.toString());
                        customContentListItemAdapter.getFilter().filter(cs.toString());
                        // Do whatever
                    }
                    else if(et_search.hasFocus() && cs.toString().isEmpty())
                    {
                        customContentListItemAdapter = new CustomContentListItemAdapter(resourceItemContents, activity, getSupportFragmentManager());
                        lv_resourcesubList.setAdapter(customContentListItemAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // When user changed the Text
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }
    private void setListAdapter() {
        customContentListItemAdapter = new CustomContentListItemAdapter(resourceItemContents, activity, getSupportFragmentManager());
        lv_resourcesubList.setAdapter(customContentListItemAdapter);
    }
}
