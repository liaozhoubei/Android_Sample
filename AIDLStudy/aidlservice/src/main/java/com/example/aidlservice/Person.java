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
        // 从序列号中取出数据，要按照writeToParcel()写入的顺序一样取出
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

    /**
     * 将数据写入序列化中
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 写入时的顺序决定取出数据时的顺序
        dest.writeString(name);
        dest.writeInt(age);
    }


    /**
     * 必须实现的方法，写法基本固定
     */
    public static final Creator<Person> CREATOR = new Creator<Person>() {
        /**
         * 从序列化中取出一个个的字段
         * @param in
         * @return
         */
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
