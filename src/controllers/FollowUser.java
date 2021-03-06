package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import managers.ManageUser;

/**
 * Servlet implementation class FollowUser
 * 
 * Servlet per seguir a un usuari.
 * 
 */
@WebServlet("/FollowUser")
public class FollowUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int uid = (int)session.getAttribute("uid");
		Integer userToFollow = Integer.parseInt(request.getParameter("uid"));
		
		ManageUser userManager = new ManageUser();
		
		try {
			boolean result = false;
			if(!userManager.userIsFollowed(uid, userToFollow)) result = userManager.follow(uid, userToFollow);
			/*Verifiquem si hi ha hagut algun error.*/
			if(result) response.setStatus(HttpServletResponse.SC_ACCEPTED);
			else{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		userManager.finalize();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
