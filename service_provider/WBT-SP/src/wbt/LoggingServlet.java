package wbt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoggingServlet
 */
@WebServlet("/LoggingServlet")
public class LoggingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static boolean createNewFile = true;
	private static File file;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoggingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
//		file = new File("/mnt/3E9ED9A49ED954CD/GitHub/securicast/service_provider/log_ID.txt");
//		if(file.exists() == false) {
//			file.createNewFile();
//		}
//		PrintWriter out = new PrintWriter(new FileWriter(file, true));
//		out.append(request.getParameterNames().nextElement() + "\n");
//		out.close();
//		System.out.println(request.getParameterNames().nextElement());
		
	}

}
