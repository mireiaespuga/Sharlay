package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import managers.ManageTweet;
import models.Tweet;
import models.dTmodel;

/**
 * Servlet implementation class GetAllTweets
 * 
 * Servlet per obtenir tots els tweets.
 * 
 */
@WebServlet("/GetAllTweets")
public class GetAllTweets extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllTweets() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		dTmodel dt = new dTmodel();
		List<Tweet> tweets = Collections.emptyList();
		ManageTweet tweetManager = new ManageTweet();
		
		try {
			BeanUtils.populate(dt, request.getParameterMap());
			tweets = tweetManager.getAllTweets(dt.getUid(), dt.getStart(), dt.getEnd());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tweetManager.finalize();

		request.setAttribute("tweets",tweets);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/viewAllTweets.jsp"); 
		dispatcher.forward(request,response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}