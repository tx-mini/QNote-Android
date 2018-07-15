package com.ace.qnote.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.qnote.R;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import csu.edu.ice.model.util.SpNameConstant;

public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView mCameraSf;
    private ImageButton mTakePicBtn;
    private Button mContinueTakePic;
    private Button mReTakePhoto;

    private LinearLayout llBack;
    private TextView mCourseText;
    private TextView mFinishBtn;
    private TextView mPicNumText;

    private Camera mCamera;
    private SurfaceHolder mHolder;

    private ImageView mPresentImage;

    private MyHandler mHandler = new MyHandler();

    private boolean mIsTakingphoto = false;

    private final static int MAX_PIC_NUM = 9;

    //这是相片数组
    private ArrayList<Bitmap> mPictures = new ArrayList<Bitmap>();

    private ArrayList<String> mPicPath = new ArrayList<>();

    private final static int MSG_GET_A_PHOTO = 1;
    private final static int MSG_TAKE_PHOTO_FAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_take_photo);

        initViews();

        SharedPreferences sharedPreferences = getSharedPreferences(SpNameConstant.CONFIG,MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isFirstTimeOpenCamera",true)) {
            Toast.makeText(getBaseContext(),"长按相机可以直接从相册中选区图片",Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putBoolean("isFirstTimeOpenCamera",false).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "onResume");
        if (null == mCamera) {
            mCamera = getCamera();
            if(mHolder != null){
                previceCamera(mCamera, mHolder);
            }
        }
    }

    private void releaseCamera(){
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private void initViews() {
        mCameraSf = findViewById(R.id.camera_sf);
        mTakePicBtn = findViewById(R.id.take_photo_btn);
        mReTakePhoto = findViewById(R.id.restart_take_photo);
        mContinueTakePic = findViewById(R.id.continue_take_photo);
        mPresentImage = findViewById(R.id.present_photo);

        mPresentImage.setVisibility(View.GONE);
        mTakePicBtn.setVisibility(View.VISIBLE);
        mReTakePhoto.setVisibility(View.GONE);
        mContinueTakePic.setVisibility(View.GONE);

        llBack = findViewById(R.id.ll_back);
        mCourseText = findViewById(R.id.course_name_text);
        mFinishBtn = findViewById(R.id.finish_text);
        mPicNumText = findViewById(R.id.photo_num_text);

        mPicNumText.setText("0");

        String courseName = getIntent().getStringExtra("course_name");
        if(courseName != null){
            mCourseText.setText(courseName);
        }

        mTakePicBtn.setOnClickListener(this);
        mCameraSf.setOnClickListener(this);
        mReTakePhoto.setOnClickListener(this);
        mContinueTakePic.setOnClickListener(this);

        llBack.setOnClickListener(this);
        mFinishBtn.setOnClickListener(this);

        mHolder = mCameraSf.getHolder();

        mHolder.addCallback(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_sf: {
                if (null != mCamera)
                    mCamera.autoFocus(null);
                break;
            }
            case R.id.take_photo_btn: {
                if(mPictures.size() >= MAX_PIC_NUM){
                    Toast.makeText(this, "一次最多拍摄" + MAX_PIC_NUM + "张照片", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(mIsTakingphoto){
                    break;
                }
                mIsTakingphoto = true;
                startTakephoto();
                break;
            }
            case R.id.continue_take_photo:{
                mPresentImage.setVisibility(View.GONE);
                mTakePicBtn.setVisibility(View.VISIBLE);
                mReTakePhoto.setVisibility(View.GONE);
                mContinueTakePic.setVisibility(View.GONE);

                previceCamera(mCamera, mHolder);
                break;
            }
            case R.id.restart_take_photo:{
                mPresentImage.setVisibility(View.GONE);
                mTakePicBtn.setVisibility(View.VISIBLE);
                mReTakePhoto.setVisibility(View.GONE);
                mContinueTakePic.setVisibility(View.GONE);
                if(!mPictures.isEmpty()){
                    mPictures.remove(mPictures.size() - 1);
                }
                mPicNumText.setText(Integer.toString(mPictures.size()));

                previceCamera(mCamera, mHolder);
                break;
            }
            case R.id.ll_back: {
                finish();
                break;
            }
            case R.id.finish_text: {
                Intent intent = new Intent();
                intent.putExtra("picPath",mPicPath);
                setResult(RESULT_OK,intent);
                finish();
                break;
            }

            default:
                break;
        }
    }

    private void startTakephoto() {

        try {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bm0 = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Matrix m = new Matrix();
                    m.setRotate(90, (float) bm0.getWidth() / 2, (float) bm0.getHeight() / 2);
                    final Bitmap bm = Bitmap.createBitmap(bm0, 0, 0, bm0.getWidth(), bm0.getHeight(), m, true);

                    Message message = new Message();
                    message.what = MSG_GET_A_PHOTO;
                    message.obj = bm;

                    mHandler.sendMessage(message);
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_TAKE_PHOTO_FAIL);
        }

    }

    //保存拍照相片到本地磁盘
    private void saveData(Bitmap bitmap) {
        FileOutputStream fos = null;
        String tempStr = Environment.getExternalStorageDirectory().getAbsolutePath();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        String fileName = dateFormat.format(date) + ".jpg";
        String filePath = tempStr + "/" + fileName;
        mPicPath.add(filePath);
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.i("surfaceCreated", "surfaceCreated");
        previceCamera(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("surfaceChanged", "surfaceChanged");

        mCamera.stopPreview();
        previceCamera(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera) {
            releaseCamera();
        }
    }

    private Camera getCamera() {
        if (null == mCamera) {
            mCamera = Camera.open();
        }
        return mCamera;
    }

    private void previceCamera(Camera camera, SurfaceHolder holder) {
        try {
            //设置最佳预览参数和拍照参数

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;

            Camera.Size bestPreviewSize = getBestSize(screenWidth, screenHeight, mCamera.getParameters().getSupportedPreviewSizes());
            Camera.Size bestPicSize = getBestSize(screenWidth, screenHeight, mCamera.getParameters().getSupportedPictureSizes());

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            parameters.setPictureSize(bestPicSize.width, bestPicSize.height);

            Log.i("previewSize", bestPreviewSize.width+" x "+bestPreviewSize.height);
            Log.i("pictureSize", bestPicSize.width+" x "+ bestPicSize.height);

            parameters.setPictureFormat(ImageFormat.JPEG);

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            mCamera.setParameters(parameters);

            camera.setPreviewDisplay(holder);

            camera.setDisplayOrientation(90);

            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Camera.Size getBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizes){
        if(sizes == null || sizes.size() == 0 || targetWidth <= 0 || targetHeight <= 0){
            return null;
        }

        Log.i("target size:", targetWidth + "x" + targetHeight);
        double targetRatio = ((double)targetHeight / targetWidth);

        double minDiff = Double.MAX_VALUE;
        Camera.Size bestSize = null;

        for(Camera.Size size : sizes){

            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size;
                Log.i("perfect size:", size.width + "x" + size.height);
                break;
            }

            double ratio = (double)size.width / size.height;
            double diff = Math.abs(ratio - targetRatio);
            Log.i("size:", size.width + "x" + size.height + " ratio:" + ratio);
            if(diff < minDiff) {
                bestSize = size;
                minDiff = diff;
            }
        }
        return bestSize;
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_A_PHOTO:{
                    Bitmap pic = (Bitmap) msg.obj;
                    mPictures.add(pic);
                    saveData(pic);
                    mPicNumText.setText(Integer.toString(mPictures.size()));
                    mPresentImage.setImageBitmap(pic);
                    mPresentImage.setVisibility(View.VISIBLE);
                    mTakePicBtn.setVisibility(View.GONE);
                    mReTakePhoto.setVisibility(View.VISIBLE);
                    mContinueTakePic.setVisibility(View.VISIBLE);

                    mIsTakingphoto = false;
                    break;
                }
                case MSG_TAKE_PHOTO_FAIL:{

                    mIsTakingphoto = false;
                    Toast.makeText(TakePhotoActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();

                    break;
                }
                default:
                    break;
            }
        }
    }
}
