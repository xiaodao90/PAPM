package user_manager;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class page_filter implements Filter {
	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
	   this.filterConfig = filterConfig;

	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("<-----------this is filter----------->");
		PrintWriter out = new PrintWriter(response.getWriter());
		try {
			HttpServletResponse resp = (HttpServletResponse) response;
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();
			
			String requestURI = req.getRequestURI().substring(req.getRequestURI().indexOf("/",1),req.getRequestURI().length());
			System.out.println(requestURI);
			/*if (!requestURI.equals("/login.jsp") && !requestURI.equals("/Error_page.jsp") && !requestURI.equals("/index.jsp") && !requestURI.equals("/"))*/
			if (!requestURI.equals("/login.jsp") && !requestURI.equals("/Error_page.jsp") && !requestURI.equals("/")) 
			{
				if (session.getAttribute("user")==null)
				{
					System.out.println("user is null");
					session.setAttribute("error_code", 1);
					resp.sendRedirect("Error_page.jsp");
					
					return ;
				}
				else 
					System.out.println(session.getAttribute("user"));
			}
			
			chain.doFilter (request, response);
		}
		catch (Exception e) 
		{
			System.err.println(e.toString());
		}
	}
	
	public void destroy() {
	   this.filterConfig = null;
	}
}