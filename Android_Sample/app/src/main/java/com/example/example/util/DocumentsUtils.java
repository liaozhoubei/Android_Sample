package com.example.example.util;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentsUtils {

    private static final String TAG = DocumentsUtils.class.getSimpleName();

    public static final int OPEN_DOCUMENT_TREE_CODE = 8000;

    private static List<String> sExtSdCardPaths = new ArrayList<>();

    private DocumentsUtils() {

    }

    public static void cleanCache() {
        sExtSdCardPaths.clear();
    }

    /**
     * Get a list of external SD card paths. (Kitkat or higher.)
     *
     * @return A list of external SD card paths.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        if (sExtSdCardPaths.size() > 0) {
            return sExtSdCardPaths.toArray(new String[0]);
        }
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w(TAG, "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    sExtSdCardPaths.add(path);
                }
            }
        }
        if (sExtSdCardPaths.isEmpty()) sExtSdCardPaths.add("/storage/sdcard1");
        return sExtSdCardPaths.toArray(new String[0]);
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD
     * card. Otherwise,
     * null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(final File file, Context c) {
        return getExtSdCardFolder(file, c) != null;
    }

    /**
     * Get a DocumentFile corresponding to the given file (for writing on ExtSdCard on Android 5).
     * If the file is not
     * existing, it is created.
     *
     * @param file        The file.
     * @param isDirectory flag indicating if the file should be a directory.
     * @return The DocumentFile
     */
    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory,
                                               Context context) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return DocumentFile.fromFile(file);
        }

        String baseFolder = getExtSdCardFolder(file, context);
        boolean originalDirectory = false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            if (!baseFolder.equals(fullPath)) {
                relativePath = fullPath.substring(baseFolder.length() + 1);
            } else {
                originalDirectory = true;
            }
        } catch (IOException e) {
            return null;
        } catch (Exception f) {
            originalDirectory = true;
            //continue
        }
        String as = PreferenceManager.getDefaultSharedPreferences(context).getString(baseFolder,
                null);

        Uri treeUri = null;
        if (as != null) treeUri = Uri.parse(as);
        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        if (originalDirectory) return document;
        String[] parts = relativePath.split("/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public static boolean mkdirs(Context context, File dir) {
        boolean res = dir.mkdirs();
        if (!res) {
            if (DocumentsUtils.isOnExtSdCard(dir, context)) {
                DocumentFile documentFile = DocumentsUtils.getDocumentFile(dir, true, context);
                res = documentFile != null && documentFile.canWrite();
            }
        }
        return res;
    }

    public static boolean delete(Context context, File file) {
        boolean ret = file.delete();

        if (!ret && DocumentsUtils.isOnExtSdCard(file, context)) {
            DocumentFile f = DocumentsUtils.getDocumentFile(file, false, context);
            if (f != null) {
                ret = f.delete();
            }
        }
        return ret;
    }

    public static boolean canWrite(File file) {
        boolean res = file.exists() && file.canWrite();

        if (!res && !file.exists()) {
            try {
                if (!file.isDirectory()) {
                    res = file.createNewFile() && file.delete();
                } else {
                    res = file.mkdirs() && file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static boolean canWrite(Context context, File file) {
        boolean res = canWrite(file);

        if (!res && DocumentsUtils.isOnExtSdCard(file, context)) {
            DocumentFile documentFile = DocumentsUtils.getDocumentFile(file, true, context);
            res = documentFile != null && documentFile.canWrite();
        }
        return res;
    }

    public static boolean renameTo(Context context, File src, File dest) {
        boolean res = src.renameTo(dest);

        if (!res && isOnExtSdCard(dest, context)) {
            DocumentFile srcDoc;
            if (isOnExtSdCard(src, context)) {
                srcDoc = getDocumentFile(src, false, context);
            } else {
                srcDoc = DocumentFile.fromFile(src);
            }
            DocumentFile destDoc = getDocumentFile(dest.getParentFile(), true, context);
            if (srcDoc != null && destDoc != null) {
                try {
                    if (src.getParent().equals(dest.getParent())) {
                        res = srcDoc.renameTo(dest.getName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        res = DocumentsContract.moveDocument(context.getContentResolver(),
                                srcDoc.getUri(),
                                srcDoc.getParentFile().getUri(),
                                destDoc.getUri()) != null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }

    public static InputStream getInputStream(Context context, File destFile) {
        InputStream in = null;
        try {
            if (!canWrite(destFile) && isOnExtSdCard(destFile, context)) {
                DocumentFile file = DocumentsUtils.getDocumentFile(destFile, false, context);
                if (file != null && file.canWrite()) {
                    in = context.getContentResolver().openInputStream(file.getUri());
                }
            } else {
                in = new FileInputStream(destFile);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return in;
    }

    public static OutputStream getOutputStream(Context context, File destFile) {
        OutputStream out = null;
        try {
            if (!canWrite(destFile) && isOnExtSdCard(destFile, context)) {
                DocumentFile file = DocumentsUtils.getDocumentFile(destFile, false, context);
                if (file != null && file.canWrite()) {
                    out = context.getContentResolver().openOutputStream(file.getUri());
                }
            } else {
                out = new FileOutputStream(destFile);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static boolean saveTreeUri(Context context, String rootPath, Uri uri) {
        DocumentFile file = DocumentFile.fromTreeUri(context, uri);
        Log.d(TAG, "saveTreeUri= " + uri);
        if (file != null && file.canWrite()) {
            SharedPreferences perf = PreferenceManager.getDefaultSharedPreferences(context);
            perf.edit().putString(rootPath, uri.toString()).apply();
            return true;
        } else {
            Log.e(TAG, "no write permission: " + rootPath);
        }
        Log.d(TAG, "save_tree_uri=false");
        return false;
    }

    public static String getTreeUri(Context context, String rootPath) {
        SharedPreferences perf = PreferenceManager.getDefaultSharedPreferences(context);
        String treeUri = perf.getString(rootPath, "");
        Log.d(TAG, "treeUri=" + treeUri);
        return treeUri;
    }

    public static boolean checkWritableRootPath(Context context, String rootPath) {
        File root = new File(rootPath);
        if (!root.canWrite()) {
            if (DocumentsUtils.isOnExtSdCard(root, context)) {
                DocumentFile documentFile = DocumentsUtils.getDocumentFile(root, true, context);
                return documentFile == null || !documentFile.canWrite();
            } else {
                SharedPreferences perf = PreferenceManager.getDefaultSharedPreferences(context);
                String documentUri = perf.getString(rootPath, "");
                if (documentUri == null || documentUri.isEmpty()) {
                    return true;
                } else {
                    DocumentFile file = DocumentFile.fromTreeUri(context, Uri.parse(documentUri));
                    return !(file != null && file.canWrite());
                }
            }
        }
        return false;
    }

    public static boolean isExist(DocumentFile documentFile, String fileName) {
        boolean exist = false;
        if (documentFile != null) {
            DocumentFile file = documentFile.findFile(fileName);
            if (file != null) {
                exist = file.exists();
            }
        } else {
            Log.w(TAG, "documentFile null");
        }
        return exist;
    }

    /**
     * 在上一级目录下创建目录
     *
     * @param parentDir 父节点目录 docFile
     * @param dirName   child 目录名
     */
    public static DocumentFile mkdir(DocumentFile parentDir, String dirName) {
        if (parentDir != null && !TextUtils.isEmpty(dirName)) {
            if (!DocumentsUtils.isExist(parentDir, dirName)) {
                return parentDir.createDirectory(dirName);
            } else {
                return parentDir.findFile(dirName);
            }
        }
        Log.d(TAG, "dirName = " + dirName);
        return null;
    }

    /**
     * @param context      context
     * @param uri          uri 默认传null ,从sp中读取
     * @param orgFilePath  被复制的文件路径
     * @param destFileName 复制后的文件名
     * @desc 将orgFilePath文件复制到指定SD卡指定路径/storage/xx-xx/hello/
     */
    public static boolean doCopyFile(Context context, Uri uri, String orgFilePath, String destFileName) {
        // 初始化基础目录，确保录音所在目录存在
        // 获取到根目录的uri
        if (uri == null) {
            String sdPath = StorageUtils.getSDCardDir(context);
            if (TextUtils.isEmpty(sdPath)) {
                return false;
            }
            String uriStr = DocumentsUtils.getTreeUri(context, sdPath);
            if (TextUtils.isEmpty(uriStr)) {
                return false;
            }
            uri = Uri.parse(uriStr);
        }
        DocumentFile destFile = null;
        File inFile = new File(orgFilePath);
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
        if (documentFile != null) {
            DocumentFile[] documentFiles = documentFile.listFiles();
            for (DocumentFile file : documentFiles) {
                Log.i(TAG, "file.getName() = " + file.getName());
                if (file.isDirectory() && file.getName() != null && file.getName().equals("hello")) {
                    destFile = file;
                }
            }
            // 进行文件复制
            if (destFile != null) {
                DocumentFile newFile = destFile.createFile("*/*", destFileName);
                if (newFile != null) {
                    return copyFile(context, inFile, newFile);
                } else {
                    Log.w(TAG, "newFile is null");
                }
            }
        }
        return false;
    }

    /**
     * ref: https://www.jianshu.com/p/2f5d80688ca6
     */
    private static boolean copyFile(Context context, File inFile, DocumentFile documentFile) {
        OutputStream out = null;
        try {
            InputStream in = new FileInputStream(inFile);
            out = context.getContentResolver().openOutputStream(documentFile.getUri());
            byte[] buf = new byte[1024 * 10];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "error = " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}