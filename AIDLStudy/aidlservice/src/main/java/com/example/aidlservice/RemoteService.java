package com.example.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {
    public final String TAG = this.getClass().getSimpleName();
    private ArrayList<Person> persons;

    public RemoteService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        persons = new ArrayList<>();
        return iBinder;
    }



    private IBinder iBinder = new IService.Stub() {
        @Override
        public int add(int num1, int num2) throws RemoteException {
            return num1 + num2;
        }

        @Override
        public List<Person> addPerson(Person p) throws RemoteException {
            persons.add(p);
            return persons;
        }
    };
}
