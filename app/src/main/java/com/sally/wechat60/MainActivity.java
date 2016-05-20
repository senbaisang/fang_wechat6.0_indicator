package com.sally.wechat60;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.sally.wechat60.fragment.TabFragment;
import com.sally.wechat60.view.ChangeColorView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]{"fragment1", "fragment2", "fragment3", "fragment4"};
    private FragmentPagerAdapter mAdapter;

    private List<ChangeColorView> mIndicators = new ArrayList<ChangeColorView>();
    private ChangeColorView mIndicatorOne, mIndicatorTwo, mIndicatorThree, mIndicatorFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initDatas();
        initEvents();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 从第一页 -> 第二页  position 0 positionOffset 0~1
                // 从第二页 -> 第一页  position 0 positionOffset 1~0
                if(positionOffset > 0) {
                    ChangeColorView exit = mIndicators.get(position);
                    ChangeColorView enter = mIndicators.get(position+1);

                    exit.setIconAlpha(1 - positionOffset);
                    enter.setIconAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mIndicatorOne = (ChangeColorView) findViewById(R.id.id_one);
        mIndicatorTwo = (ChangeColorView) findViewById(R.id.id_two);
        mIndicatorThree = (ChangeColorView) findViewById(R.id.id_three);
        mIndicatorFour = (ChangeColorView) findViewById(R.id.id_four);
        mIndicators.add(mIndicatorOne);
        mIndicators.add(mIndicatorTwo);
        mIndicators.add(mIndicatorThree);
        mIndicators.add(mIndicatorFour);

        mIndicators.get(0).setIconAlpha(1.0f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initDatas() {
        for(String title : mTitles) {
            TabFragment fragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TabFragment.TITLE, title);
            fragment.setArguments(bundle);
            mTabs.add(fragment);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };
    }

    private void initEvents() {
        mIndicatorOne.setOnClickListener(this);
        mIndicatorTwo.setOnClickListener(this);
        mIndicatorThree.setOnClickListener(this);
        mIndicatorFour.setOnClickListener(this);
    }

    /**
     * 设置menu显示icon
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu != null) {
            if(menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * actionbar menu 默认更多的按钮不现实，使用反射，将其显示出来
     */
    private void setOverflowButtonAlways() {
        ViewConfiguration config = ViewConfiguration.get(this);
        try {
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKey.setAccessible(true);
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        resetIndicatorsState();
        switch(v.getId()) {
            case R.id.id_one:
                mIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_two:
                mIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_three:
                mIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_four:
                mIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    private void resetIndicatorsState() {
        for(int i=0; i<mIndicators.size(); i++) {
            mIndicators.get(i).setIconAlpha(0.0f);
        }
    }
}
