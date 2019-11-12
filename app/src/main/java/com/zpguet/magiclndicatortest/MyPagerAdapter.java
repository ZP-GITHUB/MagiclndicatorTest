package com.zpguet.magiclndicatortest;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/6/27
 * Time: 15:16
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private String[] titles;
    public MyPagerAdapter(FragmentManager fm,List<Fragment> framgentList, String[] titles) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = framgentList;
        this.titles = titles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= titles.length) {
            return "";
        }else {
            return titles[position];
        }
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
