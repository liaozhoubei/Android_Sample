// IService.aidl
package com.example.studyservice;
import com.example.studyservice.ICallback;

// Declare any non-default types here with import statements

interface IService {
        void start();
        void registerCallback(ICallback cb);
        void unregisterCallback(ICallback cb);
}
