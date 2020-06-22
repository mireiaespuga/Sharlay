package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import managers.ManageUser;
import models.User;
import models.dTmodel;

/**
 * Servlet implementation class GetFollowers
 */
@WebServlet("/GetFollowers")
public class GetFollowers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFollowers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Hola mundo");
		List<User> users = Collections.emptyList();
		dTmodel dt = new dTmodel();
		System.out.println(request.getParameter("uid"));
		int viewuid = Integer.parseInt(request.getParameter("uid"));
		
		try {
			BeanUtils.populate(dt, request.getParameterMap());
			ManageUser userManager = new ManageUser();
			users = userManager.getUserFollowers(viewuid,dt.getStart(),dt.getEnd());
			userManager.finalize();
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		request.setAttribute("users",users);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/viewFollowers.jsp"); 
		dispatcher.forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}