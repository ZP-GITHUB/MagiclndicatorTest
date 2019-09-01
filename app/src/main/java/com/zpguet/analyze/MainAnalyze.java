package com.zpguet.analyze;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.aip.util.Base64Util;
import com.zpguet.framelayout.BodyView;
import com.zpguet.magiclndicatortest.R;
import com.zpguet.model.BodyAttr;
import com.zpguet.model.BodyKeyPoint;
import com.zpguet.model.BodySegment;
import com.zpguet.model.BodyTracking;
import com.zpguet.model.Gesture;
import com.zpguet.util.NetworkUtil;
import com.zpguet.util.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainAnalyze extends AppCompatActivity {
    private final String TAG = this.getClass().toString();

    // 手势识别
    private final int ACTION_GESTURE_CHOOSE_IMAGE = 1;
    // 人体属性：选择本地图片文件请求码
    private final int ACTION_ATTR_CHOOSE_IMAGE = 2;
    // 人像分割
    private final int ACTION_SEGMENT_CHOOSE_IMAGE = 3;
    // 人体关键点
    private final int ACTION_KEYPOINT_CHOOSE_IMAGE = 4;
    // 人流量静态分析
    private final int ACTION_STATIC_TRACKING_CHOOSE_IMAGE = 5;

    private Button btn;
    private Button btn1;

    private TextView tokenTextView;
    private TextView titleTextView;
    private TextView errorTextView;
    private ImageView imageViewOrigin;
    private ImageView imageView;
    private BodyView bodyView;
    private ImageView originView;
    private ImageView binaryView;

    private BodyAnalyze bodyAnalyze;
    private byte[] imageBytes;
    private HashMap typeMap = new HashMap();
    private String photoPath ;//照片保存路径

    private int screenWidth;
    private int screenHeight;

    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_analyze);

        this.initView();
        bodyAnalyze = new BodyAnalyze();
        bodyAnalyze.init();

        // 检查网络
        if (!NetworkUtil.isNetworkConnected(this)) {
            NetworkUtil.showNoNetWorkDlg(this);
        }

        Intent it = getIntent();
        //type为空时，默认为0
        int type = it.getIntExtra("type",1);
        typeMap.put("type",type);
        if(type == 5){
            btn1.setEnabled(false);
        }

        // android 7.0以上系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    private void initView() {
        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>屏幕宽高（w:" + screenWidth + ",h:" + screenHeight + ")");

        this.btn = (Button) this.findViewById(R.id.btn);
        this.btn1 = (Button) this.findViewById(R.id.btn1);

        this.tokenTextView = (TextView) this.findViewById(R.id.tokenTextView);
        this.titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        this.errorTextView = (TextView) this.findViewById(R.id.errorTextView);
        this.imageViewOrigin = (ImageView) this.findViewById(R.id.imageViewOrigin);
        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        this.bodyView = (BodyView) this.findViewById(R.id.bodyview);
        this.originView = (ImageView) this.findViewById(R.id.originView);
        this.binaryView = (ImageView) this.findViewById(R.id.binaryView);

        //打开相机
        this.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 为图片命名啊
                String name = new DateFormat().format("yyyymmdd",
                        Calendar.getInstance(Locale.CHINA))
                        + ".jpg";
                photoPath  = "/mnt/sdcard/test/" + name;// 保存路径
                Intent intent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(photoPath)));

                startActivityForResult(intent,1);
