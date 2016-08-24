package com.towerofcat.servlet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class SetUpJson {

	public static void main(String[] args) {
		
		Result result = new Result();
		result.setResult(1);
		List<Person> list = new ArrayList<Person>();
		result.setPersonData(list);
		
		Person person1 = new Person();
		person1.setName("nate");
		person1.setAge(12);
		person1.setUrl("http://pic.yesky.com/uploadImages/2014/345/36/E8C039MU0180.jpg");
		List<SchoolInfo> schools = new ArrayList<SchoolInfo>();
		SchoolInfo schoolInfo1 = new SchoolInfo();
		schoolInfo1.setSchool_name("清华");
		SchoolInfo schoolInfo2 = new SchoolInfo();
		schoolInfo2.setSchool_name("北大");
		schools.add(schoolInfo1);
		schools.add(schoolInfo2);
		person1.setSchoolInfo(schools);
		list.add(person1);
		
		
		Person person2 = new Person();
		person2.setName("jack");
		person2.setAge(24);
		person2.setUrl("http://pic.yesky.com/uploadImages/2014/345/35/228TSWE5QQU9.jpg");
		List<SchoolInfo> schools1 = new ArrayList<SchoolInfo>();
		SchoolInfo schoolInfo3 = new SchoolInfo();
		schoolInfo3.setSchool_name("人大");
		SchoolInfo schoolInfo4 = new SchoolInfo();
		schoolInfo4.setSchool_name("复旦");
		schools1.add(schoolInfo3);
		schools1.add(schoolInfo4);
		person2.setSchoolInfo(schools1);
		list.add(person2);
		
		
		Gson gson = new Gson();
		
		System.out.println(gson.toJson(result));
		

	}

}
