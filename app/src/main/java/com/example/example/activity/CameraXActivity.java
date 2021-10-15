package com.example.example.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.example.R;
import com.example.example.util.CameraHelper;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 *
 * https://github.com/duoshine/CameraX 获取视频流
 */
public class CameraXActivity extends AppCompatActivity {

    private String TAG = "CameraXActivity";
    private File outputDirectory;
    private LocalBroadcastManager broadcastManager;

    private int displayId = -1;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private Preview preview = null;
    private ImageCapture imageCapture = null;
    private ImageAnalysis imageAnalyzer = null;
    private Camera camera = null;
    private ProcessCameraProvider cameraProvider = null;
    private WindowManager windowManager;
    private PreviewView viewFinder;

    private DisplayManager displayManager;

    static String KEY_EVENT_ACTION = "key_event_action";
    static String KEY_EVENT_EXTRA = "key_event_extra";
    private static String FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static String PHOTO_EXTENSION = ".jpg";
    private static double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static double RATIO_16_9_VALUE = 16.0 / 9.0;
    private ArrayList EXTENSION_WHITELIST = new ArrayList<String>();

    /**
     * Milliseconds used for UI animations
     */
    private static long ANIMATION_FAST_MILLIS = 50L;
    private static long ANIMATION_SLOW_MILLIS = 100L;
    /**
     * Blocking camera operations are performed using this executor
     */
    private ExecutorService cameraExecutor;
    private Button cameraCaptureButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);
        viewFinder = findViewById(R.id.view_finder);
        cameraCaptureButton = findViewById(R.id.btn_capture);
        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        EXTENSION_WHITELIST.add("JPG");
    }

    /**
     * Volume down button receiver used to trigger shutter
     */
    private BroadcastReceiver volumeDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
