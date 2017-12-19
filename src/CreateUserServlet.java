import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Exceptions.AlreadyExistsException;
import Exceptions.SessionExpiredException;

/**
 * Servlet implementation class CreateUser
 */
@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateUserServlet() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			HttpSession session = request.getSession(false);
			if (session == null || !session.getAttributeNames().hasMoreElements()) {
				throw new SessionExpiredException();
			}
			Authenticator auth = (Authenticator) session.getAttribute("AUTH");
			auth.loginSessionParameters(request, response);
			String dir = getServletContext().getRealPath("/")+ File.separator;
			AccessController ac = new AccessController(dir);
			List<Capability> caps = ac.getCapabilities(request);
			if (!ac.checkPermission(caps, "webapp", "create_user")) {
				PrintWriter out = response.getWriter();
				out.println("<script type=\"text/javascript\">");
				out.println("alert('User not allowed');");
				out.println("location='home.jsp';");
				out.println("</script>");
			} else
				request.getRequestDispatcher("/createUser.jsp").forward(request, response);
		} catch (Exception e) {
			PrintWriter out = response.getWriter();
			if (e instanceof SessionExpiredException) {
				out.println("<script type=\"text/javascript\">");
				out.println("alert('Session Expired');");
				out.println("location='login.jsp';");
				out.println("</script>");
			}
			if(e instanceof FileNotFoundException){
				out.println("<script type=\"text/javascript\">");
				out.println("alert('Unable to connect to database');");
				out.println("location='login.jsp';");
				out.println("</script>");
			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			HttpSession session = request.getSession(false);
			if (session == null || !session.getAttributeNames().hasMoreElements()) {
				throw new SessionExpiredException();
			}
			Authenticator auth = (Authenticator) session.getAttribute("AUTH");
			String password1 = "";
			String password2 = "";
			password1 = request.getParameter("password1").trim();
			password2 = request.getParameter("password2").trim();
			String name = request.getParameter("username");

			if (!password1.equals(password2) || name.equals("") || password1.equals("") || password2.equals("")) {
				PrintWriter out = response.getWriter();
				out.println("<script type=\"text/javascript\">");
				out.println("alert('User fields are incorrect or blank');");
				out.println("location='createUser.jsp';");
				out.println("</script>");
			} else {
				String dir = getServletContext().getRealPath("/");
				auth.createAccount(name, password1);
				AccessController ac = new AccessController(dir);
				ac.givePermission("webapp", "change_pw", name);
				Logger logger = new Logger(dir);
				logger.authenticated("Create User", session.getAttribute("USER").toString());
				response.sendRedirect("home");
			}
		} catch (Exception e) {
			PrintWriter out = response.getWriter();
			if (e instanceof AlreadyExistsException) {
				out.println("<script type=\"text/javascript\">");
				out.println("alert('User already exists');");
				out.println("location='home.jsp';");
				out.println("</script>");
			}
			if (e instanceof SessionExpiredException) {
				out.println("<script type=\"text/javascript\">");
				out.println("alert('Session Expired');");
				out.println("location='login.jsp';");
				out.println("</script>");
			}
			if(e instanceof FileNotFoundException){
				out.println("<script type=\"text/javascript\">");
				out.println("alert('Unable to connect to database');");
				out.println("location='login.jsp';");
				out.println("</script>");
			}
		}

	}
}
