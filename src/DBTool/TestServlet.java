package DBTool;


import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class TestServlet extends HttpServlet {
    private DBConnectionManager connMgr;

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
        connMgr = DBConnectionManager.getInstance();
    }

    public void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        Connection con = connMgr.getConnection("mysql");
        if (con == null) {
            out.println("Can't get connection");
            return;
        }
        ResultSet rs = null;
        ResultSetMetaData md = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM mpitrace");
            md = rs.getMetaData();
            out.println("<H1>Employee data</H1>");
            while (rs.next()) {
                out.println("<BR>");
                for (int i = 1; i < md.getColumnCount(); i++) {
                    out.print(rs.getString(i) + ", ");
                }
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace(out);
        }
        connMgr.freeConnection("idb", con);
    }

    public void destroy() {
        connMgr.release();
        super.destroy();
    }
}