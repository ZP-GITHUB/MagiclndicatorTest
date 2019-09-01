package com.zpguet.magiclndicatortest;

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

    public MyPagerAdapter(FragmentManager fm,List<Fragment> framgentList) {
        super(fm);
        this.list = framgentList;
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
