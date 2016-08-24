package com.towerofcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class JsonServerServlet
 */
@WebServlet("/JsonServerServlet")
public class JsonServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JsonServerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
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
		out.print(gson.toJson(result));
	}

}
