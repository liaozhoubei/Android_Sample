package com.example.studyservice.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlservice.IPersonService;
import com.example.aidlservice.Person;
import com.example.studyservice.R;

import java.util.ArrayList;

public class AIDLClientActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText num1;
    private EditText num2;
    private EditText result;
    private EditText name;
    private EditText age;
    private TextView textGetPerson;
    private Button btn_1;
    private Button btn_2;

    IPersonService iService;

    /**
     * 创建ServiceConnection与服务进行链接
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = IPersonService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_client);
        initView();
        bindService();

    }

    private void initView() {
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        result = (EditText) findViewById(R.id.result);

        btn_1 = (Button) findViewById(R.id.btn_addNumber);
        btn_2 = (Button) findViewById(R.id.btn_addPerson);

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        textGetPerson = (TextView) findViewById(R.id.text_getPerson);

        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
    }

    /**
     * 与远程服务进行绑定，调用服务端
     */
    private void bindService() {
        Intent intent = new Intent();
        // 传入AIDL的包名，以及调用远程服务的全类名
        intent.setComponent(new ComponentName("com.example.aidlservice", "com.example.aidlservice.RemoteService"));
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addNumber:
                String string = this.num1.getText().toString();
                if (this.num1.getText().toString().length() == 0 || this.num2.getText().toString().length()==0){
                    Toast.makeText(this, "请先填入数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                int num1 = Integer.parseInt(this.num1.getText().toString());
                int num2 = Integer.parseInt(this.num2.getText().toString());


                try {
                    int num = iService.add(num1, num2);
                    result.setText(num + "");
                    Toast.makeText(getApplicationContext(), num + "来到了", Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_addPerson:
                String p1 = name.getText().toString();
                int num3 = Integer.parseInt(age.getText().toString());
                Person person = new Person(p1, num3);
                try {
                    ArrayList<Person> persons = (ArrayList<Person>) iService.addPerson(person);
                    textGetPerson.setText(persons.toString());
                    Toast.makeText(getApplicationContext(), persons.toString(), Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
