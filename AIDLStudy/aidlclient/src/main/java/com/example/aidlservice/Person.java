package com.example.aidlservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bei on 2016/10/9.
 */

public class Person implements Parcelable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    protected Person(Parcel in) {
        this.name = in.readString();
        this.age = in.readInt();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }

//    public static final Creator<Person> CREATOR = new Creator<Person>() {
//        @Override
//        public Person createFromParcel(Parcel source) {
//            return new Person(source);
//        }
//
//        @Override
//        public Person[] newArray(int size) {
//            return new Person[size];
//        }
//    };

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
