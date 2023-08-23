package com.example.databasecontentprovider;
import android.content.Intent;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.databasecontentprovider.loadinterface.*;

public class MainActivity extends Activity {

	private BookDao bookdao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bookdao = new BookDao(getApplicationContext());
		
		Button addData = (Button) findViewById(R.id.add_data);
		addData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						bookdao.addData();
						super.run();
					}
				}.start();
				
			}
		});
		
		
		Button updateData = (Button) findViewById(R.id.update_data);
		updateData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						bookdao.updateData();
						super.run();
					}
				}.start();
			}
		});
		
		
		Button deleteButton = (Button) findViewById(R.id.delete_data);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						bookdao.delete();
						super.run();
					}
				}.start();
			}
		});
		
		
		Button queryButton = (Button) findViewById(R.id.query_data);
		queryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						bookdao.query();
						super.run();
					}
				}.start();
			}
		});
		
		Button signOutButton = (Button) findViewById(R.id.sign_out);
		signOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
			}
		});
		
	}

}
