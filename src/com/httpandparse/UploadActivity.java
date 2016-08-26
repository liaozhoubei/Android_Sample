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
				String url = "http://192.168.2.124:8080/bei/servlet/UploaderServlet";
				File file = Environment.getExternalStorageDirectory();
				File fileAbs = new File(file, "Sky.jpg");
				if (fileAbs.exists()){
					System.out.println("存在这个文件");
				} else {
					System.out.println("不存在Sky");
				}
				String fileName = fileAbs.getAbsolutePath();
				System.out.println(fileName);
				UploadThread thread = new UploadThread(url, fileName);
				thread.start();
			}
		});
	}

}
