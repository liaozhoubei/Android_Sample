package com.example.example.util;

import android.media.MediaCodec;

import android.media.MediaCodecInfo;

import android.media.MediaCodecList;
import android.media.MediaFormat;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;

import java.io.File;

import java.io.FileOutputStream;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;

import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;

/**
 * 硬编码为 H264
 * MediaCodec 不支持 nv21 编码，支持 nv12 编码
 */
public class AvcEncoder {

    private final static String TAG = "MeidaCodec";

    private static int yuvqueuesize = 10;
    //待解码视频缓冲队列，静态成员！
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    public static void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }

    private int TIMEOUT_USEC = 12000;

    private MediaCodec mediaCodec;

    int m_width;

    int m_height;

    int m_framerate;

    public byte[] configbyte;

    public AvcEncoder(int width, int height, int framerate, int bitrate) {

        m_width = width;
        m_height = height;
        m_framerate = framerate;

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 5);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
//            MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodec.getCodecInfo().getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC);
            selectVideoCodec(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            e.printStackTrace();
        }
//配置编码器参数
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//启动编码器
        mediaCodec.start();
//创建保存编码后数据的文件
        createfile();
    }

    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return null if no codec matched
     */
    protected static MediaCodecInfo selectVideoCodec(final String mimeType) {
        Log.v(TAG, "selectVideoCodec:");

        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            // select first codec that match a specific MIME type and color format
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    Log.i(TAG, "codec:" + codecInfo.getName() + ",MIME=" + types[j]);
                    final int format = selectColorFormat(codecInfo, mimeType);
                    if (format > 0) {
                        return codecInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * select color format available on specific codec and we can use.
     *
     * @return 0 if no colorFormat is matched
     */
    protected static final int selectColorFormat(final MediaCodecInfo codecInfo, final String mimeType) {
        Log.i(TAG, "selectColorFormat: ");
        int result = 0;
        final MediaCodecInfo.CodecCapabilities caps;
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            caps = codecInfo.getCapabilitiesForType(mimeType);
        } finally {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
        int colorFormat;
        for (int i = 0; i < caps.colorFormats.length; i++) {
            colorFormat = caps.colorFormats[i];
            if (isRecognizedViewoFormat(colorFormat)) {
                if (result == 0)
                    result = colorFormat;
                break;
            }
        }
        if (result == 0)
            Log.e(TAG, "couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return result;
    }

    /**
     * color formats that we can use in this class
     */
    protected static int[] recognizedFormats;

    static {
        recognizedFormats = new int[]{
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private static final boolean isRecognizedViewoFormat(final int colorFormat) {
        Log.i(TAG, "isRecognizedViewoFormat:colorFormat=" + colorFormat);
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++) {
            if (MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar == colorFormat) {
                Log.i(TAG, "isRecognizedViewoFormat: COLOR_FormatYUV420Planar");
            } else if (MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar == colorFormat) {
                Log.i(TAG, "isRecognizedViewoFormat: COLOR_FormatYUV420SemiPlanar");
            } else if (MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar == colorFormat) {
                Log.i(TAG, "isRecognizedViewoFormat: COLOR_QCOM_FormatYUV420SemiPlanar");
            } else if (MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface == colorFormat) {
                Log.i(TAG, "isRecognizedViewoFormat: COLOR_FormatSurface");
            }
            if (recognizedFormats[i] == colorFormat) {
                return true;
            }
        }
        return false;
    }

    private static final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/test1.h264";

    private BufferedOutputStream outputStream;

    private void createfile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void StopEncoder() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRuning = false;

    public void StopThread() {

        isRuning = false;

        try {
            StopEncoder();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    int count = 0;

    public void StartEncoderThread() {

        Thread EncoderThread = new Thread(new Runnable() {

            @Override

            public void run() {

                isRuning = true;

                byte[] input = null;

                long pts = 0;

                long generateIndex = 0;

                while (isRuning) {

                    //访问MainActivity用来缓冲待解码数据的队列
                    if (YUVQueue.size() > 0) {
                        //从缓冲队列中取出一帧
                        input = (byte[]) YUVQueue.poll();
                        byte[] yuv420sp = new byte[m_width * m_height * 3 / 2];
                        //把待编码的视频帧转换为YUV420格式
                        NV21ToNV12(input, yuv420sp, m_width, m_height);
                        input = yuv420sp;

                    }

                    if (input != null) {

                        try {
                            long startMs = System.currentTimeMillis();
                            //编码器输入缓冲区
                            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                            //编码器输出缓冲区
                            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                pts = computePresentationTime(generateIndex);
                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                                inputBuffer.clear();
                                //把转换后的YUV420格式的视频帧放到编码器输入缓冲区中
                                inputBuffer.put(input);
                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                                generateIndex += 1;
                            }

                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            while (outputBufferIndex >= 0) {
                                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                byte[] outData = new byte[bufferInfo.size];
                                outputBuffer.get(outData);
                                if (bufferInfo.flags == BUFFER_FLAG_CODEC_CONFIG) {
                                    configbyte = new byte[bufferInfo.size];
                                    configbyte = outData;
                                } else if (bufferInfo.flags == BUFFER_FLAG_KEY_FRAME) {
                                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                                    //把编码后的视频帧从编码器输出缓冲区中拷贝出来
                                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                                    outputStream.write(keyframe, 0, keyframe.length);
                                } else {
                                    //写到文件中
                                    outputStream.write(outData, 0, outData.length);
                                }
                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }

                    } else {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }

        });

        EncoderThread.start();

    }

    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {

        if (nv21 == null || nv12 == null) return;

        int framesize = width * height;

        int i = 0, j = 0;

        System.arraycopy(nv21, 0, nv12, 0, framesize);

        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }

        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }

        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }

    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / m_framerate;

    }

}
