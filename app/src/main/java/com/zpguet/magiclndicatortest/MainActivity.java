package com.zpguet.magiclndicatortest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;
import com.google.android.material.tabs.TabLayout;
import com.zpguet.framelayout.TestFragment1;
import com.zpguet.framelayout.TestFragment2;
import com.zpguet.framelayout.TestFragment3;
import com.zpguet.framelayout.TestFragment4;
import com.zpguet.util.SystemUIUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String[] CHANNELS = new String[]{"图像识别", "智能聊天", "语音识别","我的语录"};
//    private static final String[] CHANNELS = new String[]{"图像识别", "智能聊天", "语音识别"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
//    private ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);


//    private MyPagerAdapter myPagerAdapter =
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isGranted = true;
        for (String permission: permissions
             ) {
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions,100);
                isGranted = false;
                break;
            }
        }
        if (isGranted)
            initLayout();
    }

    private void initLayout() {
        Utils.init(this);
        SystemUIUtil.fitStatusBar(getWindow(),true,false);
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorSurface,value,true);
        getWindow().setStatusBarColor(value.data);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        List<Fragment> list = new ArrayList<>();
        list.add(new TestFragment1());
        list.add(new TestFragment2());
        list.add(new TestFragment3());
        list.add(new TestFragment4());
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),list,CHANNELS);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPadding(0,0,0,mViewPager.getPaddingBottom());
        tabLayout = findViewById(R.id.tabLayout);
//        mViewPager.setAdapter(mExamplePagerAdapter);
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(mViewPager);
//        initMagicIndicator3();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"请重新打开应用进行授权",Toast.LENGTH_SHORT).show(); ;
                finish();
                return ;
            }
        }
        initLayout();
    }

    private void initMagicIndicator3() {
//        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator3);
//        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
//        CommonNavigator commonNavigator = new CommonNavigator(this);
//        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
//            @Override
//            public int getCount() {
//                return mDataList == null ? 0 : mDataList.size();
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, final int index) {
//                BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
//
//                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
//                clipPagerTitleView.setText(mDataList.get(index));
//                clipPagerTitleView.setTextColor(Color.parseColor("#808080"));
//                clipPagerTitleView.setClipColor(Color.WHITE);
//                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mViewPager.setCurrentItem(index);
//                    }
//                });
//                badgePagerTitleView.setInnerPagerTitleView(clipPagerTitleView);
//
//                return badgePagerTitleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                float navigatorHeight = context.getResources().getDimension(R.dimen.common_navigator_height);
//                float borderWidth = UIUtil.dip2px(context, 1);
//                float lineHeight = navigatorHeight - 2 * borderWidth;
//                indicator.setLineHeight(lineHeight);
//                indicator.setRoundRadius(lineHeight / 2);
//                indicator.setYOffset(borderWidth);
//                indicator.setColors(Color.parseColor("#2690DF"));
//                return indicator;
//            }
//        });
//        magicIndicator.setNavigator(commonNavigator);
//        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }
}
