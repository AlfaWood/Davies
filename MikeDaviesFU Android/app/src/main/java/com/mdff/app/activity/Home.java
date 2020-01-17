package com.mdff.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.fragment.Feed;
import com.mdff.app.fragment.Messages;
import com.mdff.app.fragment.QA;
import com.mdff.app.fragment.Resources;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.BottomNavigationHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {

    BottomNavigationView bottomNavigationView;public ArrayList<com.mdff.app.model.Feed> feedList;
    ViewPager viewPager;Intent intent;public AppUtil appUtil;Activity activity;private LinearLayout loadingLayout,homeLayout;
    MenuItem prevMenuItem;public String comeFrom; public boolean isFinish=false; public static int badge_count;  public  static TextView tv_badge;
    MyReceiver reMyreceive;
    IntentFilter intentfilter1;
    LinearLayout ll_imv_profileLayout;CircleImageView imv_profile;
    public double page_count = 0;
    public int count=0;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = Home.this;
        appUtil = new AppUtil(activity);
        intent = getIntent();
        reMyreceive = new MyReceiver();
        intentfilter1 = new IntentFilter("message.came");
        try {
            comeFrom = intent.getStringExtra("comeFrom");
            page_count = intent.getExtras().getDouble("page_count");
            count = intent.getExtras().getInt("count");
            System.out.print(page_count+count);

        } catch (Exception e) {
            System.out.print(e);
        }
        if(comeFrom.equalsIgnoreCase("messageDetail")) {
            feedList=new ArrayList<>();
        }
        else {
            try {
                feedList = (ArrayList<com.mdff.app.model.Feed>) intent.getSerializableExtra("feedList");
                System.out.print(feedList);
                badge_count = intent.getExtras().getInt("badgecount");
            } catch (Exception e) {
                System.out.print(e);
            }
        }

        ll_imv_profileLayout=(LinearLayout)findViewById(R.id.imv_profileLayout);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        imv_profile=(CircleImageView) findViewById(R.id.imv_profile);
        setProfile();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        BottomNavigationHelper.removeShiftMode(bottomNavigationView);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(this)
                .inflate(R.layout.custom_tab, bottomNavigationMenuView, false);
         tv_badge=(TextView)badge.findViewById(R.id.notificationsbadge);
        if(badge_count>0) {
            tv_badge.setText("" + badge_count);
            tv_badge.setVisibility(View.VISIBLE);
        }
        else
        {
            tv_badge.setVisibility(View.GONE);
        }
        ll_imv_profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
                Intent i= new Intent(Home.this,UserProfile.class);
                startActivity(i);

            }
        });


        itemView.addView(badge);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.feed_tab:
                        Feed.pageName="feed";
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.resource_tab:
                        Feed.pageName="resource";
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.qa_tab:
                        Feed.pageName="qa";
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.msg_tab:
                        Feed.pageName="inbox";
                        viewPager.setCurrentItem(3);
                        break;
                }

                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
                switch (position) {
                    case 0:
                        Feed.pageName="feed";
                        break;
                    case 1:
                        Feed.pageName="resource";
                        break;
                    case 2:
                        Feed.pageName="qa";
                        break;
                    case 3:
                        Feed.pageName="inbox";
                        break;
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }
    public void setProfile()
    {
        try{
            Picasso.get().load(appUtil.getPrefrence("profile_pic"))//download URL
                    .error(R.drawable.profile)//if failed
                    .placeholder(R.drawable.profile)
                    .into(imv_profile);//imageview
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(reMyreceive);

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setProfile();
        registerReceiver(reMyreceive, intentfilter1);
        appUtil.setInboxNotificationCount(tv_badge);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {

    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d("sohail", "MyReceiver: broadcast received");
            appUtil.setInboxNotificationCount(tv_badge);

        }
    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        if(isFinish)
        {
            isFinish=false;
            dialog.dismiss();
            finish();
        }
        else {
            dialog.dismiss();
        }

    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Feed());
        adapter.addFragment(new Resources());
        adapter.addFragment(new QA());
        adapter.addFragment(new Messages());
        viewPager.setAdapter(adapter);
        if(comeFrom.equalsIgnoreCase("messageDetail")) {
            viewPager.setCurrentItem(3);

        }
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else if(comeFrom.equals("signup"))
        {
            comeFrom="";
            System.exit(0);
        }
        else {
            this.finish();

        }
    }
}
