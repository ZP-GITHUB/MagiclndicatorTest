package com.zpguet.fragment;



import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a520wcf.yllistview.YLListView;
import com.baidu.aip.util.Base64Util;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.zpguet.analyze.BodyAnalyze;
import com.zpguet.widget.BodyView;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.model.BodyAttr;
import com.zpguet.model.BodyKeyPoint;
import com.zpguet.model.BodySegment;
import com.zpguet.model.BodyTracking;
import com.zpguet.model.Gesture;
import com.zpguet.util.NetworkUtil;
import com.zpguet.util.StreamUtil;
import com.zpguet.util.BitmapUtil;

import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 *
 */

public class ImageRecognizeFragment extends Fragment {
    private final String TAG = this.getClass().toString();

    private static final String[] items = new String[]{"手势识别", "人体属性", "人像分割","人体关键点","人流量分析"};
    private YLListView listView;
    Intent intent = new Intent();
    private View view;
    private Executor executor = new ThreadPoolExecutor(5,10,100, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(5));
    private View redoImage;
    private FrameLayout frameLayout;
    private CameraView cameraView;
    private ConstraintLayout layoutCameraOption;
    private ImageView imageView;
    private TabLayout tabLayout;
    private BodyView bodyView;
    private BodyAnalyze bodyAnalyze;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bodyAnalyze = new BodyAnalyze();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_image_recognize, null);
        bodyAnalyze.init(view.getContext());
//        listView = (YLListView) view.findViewById(R.id.listView);
//        // 不添加也有默认的头和底
//        View topView=View.inflate(getActivity(),R.layout.top,null);
//        listView.addHeaderView(topView);
//        View bottomView=new View(getApplicationContext());
//        listView.addFooterView(bottomView);

        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
//        listView.setFinalBottomHeight(100);
//        listView.setFinalTopHeight(100);

//        listView.setAdapter(new DemoAdapter());

