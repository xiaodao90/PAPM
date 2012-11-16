package user_manager;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.fileupload.FileItem;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.fop.svg.PDFTranscoder;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;



/**  
 *<p>Title: HighChartsAction.java</p>
 *<p>Description: 处理HighCHarts导出中文乱码</p>
 *<p>Copyright: Copyright (c) 2012</p>
 *<p>Company: gener-tech</p>
 *@author: 
 *@E-mail: 
 *@create time 2012-7-10 下午02:56:18
 *@version V1.3
 */
public class HighChartsServlet extends HttpServlet {
    
	   private static final long serialVersionUID = 3920224595120519682L;
	   
	    public HighChartsServlet() {
	      super();
	   }      

	   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	      doPost(request,response);
	   }     
	   
	   
	   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   try {
		   
		      request.setCharacterEncoding("utf-8");//注意编码
		      
		      String type = null; 
	          String svg = null; 
	          String filename = null; 
	          DiskFileItemFactory factory = new DiskFileItemFactory(); 
	          ServletFileUpload upload = new ServletFileUpload(factory); 
	          List items = upload.parseRequest(request); 
	          for (Iterator i = items.iterator(); i.hasNext();) {  
	              FileItem fileItem = (FileItem) i.next();  
	              String field = fileItem.getFieldName();  
	              if(field.equals("type")){  
	                        type = fileItem.getString();  
	                        continue;  
	              }else if(field.equals("svg")){  
	                       svg = fileItem.getString();  
	                       continue;  
	              }else if(field.equals("filename")){  
	                       filename = fileItem.getString();  
	                       continue;  
	              } 
	          } 
	          System.out.println(type);
		      
		     /* String type = request.getParameter("type");
		      String svg = request.getParameter("svg");*/
		      ServletOutputStream out = response.getOutputStream();
	
		      if (null != type && null != svg){
		      // This line is necessary due to a bug in the highcharts SVG generator for IE
		      // I'm guessing it wont be needed later.
		      svg = svg.replaceAll(":rect", "rect"); 
		      String ext = "";
		      Transcoder t = null;
		      
		      if (type.equals("image/png")) {
		         ext = "png";
		         t = new PNGTranscoder();
		         
		      } else if (type.equals("image/jpeg")) {
		         ext = "jpg";
		          t = new JPEGTranscoder();
	
		      } else if (type.equals("application/pdf")) {
		         ext = "pdf";
		         t = new PDFTranscoder();
	
		      } else if (type.equals("image/svg+xml")) {
		         ext = "svg";   
		      }
		      
		      response.addHeader("Content-Disposition", "attachment; filename=chart."+ext);
		      response.addHeader("Content-Type", type);
		      
		         if (null != t){
		            TranscoderInput input = new TranscoderInput(new StringReader(svg));
		            TranscoderOutput output = new TranscoderOutput(out);
		            try {
		               t.transcode(input,output);
		            } catch (TranscoderException e){
		               out.print("Problem transcoding stream. See the web logs for more details.");
		               e.printStackTrace();
		            }
		   
		         } else if (ext == "svg"){
		            out.print(svg);
		         } else {
		            out.print("Invalid type: " + type);
		         }
		      } else {
		         response.addHeader("Content-Type", "text/html");
		         out.println("Usage:\n\tParameter [svg]: The DOM Element to be converted.\n\tParameter [type]: The destination MIME type for the elment to be transcoded.");
		      }
		      out.flush();
		      out.close();   
		   }

		   catch (Exception e) {
	           System.err.println(e.toString());
	   }
	   
       }
	}