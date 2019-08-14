package com.example.example;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.example.example.util.BitmapUtils;
import com.example.example.util.CameraHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera.Size previewSize;
    private Camera.Parameters mCameraParameters;
    private int mSensorRotation = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = findViewById(R.id.sv_prew);
        initListener();

    }

    @Override
    protected void onResume() {
        if (!CameraHelper.checkCameraHardware(this)) {
            Toast.makeText(this, "相机不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean open = safeCameraOpen(mCameraId);
        if (!open){
            Toast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
        }
        mHolder = surfaceView.getHolder();
        // Create an instance of Camera
        mHolder.addCallback(callback);
        mSensorManager.registerListener(sensorEventListener, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            mCamera = CameraHelper.getCameraInstance(id);
            qOpened = (mCamera != null);
            int displayDegrees = CameraHelper.calculateCameraPreviewOrientation(this, id);
            if (mCamera != null)
                mCamera.setDisplayOrientation(displayDegrees);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void initListener() {
        mSensorManager = (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 加速度

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("CameraActivity", "X坐标：" + event.getX()+",Y坐标："+event.getY());
                cameraFocus(event);
                return false;
            }
        });
    }

    /**
     * 设置相机手动对焦
     *{@link  }
     * @see <a href="https://www.jianshu.com/p/d9b7bdb1e574">Android自定义相机Camera实现手动对焦</a>
     * @param event
     */
    private void cameraFocus(MotionEvent event) {
        int areaX = (int) (event.getX() / surfaceView.getWidth() * 2000) - 1000; // 获取映射区域的X坐标
        int areaY = (int) (event.getY() / surfaceView.getWidth() * 2000) - 1000; // 获取映射区域的Y坐标
        // 创建Rect区域
        Rect focusArea = new Rect();
        focusArea.left = Math.max(areaX - 100, -1000); // 取最大或最小值，避免范围溢出屏幕坐标
        focusArea.top = Math.max(areaY - 100, -1000);
        focusArea.right = Math.min(areaX + 100, 1000);
        focusArea.bottom = Math.min(areaY + 100, 1000);
        // 创建Camera.Area
        Camera.Area cameraArea = new Camera.Area(focusArea, 1000);
        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        if (mCameraParameters.getMaxNumMeteringAreas() > 0) {
            meteringAreas.add(cameraArea);
            focusAreas.add(cameraArea);
        }
        mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置对焦模式
        mCameraParameters.setFocusAreas(focusAreas); // 设置对焦区域
        mCameraParameters.setMeteringAreas(meteringAreas); // 设置测光区域
        try {
            mCamera.cancelAutoFocus(); // 每次对焦前，需要先取消对焦
            mCamera.setParameters(mCameraParameters); // 设置相机参数
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            }); // 开启对焦
        } catch (Exception e) {
        }
    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            startCameraPreview(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            if (mCamera == null) {
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void startCameraPreview(SurfaceHolder holder) {
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
            List<Camera.Size> pictureSizes = mCameraParameters.getSupportedPictureSizes();
//
            try {
                Collections.sort(pictureSizes, comparator);
                Log.e(TAG, "surfaceCreated: after sort width:" + pictureSizes.get(0).width + "   height:" + pictureSizes.get(0).height);
                // 设置拍照时的图片大小，若不设置，拍照会返回最小的尺寸
                mCameraParameters.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);
                // 部分手机设置预览参数会崩溃
                //  List<Camera.Size> previewSizes = mCameraParameters.getSupportedPreviewSizes();
                //  Collections.sort(previewSizes, comparator);
                //  parameters.setPreviewSize(pictureSizes.get(0).width, pictureSizes.get(0).height);
                previewSize = mCameraParameters.getPreviewSize();
                requestLayout();
                mCamera.setParameters(mCameraParameters);
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }

    /**
     * 重新设置 surfaceView 的宽高，避免预览变形
     */
    private void requestLayout() {
        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                int width = surfaceView.getWidth();
//                int height = surfaceView.getHeight();
                Display defaultDisplay = getWindowManager().getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getSize(point);
                int screenWidth = point.x;
                int surfaceWidth = screenWidth / 2;
                // 根据实际的预览尺寸，以屏幕宽度的一半为基准，计算 surfaceView 高度，避免画面变形
                int surfaceheight = 0;
                int originDegrees = CameraHelper.calculateCameraPreviewOrientation(CameraActivity.this, mCameraId);
                if (originDegrees == 90 || originDegrees == 270){
                    surfaceheight = surfaceWidth * previewSize.width / previewSize.height;
                }else {
                    surfaceheight = surfaceWidth * previewSize.height / previewSize.width;
                }

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                layoutParams.width = surfaceWidth;
                layoutParams.height = surfaceheight;
                surfaceView.setLayoutParams(layoutParams);
                surfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private Comparator comparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            if (o1.width > o2.width) {
                return -1;
            } else if (o1.width == o2.width) {
                if (o1.height == o2.height) {
                    return 0;
                }
                return o1.height > o2.height ? -1 : 1;
            } else {
                return 1;
            }
        }
    };

    /**
     * 调用拍照要注意旋转拍照的角度
     * @see  <a href="https://blog.csdn.net/u010126792/article/details/86706199">Android Camera预览角度和拍照保存图片角度学习</a>
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //获取图片时
            Bitmap bitmap = BitmapUtils.compressImage(data, 720, 1080);
            int rotation = (CameraHelper.calculateCameraPreviewOrientation(CameraActivity.this, mCameraId)+ mSensorRotation) % 360 ;
            Matrix matrix = new Matrix();
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                //如果是后置摄像头因为没有镜面效果直接旋转特定角度
                matrix.setRotate(rotation);
            } else {
                //如果是前置摄像头需要做镜面操作，然后对图片做镜面postScale(-1, 1)
                //因为镜面效果需要360-rotation，才是前置摄像头真正的旋转角度
                rotation = (360 - rotation) % 360;
                matrix.setRotate(rotation);
                matrix.postScale(-1, 1);
            }
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] photoData = stream.toByteArray();
            createFileWithByte(photoData);//保存照片
            showImageDialog(result);

            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //手机移动一段时间后静止，然后静止一段时间后进行对焦
            // 读取加速度传感器数值，values数组0,1,2分别对应x,y,z轴的加速度
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                int x = (int) event.values[0];
                int y = (int) event.values[1];
                int z = (int) event.values[2];

            }

            mSensorRotation = calculateSensorRotation(event.values[0],event.values[1]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }
    };

    public  int calculateSensorRotation(float x, float y) {
        //x是values[0]的值，X轴方向加速度，从左侧向右侧移动，values[0]为负值；从右向左移动，values[0]为正值
        //y是values[1]的值，Y轴方向加速度，从上到下移动，values[1]为负值；从下往上移动，values[1]为正值
        //不考虑Z轴上的数据，
        if (Math.abs(x) > 6 && Math.abs(y) < 4) {
            if (x > 6) {
                return 270;
            } else {
                return 90;
            }
        } else if (Math.abs(y) > 6 && Math.abs(x) < 4) {
            if (y > 6) {
                return 0;
            } else {
                return 180;
            }
        }

        return -1;
    }




    /**
     * 根据byte数组生成文件
     *
     * @param data 生成文件用到的byte数组
     */
    private void createFileWithByte(byte[] data) {
        // TODO Auto-generated method stub
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File pictureFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }




    public void tackPicFromCamera(View view) {
        mCamera.takePicture(null, null, mPicture);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPreviewAndFreeCamera();
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    public void changeCamera(View view) {
        stopPreviewAndFreeCamera();
        mCameraId = mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT
                ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
        safeCameraOpen(mCameraId);
        startCameraPreview(mHolder);
    }

    //初始化并弹出对话框方法
    private void showImageDialog(Bitmap bitmap) {
        View view = LayoutInflater.from(this).inflate(R.layout.image_dialog, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        ImageView imgView = view.findViewById(R.id.iv_photo);
        imgView.setImageBitmap(bitmap);
        Button btn_cancel_high_opion = view.findViewById(R.id.btn_cancel_high_opion);
        Button btn_agree_high_opion = view.findViewById(R.id.btn_agree_high_opion);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((getResources().getDisplayMetrics().widthPixels / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(sensorEventListener,mSensor);
    }
}