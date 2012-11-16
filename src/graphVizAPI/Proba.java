package graphVizAPI;
import java.io.File;

public class Proba
{
   public static void main(String[] args)
   {
      Proba p = new Proba();
     //p.start();
     p.start2();
   }

   /**
    * Construct a DOT graph in memory, convert it
    * to image and store the image in the file system.
    */
   public void start()
   {
      GraphViz gv = new GraphViz();
      gv.addln(gv.start_graph());
      gv.addln("node [style=filled];");
      gv.addln("A -> B[color=\"0.650\" \"0.700\" \"0.700\"];");
      gv.addln("A -> C[color=\"#0000ff\",style=filled];");
      gv.addln("A [color=\"0.000 1.000 1.000\"];");
      gv.addln(gv.end_graph());
      System.out.println(gv.getDotSource());
      
      String type = "gif";
//      String type = "dot";
//      String type = "fig";    // open with xfig
//      String type = "pdf";
//      String type = "ps";
//      String type = "svg";    // open with inkscape
//      String type = "png";
//      String type = "plain";
//      File out = new File("/tmp/out." + type);   // Linux
      File out = new File("C:\\temp\\out." + type);    // Windows
      gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
   }
   
   /**
    * Read the DOT source from a file,
    * convert to image and store the image in the file system.
    */
   private void start2()
   {
   //   String dir = "/home/jabba/eclipse2/laszlo.sajat/graphviz-java-api";     // Linux
  //    String input = dir + "/sample/simple.dot";
   String input = "C:/Documents and Settings/吴恺/Workspaces/MyEclipse 10/graphviz-java-api/sample/simple.dot";    // Windows
	   
	   GraphViz gv = new GraphViz();
	   gv.readSource(input);
	   System.out.println(gv.getDotSource());
   		
//	   String type = "gif";
//	   String type = "dot";
//	   String type = "fig";    // open with xfig
//	   String type = "pdf";
//	   String type = "ps";
//	   String type = "svg";    // open with inkscape
	   String type = "png";
//      String type = "plain";
//	   File out = new File("/tmp/simple." + type);   // Linux
	   File out = new File("C:\\temp\\out." + type);   // Windows
	   gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
   }
}