//        listView.setAdapter(new MyAdapter(items));
//
//        //YLListView默认有头和底  处理点击事件位置注意减去
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position=position-listView.getHeaderViewsCount();
//                switch (position){
//                    case 0:
////                        Toast.makeText(getActivity(),"你点击了1按钮",Toast.LENGTH_SHORT).show();
//                        intent.putExtra("type",1);
//                        intent.setClass(getActivity(), MainAnalyze.class);
//                        startActivity(intent);
//                        break;//当我们点击某一项就能吐司我们点了哪一项
//
//                    case 1:
////                        Toast.makeText(getActivity(),"你点击了2按钮",Toast.LENGTH_SHORT).show();
//                        intent.putExtra("type",2);
//                        intent.setClass(getActivity(), MainAnalyze.class);
//                        startActivity(intent);
//                        break;
//
//                    case 2:
////                        Toast.makeText(getActivity(),"你点击了3按钮",Toast.LENGTH_SHORT).show();
//                        intent.putExtra("type",3);
//                        intent.setClass(getActivity(), MainAnalyze.class);
//                        startActivity(intent);
//                        break;
//
//                    case 3:
////                        Toast.makeText(getActivity(),"你点击了4按钮",Toast.LENGTH_SHORT).show();
//                        intent.putExtra("type",4);
//                        intent.setClass(getActivity(), MainAnalyze.class);
//                        startActivity(intent);
//                        break;
//
//                    case 4:
////                        Toast.makeText(getActivity(),"你点击了5按钮",Toast.LENGTH_SHORT).show();
//                        intent.putExtra("type",5);
//                        intent.setClass(getActivity(), MainAnalyze.class);
//                        startActivity(intent);
//                        break;
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView = view.findViewById(R.id.cameraView);
        redoImage= view.findViewById(R.id.redoImage);
        cameraView.bindToLifecycle(this);
        frameLayout = view.findViewById(R.id.layoutFrameCamera);
        layoutCameraOption = view.findViewById(R.id.layoutCameraSection);
        imageView = view.findViewById(R.id.imageView);
        tabLayout = view.findViewById(R.id.tabImageAnalyze);
        bodyView = view.findViewById(R.id.bodyview);
        MaterialCardView imageCapture = view.findViewById(R.id.captureButton);
        View selectFromGallery = view.findViewById(R.id.selectFromGallery);
        imageCapture.setOnClickListener(v -> {
            if (redoImage.getVisibility() != View.VISIBLE) {
                File file = new File(view.getContext().getCacheDir().getPath() + File.separator + "imageCapture" + System.currentTimeMillis());
                file.deleteOnExit();
                Dialog dialog = new MaterialAlertDialogBuilder(imageView.getContext())
                        .setTitle("正在处理")
                        .setMessage("正在拍照，请稍等")
                        .show();
                cameraView.takePicture(executor, new ImageCapture.OnImageCapturedListener() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
                        Image img = image.getImage();
                        if (img != null) {

                            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[byteBuffer.capacity()];
                            byteBuffer.get(bytes);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(rotationDegrees);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                            bytes = StreamUtil.compressBitmap(bitmap,getResources().getDisplayMetrics().widthPixels);
                            final byte[] buffer = bytes;
                            new Handler(Looper.getMainLooper()).post(() -> {
//                                Toast.makeText(imageView.getContext(),String.valueOf(rotationDegrees),Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                showResult(tabLayout.getSelectedTabPosition(),buffer);
                            });
                        }
                        super.onCaptureSuccess(image, rotationDegrees);
                    }
                });
//                cameraView.takePicture(file, executor, new ImageCapture.OnImageSavedListener() {
//                    @Override
//                    public void onImageSaved(@NonNull File file) {
//                        try {
//                            Image image;
//
//                            FileInputStream outStream = new FileInputStream(file);
//                            byte[] buffer = StreamUtils.compressBitmap(outStream,redoImage.getContext());
////                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////                            byte[] bytes = new byte[1024];
////                            int len;
////                            while ((len = outStream.read(bytes)) != -1) {
////                                byteArrayOutputStream.write(bytes,0,len);
////                            }
////                            outStream.close();
////                            byteArrayOutputStream.close();
////                            byte[] buffer = byteArrayOutputStream.toByteArray();
//                            new Handler(Looper.getMainLooper()).post(() -> {
//                                Toast.makeText(view.getContext(), file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                                Glide.with(imageView)
//                                        .load(image)
//                                        .into(imageView);
//                                showResult(tabLayout.getSelectedTabPosition(),buffer);
//                            });
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                        Toast.makeText(getContext(), String.format("获取图片失败，错误：%s", message), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }else {
                hideAllAnalyzeView();
            }
        });
        frameLayout.setOnClickListener(v -> {
            if (redoImage.getVisibility() == View.VISIBLE) {
                if (layoutCameraOption.getVisibility() != View.VISIBLE) {
                    layoutCameraOption.setVisibility(View.VISIBLE);
                }else {
                    layoutCameraOption.setVisibility(View.GONE);
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hideAllAnalyzeView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        selectFromGallery.setOnClickListener(v -> {
            if (!NetworkUtil.isNetworkConnected(v.getContext())) {
                NetworkUtil.showNoNetWorkDlg(v.getContext());
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,2);
            }
        });
    }

    private void hideAllAnalyzeView() {
        redoImage.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        cameraView.setVisibility(View.VISIBLE);
    }
    private void showResult(int type, byte[] imageBytes) {
        redoImage.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.GONE);
//        layoutCameraOption.setVisibility(View.GONE);
        Bitmap image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        Glide.with(imageView)
                .load(image)
                .into(imageView);
        switch (type) {
            case 0:
                executor.execute(() -> {
                    Gesture gesture = bodyAnalyze.doGestureAnalyze(imageBytes);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (gesture != null && gesture.getError_msg() != null) {
                            Toast.makeText(getContext(),"Error：" + gesture.getError_msg(),Toast.LENGTH_SHORT).show();
                        }else if (gesture != null){
                            bodyView.setBody(gesture);
                        }
                    });

                });
                break;
            case 1:
                executor.execute(() -> {
                    BodyAttr body = bodyAnalyze.doAttrAnalyze(imageBytes);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (body.getError_msg() != null) {
                            Toast.makeText(getContext(),"Error:" + body.getError_msg(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    // 显示属性
                    bodyView.setBody(body);
                });
                break;
            case 2:
                executor.execute(() -> {
                    BodySegment segment = bodyAnalyze.doSegmentAnalyze(imageBytes);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (segment.getError_msg() != null) {
                            Toast.makeText(getContext(),"Error:" + segment.getError_msg(),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String base64Str = segment.getLabelmap();
                        if (base64Str == null) {
                            Log.e(TAG, ">>>>>>>>image is null");
                            return;
                        }

                        // 解析base64字符串-->byte数组（二值图像数组：）
                        byte[] bytes = Base64Util.decode(base64Str);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        Bitmap newbitmap = BitmapUtil.zoomImg(bitmap, image.getWidth(), image.getHeight());

                        imageView.setImageBitmap(newbitmap);
                    });
                });
                break;
            case 3:
                executor.execute(() -> {
                    BodyKeyPoint body = bodyAnalyze.doKeyPointAnalyze(imageBytes);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (body.getError_msg() != null) {
                            Toast.makeText(getContext(),"Error:" + body.getError_msg(),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 绘制命令
                        bodyView.setBody(body);
                    });
                });
                break;
            case 4:
                executor.execute(() -> {
                    BodyTracking bodyTracking = bodyAnalyze.doDynamicBodyTracking(imageBytes,
                            1,
                            "1,1," + (image.getWidth() - 1) + ",1," + (image.getWidth() - 1) + "," + image.getHeight()/2 + ",1," + image.getHeight()/2);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (bodyTracking.getError_msg() != null) {
                            Toast.makeText(getContext(),"Error:" + bodyTracking.getError_msg(),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 如果列表不为空，则绘制检测框（静态下好像都为空）
                        if (bodyTracking.getPerson_info().size() > 0){
                            // 绘制命令
                            bodyView.setBody(bodyTracking);
                        }

                        if (bodyTracking.getImage() != null){
                            byte[] bytes = Base64Util.decode(bodyTracking.getImage());
                            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap1);
                        }
                    });
                });
                break;
                default:
                    break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData()!= null && requestCode == 2 ) {
            Glide.with(this)
                    .load(data.getData())
                    .into(imageView);
            executor.execute(() -> {
                try {
                    InputStream inputStream = imageView.getContext().getContentResolver().openInputStream(data.getData());
                    byte[] bytes = StreamUtil.compressBitmap(inputStream,imageView.getContext());
                    new Handler(Looper.getMainLooper()).post(() -> {
                            showResult(tabLayout.getSelectedTabPosition(), bytes);
                        });
//                    if (inputStream != null) {
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        byte[] bytes = new byte[1024];
//                        int len;
//                        while ((len = inputStream.read(bytes)) != -1) {
//                            byteArrayOutputStream.write(bytes, 0, len);
//                        }
//                        inputStream.close();
//                        byteArrayOutputStream.close();
//                        byte[] buffer = byteArrayOutputStream.toByteArray();
//                        new Handler(Looper.getMainLooper()).post(() -> {
//                            showResult(tabLayout.getSelectedTabPosition(), buffer);
//                        });
//                    }


                }catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
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
