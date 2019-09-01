package com.zpguet.framelayout;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a520wcf.yllistview.YLListView;
import com.zpguet.analyze.MainAnalyze;
import com.zpguet.magiclndicatortest.R;

import androidx.fragment.app.Fragment;


/**
 *
 */

public class TestFragment1 extends Fragment {
    private final String TAG = this.getClass().toString();

    private static final String[] items = new String[]{"手势识别", "人体属性", "人像分割","人体关键点","人流量分析"};
    private YLListView listView;
    Intent intent = new Intent();
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test_1, null);

        listView = (YLListView) view.findViewById(R.id.listView);
        // 不添加也有默认的头和底
        View topView=View.inflate(getActivity(),R.layout.top,null);
        listView.addHeaderView(topView);
//        View bottomView=new View(getApplicationContext());
//        listView.addFooterView(bottomView);

        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
//        listView.setFinalBottomHeight(100);
//        listView.setFinalTopHeight(100);

//        listView.setAdapter(new DemoAdapter());

        listView.setAdapter(new MyAdapter(items));

        //YLListView默认有头和底  处理点击事件位置注意减去
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
                switch (position){
                    case 0:
//                        Toast.makeText(getActivity(),"你点击了1按钮",Toast.LENGTH_SHORT).show();
                        intent.putExtra("type",1);
                        intent.setClass(getActivity(), MainAnalyze.class);
                        startActivity(intent);
                        break;//当我们点击某一项就能吐司我们点了哪一项

                    case 1:
//                        Toast.makeText(getActivity(),"你点击了2按钮",Toast.LENGTH_SHORT).show();
                        intent.putExtra("type",2);
                        intent.setClass(getActivity(), MainAnalyze.class);
                        startActivity(intent);
                        break;

                    case 2:
//                        Toast.makeText(getActivity(),"你点击了3按钮",Toast.LENGTH_SHORT).show();
                        intent.putExtra("type",3);
                        intent.setClass(getActivity(), MainAnalyze.class);
                        startActivity(intent);
                        break;

                    case 3:
//                        Toast.makeText(getActivity(),"你点击了4按钮",Toast.LENGTH_SHORT).show();
                        intent.putExtra("type",4);
                        intent.setClass(getActivity(), MainAnalyze.class);
                        startActivity(intent);
                        break;

                    case 4:
//                        Toast.makeText(getActivity(),"你点击了5按钮",Toast.LENGTH_SHORT).show();
                        intent.putExtra("type",5);
                        intent.setClass(getActivity(), MainAnalyze.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        return view;
    }


    class MyAdapter extends BaseAdapter {
        private String[] strings;
        public MyAdapter(String[] str) {
            this.strings = str;
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView!=null&&convertView instanceof TextView){
                tv= (TextView) convertView;
            }else{
                tv=new TextView(getActivity().getApplicationContext());
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            }
//            tv.setPadding(0,10,0,10);
            tv.setGravity(Gravity.CENTER);
            tv.setText(String.format(items[position], position));
            return tv;
        }

    }

}