//                startActivityForResult(intent,1);
            }
        });

        this.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, ">>>>>>打开本地图片");

                // 检查网络
                if (!NetworkUtil.isNetworkConnected(MainAnalyze.this)) {
                    NetworkUtil.showNoNetWorkDlg(MainAnalyze.this);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String token = data.getString("token");
            tokenTextView.setText(token);
            Log.d(TAG, ">>>>>>>>token:" + token);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        this.errorTextView.setText("");
        this.imageViewOrigin.setImageBitmap(null);
        this.imageView.setImageBitmap(null);
        this.originView.setImageBitmap(null);
        this.binaryView.setImageBitmap(null);


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 1:
                    imageBytes = saveCameraImage(data ,photoPath );
                    break;
                case 2:
                    imageBytes = readCameraImage(data);
                    break;
                default:
                    break;
            }

            int type =(int) typeMap.get("type");

            if (type == ACTION_GESTURE_CHOOSE_IMAGE) {          // 手势识别
                titleTextView.setText("手势识别");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gesture gesture = bodyAnalyze.doGestureAnalyze(imageBytes);
                        if (gesture.getError_msg() != null) {
                            errorTextView.setText("Error:" + gesture.getError_msg());
                            return;
                        }
                        bodyView.setBody(gesture);
                    }
                }).start();
            }else if (type == ACTION_ATTR_CHOOSE_IMAGE) {               // 人体属性
                titleTextView.setText("人体属性");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BodyAttr body = bodyAnalyze.doAttrAnalyze(imageBytes);
                        if (body.getError_msg() != null) {
                            errorTextView.setText("Error:" + body.getError_msg());
                            return;
                        }
                        // 显示属性
                        bodyView.setBody(body);
                    }
                }).start();
            }else if (type == ACTION_SEGMENT_CHOOSE_IMAGE) {          // 人像分割
                titleTextView.setText("人像分割");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BodySegment segment = bodyAnalyze.doSegmentAnalyze(imageBytes);
                        if (segment.getError_msg() != null) {
                            errorTextView.setText("Error:" + segment.getError_msg());
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

                        Bitmap newbitmap = util.zoomImg(bitmap, width, height);

                        imageView.setImageBitmap(newbitmap);


                    }
                }).start();
            }else if (type == ACTION_KEYPOINT_CHOOSE_IMAGE) {                  // 人体关键点
                // 网络相关要开启子线程，不能在主线程访问
                titleTextView.setText("人体关键点");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BodyKeyPoint body = bodyAnalyze.doKeyPointAnalyze(imageBytes);
                        if (body.getError_msg() != null) {
                            errorTextView.setText("Error:" + body.getError_msg());
                            return;
                        }
                        // 绘制命令
                        bodyView.setBody(body);
                    }
                }).start();
            }else if (type == ACTION_STATIC_TRACKING_CHOOSE_IMAGE) {          // 人流量分析（静态）
                // 网络相关要开启子线程，不能在主线程访问
                titleTextView.setText("人流量分析（静态）");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BodyTracking bodyTracking = bodyAnalyze.doDynamicBodyTracking(imageBytes,
                                1,
                                "1,1," + (width - 1) + ",1," + (width - 1) + "," + height/2 + ",1," + height/2);
                        if (bodyTracking.getError_msg() != null) {
                            errorTextView.setText("Error:" + bodyTracking.getError_msg());
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
                    }
                }).start();
            }

        }

    }

    /** 保存相机的图片 **/
    private  byte[] saveCameraImage(Intent data , String fileName) {
        // 检查sd card是否存在
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "sd card is not avaiable/writeable right now.");
            return null;
        }

        Bitmap bmp = BitmapFactory.decodeFile(fileName);
//        Bitmap bmp = (Bitmap) data.getExtras().get("data");// 解析返回的图片成bitmap
        ByteArrayOutputStream baos;
        final int width;
        final int height;

        // 保存文件
        FileOutputStream fos = null;
        File file = new File("/mnt/sdcard/test/");
        file.mkdirs();// 创建文件夹


        try {// 写入SD card
            fos = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // 显示图片

        int twidth = bmp.getWidth();
        int theight = bmp.getHeight();
        if (twidth < this.screenWidth) {
            bmp = ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth(), bmp.getHeight());
        } else {
            float scale = this.screenWidth * 1.0f / twidth;
            bmp = ThumbnailUtils.extractThumbnail(bmp, this.screenWidth, (int) (bmp.getHeight() * scale));
        }

//                bitmap = ThumbnailUtils.extractThumbnail(bitmap, this.screenWidth, this.screenHeight/2);
        width = bmp.getWidth();
        height = bmp.getHeight();
        Log.d(TAG, "缩放后图片>>>>>>>>>Width:" + width + ", Height:" + height);

        // 设置imageview
//                this.imageView.setImageURI(mPath);


        this.imageViewOrigin.setImageBitmap(bmp);
        this.imageView.setImageBitmap(bmp);

        baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bytes会变小
        imageBytes = baos.toByteArray();
//        bmp.recycle();//回收
        return imageBytes;
    }

    /***读取相册里的照片*/
    private  byte[] readCameraImage(Intent data){
        // 选择本地图片结果
        Uri mPath = data.getData();
        Bitmap bitmap;

        final ByteArrayOutputStream baos;
        final byte[] imageBytes;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mPath);
            if (bitmap == null) {
                return null;
            }

            Log.d(TAG, "打开相册取到的原始图片>>>>>>>>>Width:" + bitmap.getWidth() + ", Height:" + bitmap.getHeight());
            int twidth = bitmap.getWidth();
            int theight = bitmap.getHeight();
            if (twidth < this.screenWidth) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight());
            } else {
                float scale = this.screenWidth * 1.0f / twidth;
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, this.screenWidth, (int) (bitmap.getHeight() * scale));
            }

//                bitmap = ThumbnailUtils.extractThumbnail(bitmap, this.screenWidth, this.screenHeight/2);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            Log.d(TAG, "缩放后图片>>>>>>>>>Width:" + width + ", Height:" + height);

            // 设置imageview
//                this.imageView.setImageURI(mPath);

            this.imageViewOrigin.setImageBitmap(bitmap);
            this.imageView.setImageBitmap(bitmap);


            if (this.bodyAnalyze == null) {
                return null;
            }


            // 获得图片字节数组方式一
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bytes会变小
            imageBytes = baos.toByteArray();


            // 方式二:不可以！！！应该是图片格式问题
//                ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
//                imageBytes = buffer.array();

            Log.d(TAG, ">>>>>>>>>>bitmap size:" + bitmap.getByteCount() + ", image bytes size:" + imageBytes.length);
            return imageBytes;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error:get Bitmap failed!");
            return null;
        }
    }

}
