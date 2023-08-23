package com.httpandparse;

import java.io.File;

import com.httpandparse.thread.UploadThread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
/**
 * 像服务端上传文件
 * Erro: 服务器端存在问题，暂时无法解决
 * @author ASUS-H61M
 *
 */
public class UploadActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		TextView tv_upload = (TextView) findViewById(R.id.tv_upload); 
		
		findViewById(R.id.bt_uploadfile).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
							  //http://192.168.2.124:8080/bei/servlet/UploaderServlet
				String url = "http://192.168.2.124:8080/Upload/Upload";
				File file = Environment.getExternalStorageDirectory();
				
				File downloadfile = new File(getFilePath());
				
				
				if (downloadfile.exists()){
					System.out.println("存在这个文件");
				} else {
					System.out.println("不存在Sky");
				}
				String fileName = downloadfile.getAbsolutePath();
				System.out.println(fileName);
				UploadThread thread = new UploadThread(url, fileName);
				thread.start();
			}
		});
	}
	
	private static String getFilePath() {
		// 从下载的URL地址中截取文件名
		String fileName = "3974346_1426551981202_mthumb.jpg";
		StringBuffer sb = new StringBuffer();
		// 获取SD卡根目录的绝对路径
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		sb.append(sdcardPath);
		sb.append(File.separator);
		sb.append("HttpAndParse");
		sb.append(File.separator);
		sb.append("download");

		File dirFile = new File(sb.toString());
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			// 如果文件夹不存在,或者此文件不是一个文件夹,需要创建文件夹
			dirFile.mkdirs();
		}
		return sb.toString() + File.separator + fileName;
	}

}
