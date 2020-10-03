package com.barmej.streetissues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.barmej.streetissues.Fragments.IssuesListFragment;
import com.barmej.streetissues.Fragments.IssuesMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private final List<Fragment> fragments=new ArrayList<>();
    private FloatingActionButton addIssueFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager=findViewById(R.id.contentFrameLayout);
        TabLayout tableLayout=findViewById(R.id.tabLayout);
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new IssuesListFragment());
        pagerAdapter.addFragment(new IssuesMapFragment());
        viewPager.setAdapter(pagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
       findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(MainActivity.this,AddNewIssuesActivity.class));
           }
       });



    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.issues_list);

                case 1:
                    return getString(R.string.issues_on_map);

                default:
                    return null;

            }

        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);
        }
    }


}
