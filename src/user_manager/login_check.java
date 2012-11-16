package user_manager;

import java.io.*;
import java.sql.*;

import javax.jms.Session;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DBTool.*;

public class login_check extends HttpServlet{

	//init the servlet
	public void init (ServletConfig config) throws ServletException {
		super.init(config);
		
	}
	//login check
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		try {
			DBManager dbm = new DBManager ();
			String name = request.getParameter("username");
			String passwd = request.getParameter("password");
			String req_type = request.getParameter("req_type");
			System.out.println(req_type);
			
			if (req_type.equals("login"))
			{
				String sql  = "select * from users where name='"+name+"' and password='"+passwd+"'";
				ArrayList res = dbm.executeSql(sql);
				
				if (res.size() > 0)
				{
					HttpSession session = request.getSession();
					session.setAttribute("user", name);
					//session.setAttribute("exp_name", "mpitrace");
					session.setAttribute("exp_name", "NULL");
					session.setAttribute("error_code", null);
					session.setAttribute("proc", 1);
					session.setAttribute("papi", 1);
					session.setAttribute("comm", 1);
					session.setAttribute("profile", 1);
					session.setAttribute("tree", 1);

					response.sendRedirect(request.getContextPath()+"/index.jsp");				//获得index.jsp的路径
				}
				else {
					HttpSession session = request.getSession();
					session.setAttribute("error_code", 2);
					response.sendRedirect(request.getContextPath()+"/Error_page.jsp");
				}
			}
			else if (req_type.equals("logout"))													//set sessions to null
			{
				HttpSession session = request.getSession();
				session.setAttribute("user", null);
				session.setAttribute("exp_name", null);
				session.setAttribute("proc", null);
				session.setAttribute("papi", null);
				session.setAttribute("comm", null);
				response.sendRedirect(request.getContextPath()+"/login.jsp");
			}
		}
		catch (Exception e) {
			System.err.println(e.toString());
			}	
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		try {
			doPost(request, response);
		}
		catch (Exception e) {
			System.err.println(e.toString());
		}	
	}
	
}