//                    cameraCaptureButton.callOnClick();
                }
            }
        }

    };

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {}

        @Override
        public void onDisplayRemoved(int displayId) {}

        @Override
        public void onDisplayChanged(int displayId) {
            if (CameraXActivity.this.displayId == displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}");
                imageCapture.setTargetRotation(viewFinder.getDisplay().getRotation());
                imageAnalyzer.setTargetRotation(viewFinder.getDisplay().getRotation());

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        broadcastManager = LocalBroadcastManager.getInstance(this);

        // Set up the intent filter that will receive events from our main activity
        IntentFilter filter = new IntentFilter();
        filter.addAction(KEY_EVENT_ACTION);
        broadcastManager.registerReceiver(volumeDownReceiver, filter);

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null);

        //Initialize WindowManager to retrieve display metrics
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // Determine the output directory
        outputDirectory = getOutputDirectory(this);

        // Wait for the views to be properly laid out
        viewFinder.post(new Runnable() {
            @Override
            public void run() {

                // Keep track of the display in which this view is attached
                displayId = viewFinder.getDisplay().getDisplayId();

                // Build UI controls
                updateCameraUi();

                // Set up the camera and its use cases
                setUpCamera();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down our background executor
        cameraExecutor.shutdown();
        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver);
        displayManager.unregisterDisplayListener(displayListener);
    }

    /**
     * Inflate camera controls and update the UI manually upon config changes to avoid removing
     * and re-adding the view finder from the view hierarchy; this provides a seamless rotation
     * transition on devices that support it.
     * <p>
     * NOTE: The flag is supported starting in Android 8 but there still is a small flash on the
     * screen for devices that run Android 9 or below.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Rebind the camera with the updated display metrics
        bindCameraUseCases();

        // Enable or disable switching between cameras
        updateCameraSwitchButton();
    }

    /**
     * Initialize CameraX, and prepare to bind the camera use cases
     */
    private void setUpCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                // CameraProvider
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Select lensFacing depending on the available cameras
                try {
                    if (hasFrontCamera()) {
                        lensFacing = CameraSelector.LENS_FACING_FRONT;
                    }
                    if (hasBackCamera()) {
                        lensFacing = CameraSelector.LENS_FACING_BACK;
                    }
                } catch (CameraInfoUnavailableException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Back and front camera are unavailable");
                }

                // Enable or disable switching between cameras
                updateCameraSwitchButton();

                // Build and bind the camera use cases
                bindCameraUseCases();
            }
        }, ContextCompat.getMainExecutor(this));

    }

    /**
     * Declare and bind preview, capture and analysis use cases
     */
    private void bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        Rect metrics = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        Log.d(TAG, "Screen metrics: " + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);

        int screenAspectRatio = aspectRatio(metrics.width(), metrics.height());
        Log.d(TAG, "Preview aspect ratio: " + screenAspectRatio);

        int rotation = viewFinder.getDisplay().getRotation();

        // CameraProvider
        if (cameraProvider == null) {
            throw new IllegalStateException("Camera initialization failed.");
        }

        // CameraSelector
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        // Preview
        preview = new Preview.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)
                .build();

        // ImageCapture
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build();

        // ImageAnalysis
        imageAnalyzer = new ImageAnalysis.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build();
        // The analyzer can then be assigned to the instance
        imageAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer(new LumaListener() {
            @Override
            public void invoke(Double luma) {
                // Values returned from our analyzer are passed to the attached listener
                // We log image analysis results here - you should do something useful
                // instead!
                Log.d(TAG, "Average luminosity: " + luma);
            }
        }));


        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer);

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
        } catch (Exception exc) {
            Log.e(TAG, "Use case binding failed", exc);
        }
    }

    /**
     * [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
     * [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     * <p>
     * Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     * of preview ratio to one of the provided values.
     *
     * @param width  - preview width
     * @param height - preview height
     * @return suitable aspect ratio
     */
    private int aspectRatio(int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    /**
     * Method used to re-draw the camera UI controls, called every time configuration changes.
     */
    private void updateCameraUi() {

        // In the background, load latest photo taken (if any) for gallery thumbnail
        new Thread(){
            @Override
            public void run() {
                File[] files = outputDirectory.listFiles();
                ArrayList<File> arrayList = new ArrayList<>();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isFile()){
                        String name = file.getName();
                        int index = name.indexOf(".");
                        String substring = name.substring(index + 1, name.length());
                        boolean contains = EXTENSION_WHITELIST.contains(substring.toUpperCase(Locale.ROOT));
                        if (contains){
                            arrayList.add(file);
                        }

                    }
                }
                if (arrayList.size() != 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setGalleryThumbnail(Uri.fromFile(arrayList.get(arrayList.size()-1)));
                        }
                    });
                }
            }
        }.start();
        // Listener for button used to capture photo
        cameraCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a stable reference of the modifiable image capture use case
                if (imageCapture != null) {
                    // Create output file to hold the image
                    File photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION);
                    // Setup image capture metadata
                    ImageCapture.Metadata metadata = new ImageCapture.Metadata();

                    // Mirror image when using the front camera
                    metadata.setReversedHorizontal(lensFacing == CameraSelector.LENS_FACING_FRONT);

                    // Create output options object which contains file + metadata
                    ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                            .setMetadata(metadata)
                            .build();

                    // Setup image capture listener which is triggered after photo has been taken
                    imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                            Uri savedUri;
                            if (output.getSavedUri() != null) {
                                savedUri = output.getSavedUri();
                            } else {
                                savedUri = Uri.fromFile(photoFile);
                            }

                            // We can only change the foreground Drawable using API level 23+ API
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // Update the gallery thumbnail with latest picture taken
                                setGalleryThumbnail(savedUri);
                            }

                            // Implicit broadcasts will be ignored for devices running API level >= 24
                            // so if you only target API level 24+ you can remove this statement
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                sendBroadcast(
                                        new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                                );
                            }

                            // If the folder selected is an external media directory, this is
                            // unnecessary but otherwise other apps will not be able to access our
                            // images unless we scan them using [MediaScannerConnection]
                            if (savedUri.getScheme() .equals("file")){
                                File file = new File(savedUri.getPath());
                                String absolutePath = file.getAbsolutePath();
                                int index = absolutePath.lastIndexOf(".");
                                // 获取保存文件的后缀，如 jpg
                                String extension = absolutePath.substring(index+1, absolutePath.length());
                                MimeTypeMap singleton = MimeTypeMap.getSingleton();
                                // 从文件类型中获取到 mimeType , 如 jpg 的类型是 image/jpeg
                                String mimeType = singleton.getMimeTypeFromExtension(extension);
                                // 刷新媒体存储器
                                MediaScannerConnection.scanFile(CameraXActivity.this,
                                        new String[]{absolutePath}, new String[]{mimeType},
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            @Override
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.d(TAG, "Image capture scanned into media store: " + uri);
                                            }
                                        });
                            }

                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(TAG, "Photo capture failed:" + exception.getMessage(), exception);
                        }
                    });


                    // We can only change the foreground Drawable using API level 23+ API
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        View root = findViewById(R.id.camera_container);
                        // Display flash animation to indicate that photo was captured
                        viewFinder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                root.setForeground(new ColorDrawable(Color.WHITE));
                                root.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.setForeground(null);
                                    }
                                }, ANIMATION_FAST_MILLIS);
                            }
                        }, ANIMATION_SLOW_MILLIS);

                    }
                }
            }
        });


        // Setup for button used to switch cameras
        View cameraSwitchButton = findViewById(R.id.btn_switch_camera);
        cameraSwitchButton.setEnabled(false);
        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                    lensFacing = CameraSelector.LENS_FACING_BACK;
                } else {
                    lensFacing = CameraSelector.LENS_FACING_FRONT;
                }
                ;
                // Re-bind use cases to update selected camera
                bindCameraUseCases();
            }
        });

        View photoViewButton = findViewById(R.id.photoViewButton);
        // Listener for button used to view the most recent photo
        photoViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only navigate when the gallery has photos
                if (outputDirectory.listFiles() != null && outputDirectory.listFiles().length > 0) {

                }
            }
        });


    }

    private void setGalleryThumbnail(Uri uri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Run the operations in the view's thread
                View photoViewButton = findViewById(R.id.photoViewButton);
                // Load thumbnail into circular button using Glide
                Glide.with(photoViewButton)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into((ImageView) photoViewButton);
            }
        });

    }

    /**
     * Enabled or disabled a button to switch cameras depending on the available cameras
     */
    private void updateCameraSwitchButton() {
        Button cameraSwitchButton = findViewById(R.id.btn_switch_camera);
        try {
            cameraSwitchButton.setEnabled(hasBackCamera() && hasFrontCamera());
        } catch (CameraInfoUnavailableException e) {
            e.printStackTrace();
            cameraSwitchButton.setEnabled(false);
        }
    }

    /**
     * Returns true if the device has an available back camera. False otherwise
     */
    private boolean hasBackCamera() throws CameraInfoUnavailableException {
        return cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA);
    }

    /**
     * Returns true if the device has an available front camera. False otherwise
     */
    private boolean hasFrontCamera() throws CameraInfoUnavailableException {
        return cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
    }

    /**
     * Helper function used to create a timestamped file
     */
    private static File createFile(File baseFolder, String format, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return new File(baseFolder, simpleDateFormat.format(System.currentTimeMillis()) + extension);
    }

    /**
     * Use external media if it is available, our app's file directory otherwise
     */
    private static File getOutputDirectory(Context context) {
        Context appContext = context.getApplicationContext();

        File[] externalMediaDirs = null;
        File mediaDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            externalMediaDirs = context.getExternalMediaDirs();
            if (externalMediaDirs != null && externalMediaDirs[0] != null) {
                mediaDir = new File(externalMediaDirs[0], "CameraXPhoto");
                if (!mediaDir.exists()){
                    mediaDir.mkdirs();
                }
            }
        }else {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            mediaDir= new File(externalStoragePublicDirectory, "CameraXPhoto");
        }
        if (mediaDir != null && mediaDir.exists()) {
            return mediaDir;
        } else {
            return appContext.getFilesDir();
        }

    }

    /**
     * Our custom image analysis class.
     *
     * <p>All we need to do is override the function `analyze` with our desired operations. Here,
     * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
     */
    class LuminosityAnalyzer implements ImageAnalysis.Analyzer {
        private int frameRateWindow = 8;
        private ArrayDeque<Long> frameTimestamps = new ArrayDeque<Long>(5);
        private ArrayList<LumaListener> listeners =new ArrayList<LumaListener>();
        private long lastAnalyzedTimestamp = 0L;
        double framesPerSecond = -1.0;

        public LuminosityAnalyzer(LumaListener listener) {
            listeners.add(listener);
        }

        /**
         * Used to add listeners that will be called with each luma computed
         */
        void onFrameAnalyzed(LumaListener listener){
            listeners.add(listener);
        }

        /**
         * Analyzes an image to produce a result.
         *
         * <p>The caller is responsible for ensuring this analysis method can be executed quickly
         * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
         * images will not be acquired and analyzed.
         *
         * <p>The image passed to this method becomes invalid after this method returns. The caller
         * should not store external references to this image, as these references will become
         * invalid.
         *
         * @param image image being analyzed VERY IMPORTANT: Analyzer method implementation must
         * call image.close() on received images when finished using them. Otherwise, new images
         * may not be received or the camera may stall, depending on back pressure setting.
         *
         */
        @Override
        public void analyze(@NonNull ImageProxy image) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close();
                return;
            }

            // Keep track of frames analyzed
            long currentTime = System.currentTimeMillis();
            frameTimestamps.push(currentTime);
            // Compute the FPS using a moving average
            while (frameTimestamps.size() >= frameRateWindow) frameTimestamps.removeLast();
            Object peekFirst = frameTimestamps.peekFirst();
            long timestampFirst = (peekFirst != null) ? (long) peekFirst : currentTime;
            Object peekLast = frameTimestamps.peekLast();
            long timestampLast = (peekLast != null) ? (long) peekLast : currentTime;
            int stampSize = frameTimestamps.size();
            if (stampSize < 1){
                stampSize = 1;
            }
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    stampSize) * 1000.0;

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = (long) frameTimestamps.getFirst();

            ImageProxy.PlaneProxy plane = image.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            Buffer rewind = buffer.rewind();
            int remaining = rewind.remaining();
            byte[] data = new byte[remaining];
            buffer.get(data);

            // Convert the data into an array of pixel values ranging 0-255
            List<Integer> pixels = new ArrayList<Integer>();
            for (int i = 0; i < data.length; i++) {
                pixels.add(data[i] & 0xff);
            }

            // Compute average luminance for the image
            double luma = 0;
            long sum = 0;
            for (int e:pixels) {
                sum+= e;
            }
            luma = sum/((double)pixels.size());
            // Call all listeners with new value
            for (LumaListener lumaListner: listeners) {
                lumaListner.invoke(luma);
            }
            image.close();
        }
    }

    interface LumaListener{
        void invoke(Double luma);
    }
}
