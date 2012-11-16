package tools;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;


public class DBManager {

 // --------------------------------------------------------- Instance
 private static Logger logger = Logger.getLogger(DBManager.class);
 // --------------------------------------------------------- Methods


 private Connection con;
 private Statement stmt;
 private PreparedStatement pstmt;
 private ResultSet rs;

 private DBConnectionManager dcm = null;


 @SuppressWarnings("unused")
 private static String _DRIVER = "com.mysql.jdbc.Driver";
 @SuppressWarnings("unused")
 private static String _URL = "jdbc:mysql://localhost;database=jsptest;characterEncoding=gb2312";
 @SuppressWarnings("unused")
 private static String _USER_NA = "root";
 @SuppressWarnings("unused")
 private static String _PASSWORD = "07060121";



 public DBManager() {
 }


 private void getConnection() {
  logger.info("###############open:::::get connection by default config file");
  dcm = DBConnectionManager.getInstance();
  con = dcm.getConnection("mysql");
  try {
   con.setAutoCommit(false);
  } catch (SQLException e) {

   e.printStackTrace();
  }
 }


 public void getConnection(String driver, String url, String user_na,
   String password) throws Exception {
  try {
   logger.info("###############open:::::get connection by specific config file");
   Class.forName(driver);
   con = DriverManager.getConnection(url, user_na, password);
  } catch (ClassNotFoundException ex) {
   logger
     .info("###############Error[com.hospital.dao.tools.DBManager^^^Method:getConnection^^^Line:61]Cannot find Driver Class: "
       + driver);
   throw ex;
  } catch (SQLException ex) {
   logger
     .info("###############Error[com.hospital.dao.tools.DBManager^^^Method:getConnection^^^Line:61]Load Class: "
       + driver + " appear SQLException");
   throw ex;
  }
 }


 public boolean executeUpdate(String strSql) throws SQLException {
  getConnection();
  // getConnection(_DRIVER,_URL,_USER_NA,_PASSWORD);
  boolean flag = false;
  stmt = con.createStatement();
  logger.info("###############::SQL operation (UPDATE no arguments:" + strSql);
  try {
   if (0 < stmt.executeUpdate(strSql)) {
    close_DB_Object();
    flag = true;
    con.commit();
   }
  } catch (SQLException ex) {
   logger
     .info("###############Error DBManager Line81::Execute SQL operation (UPDATE no arguments):"
       + strSql + "Failure!");
   flag = false;
   con.rollback();
   throw ex;
  }
  return flag;

 }


 public boolean executeUpdate(String strSql, HashMap<Integer, Object> prams)
   throws SQLException, ClassNotFoundException {
  getConnection();
  // getConnection(_DRIVER,_URL,_USER_NA,_PASSWORD);
  boolean flag = false;
  try {
   pstmt = con.prepareStatement(strSql);
   setParamet(pstmt, prams);
   logger.info("###############::UPDATE has arguments: " + strSql);

   if (0 < pstmt.executeUpdate()) {
    close_DB_Object();
    flag = true;
    con.commit();
   }
  } catch (SQLException ex) {
   logger
     .info("###############Error DBManager Line106::Execute SQL operation (UPDATE no arguments):"
       + strSql + "Failure!");
   flag = false;
   con.rollback();
   throw ex;
  } catch (ClassNotFoundException ex) {
   logger
     .info("###############Error DBManager Line152::Execute SQL operation (UPDATE no arguments) :"
       + strSql + "Failue! Wrong Type of arguments");
   con.rollback();
   throw ex;
  }
  return flag;

 }


 public ArrayList<HashMap<Object, Object>> executeSql(String strSql)
   throws Exception {
  getConnection();
  // getConnection(_DRIVER,_URL,_USER_NA,_PASSWORD);
  stmt = con.createStatement();
  logger.info("###############::Execute SQL operation(Query):" + strSql);
  rs = stmt.executeQuery(strSql);
  con.commit();
  if (null != rs) {
   return convertResultSetToArrayList(rs);
  }
  close_DB_Object();
  return null;
 }


 public ArrayList<HashMap<Object, Object>> executeSql(String strSql,
   HashMap<Integer, Object> prams) throws Exception {
  getConnection();
  // getConnection(_DRIVER,_URL,_USER_NA,_PASSWORD);
  pstmt = con.prepareStatement(strSql);
  setParamet(pstmt, prams);
  logger.info("###############::Execute SQL operation(Query):" + strSql);
  rs = pstmt.executeQuery();
  con.commit();
  if (null != rs) {
   return convertResultSetToArrayList(rs);
  }
  return null;
 }


 public ArrayList<HashMap<Object, Object>> executeProcedureQuery(
   String procName) throws Exception {
  getConnection();
  String callStr = "{call " + procName + "}";
  CallableStatement cs = con.prepareCall(callStr);
  logger.info("###############::Execute Storage Procedure(Query):" + procName);
  rs = cs.executeQuery();
  con.commit();
  cs.close();
  close_DB_Object();
  return convertResultSetToArrayList(rs);
 }


 public ArrayList<HashMap<Object, Object>> executeProcedureQuery(
   String procName, Object[] parameters) throws Exception {
  int parameterPoint = 0;
  // 获取存储过程信息列表集合
  ArrayList<HashMap<Object, Object>> procedureInfo = getProcedureInfo(procName);
  // 获取存储过程的完全名�?
  String procedureCallName = getProcedureCallName(procName,parameters.length);
  // 获取连接对象
  getConnection();

  CallableStatement cs = con.prepareCall(procedureCallName);

  int index = 0;

  Iterator<HashMap<Object, Object>> iter = procedureInfo.iterator();

  while (iter.hasNext()) {
   HashMap<Object, Object> hm = iter.next();

   parameterPoint++;

   if (hm.get("WAY").equals("0")) {

    cs.setObject(parameterPoint, parameters[index]);

    index++;
   }
  }

  procedureInfo = null;
  logger.info("###############::Execute Storage Procedure(Query)::::" + procedureCallName);
  rs = cs.executeQuery();
  con.commit();
  procedureInfo = convertResultSetToArrayList(rs);
  cs.close();
  close_DB_Object();
  return procedureInfo;

 }


 public Object[] executeProcedureUpdate(String procName, Object[] parameters)
   throws Exception {
  logger.info("------------------------------------------------------------------------------------------------------");
  logger.info(" Run --> executeProcedureUpdate ##############   Executing Storage Procedure: " + procName +"   ##############");
  CallableStatement cs = null;
  Object []returnVal = null;
  try {
  // 获取 存储过程 调用全名
  String fullPCallName = getProcedureCallName(procName,parameters.length);
  logger.info(" Run --> executeProcedureUpdate #   Storage Procedure: " + fullPCallName +"   #");
  //获取存储过程参数信息
  ArrayList<HashMap<Object, Object>> p_Call_Info_List = getProcedureInfo(procName);
  //获取连接
  getConnection();
  //创建 存储过程 执行对象
  cs = con.prepareCall(fullPCallName);
  //数组下标
  int index = 1;
  //输出参数下标 纪录
        ArrayList<Integer> outPutIndexList = new ArrayList<Integer>();
        logger.info(" Run --> executeProcedureUpdate #  Number of arguments " + parameters.length +"   #");
  for(HashMap<Object,Object> tempHash:p_Call_Info_List)
  {
   if("0".equals(tempHash.get("WAY")))
      {
    //设置输入参数
    cs.setObject(index, parameters[index-1]);
    logger.info(" Run --> executeProcedureUpdate #    Input: Id:" + index +" Value: "+parameters[index-1]+" Type: "+parameters[index-1].getClass()+"   #");
   }
   else
   {
    //注册输出参数
    cs.registerOutParameter(index, getDataType(tempHash.get("TYPENAME").toString()));
    //纪录输出参数的下�?
    outPutIndexList.add(index);
    logger.info(" Run --> executeProcedureUpdate #    OutPut: Id:" + index +" Value "+parameters[index-1]+" Type: "+parameters[index-1].getClass()+"   #");
   }
   index++;
  }
  logger.info(" Run --> executeProcedureUpdate #   Finish setting arguments Executing ... :   #");
  

  if(!cs.execute())
  {
   returnVal = new Object[outPutIndexList.size()];
   logger.info(" Run --> executeProcedureUpdate #  Executing Success :   #");
  
   for(int i = 0 ;i<outPutIndexList.size();i++)
   {
    returnVal[i] = cs.getObject(outPutIndexList.get(i));
    logger.info(" Run --> executeProcedureUpdate #   return "+(i+1)+" "+returnVal[i]+"   #");
   }
   con.commit();
  }
  } catch (Exception e) {
   logger.info(" Run --> executeProcedureUpdate #   Executing Failure! Transaction rollback... :   #");
   con.rollback();
   throw e;
  } 
  logger.info("------------------------------------------------------------------------------------------------------");
  return returnVal;
 }


 public void close_DB_Object() {
  logger.info("###############close:::::Closing connection statement record objects");
  if (null != rs) {
   try {
    rs.close();
   } catch (SQLException ex) {
    rs = null;
   }
  }
  if (null != stmt) {
   try {
    stmt.close();
   } catch (SQLException ex) {
    stmt = null;
   }
  }
  if (null != pstmt) {
   try {
    pstmt.close();
   } catch (SQLException ex) {
    pstmt = null;
   }
  }
  if (con != null) {
   dcm.freeConnection("mysql", con);
  }
 }



 private PreparedStatement setParamet(PreparedStatement p_stmt,
   HashMap<Integer, Object> pramets) throws ClassNotFoundException,
   SQLException {

  if (null != pramets) {

   if (0 <= pramets.size()) {
    for (int i = 1; i <= pramets.size(); i++) {
     try {
 
      if (pramets.get(i).getClass() == Class
        .forName("java.lang.String")) {
       p_stmt.setString(i, pramets.get(i).toString());
      }

      if (pramets.get(i).getClass() == Class
        .forName("java.sql.Date")) {
       p_stmt.setDate(i, java.sql.Date.valueOf(pramets
         .get(i).toString()));
      }

      if (pramets.get(i).getClass() == Class
        .forName("java.lang.Boolean")) {
       p_stmt.setBoolean(i, (Boolean) (pramets.get(i)));
      }

      if (pramets.get(i).getClass() == Class
        .forName("java.lang.Integer")) {
       p_stmt.setInt(i, (Integer) pramets.get(i));
      }

      if (pramets.get(i).getClass() == Class
        .forName("java.lang.Float")) {
       p_stmt.setFloat(i, (Float) pramets.get(i));
      }

      if (pramets.get(i).getClass() == Class
        .forName("java.lang.Double")) {
       p_stmt.setDouble(i, (Double) pramets.get(i));
      }

     } catch (ClassNotFoundException ex) {
      throw ex;
     } catch (SQLException ex) {
      throw ex;
     }
    }
   }
  }
  return p_stmt;
 }


 private ArrayList<HashMap<Object, Object>> convertResultSetToArrayList(
   ResultSet rs) throws Exception {
  logger.info("###############::Transform Record Set to ArrayList");

  ResultSetMetaData rsmd = rs.getMetaData();

  ArrayList<HashMap<Object, Object>> tempList = new ArrayList<HashMap<Object, Object>>();
  HashMap<Object, Object> tempHash = null;

  while (rs.next()) {

   tempHash = new HashMap<Object, Object>();
   for (int i = 0; i < rsmd.getColumnCount(); i++) {
 
    tempHash.put(rsmd.getColumnName(i + 1).toUpperCase(), rs
      .getString(rsmd.getColumnName(i + 1)));
   }

   tempList.add(tempHash);
  }
  close_DB_Object();
  return tempList;
 }


 private ArrayList<HashMap<Object, Object>> getProcedureInfo(String procName)
   throws Exception {
  return this.executeSql("select Syscolumns.isoutparam as Way,systypes.name as TypeName from sysobjects,syscolumns,systypes where systypes.xtype=syscolumns.xtype and syscolumns.id=sysobjects.id and sysobjects.name='"
    + procName + "' order by Syscolumns.isoutparam");
 }


 @SuppressWarnings("unused")
 private int getParametersCount(String procName) throws Exception {
  int returnVal = 0;
  for (HashMap<Object, Object> tempHas : this
    .executeSql("select count(*) as RowsCount from sysobjects,syscolumns,systypes where systypes.xtype=syscolumns.xtype and syscolumns.id=sysobjects.id and sysobjects.name='"
      + procName + "'")) {
   returnVal = Integer.parseInt(tempHas.get("ROWSCOUNT").toString());
  }
  return returnVal;
 }


 private String getProcedureCallName(String procName, int prametCount)
   throws Exception {
  String procedureCallName = "{call " + procName;
  for (int i = 0; i < prametCount; i++) {
   if (0 == i) {
    procedureCallName = procedureCallName + "(?";
   }
   if (0 != i) {
    procedureCallName = procedureCallName + ",?";
   }
  }
  procedureCallName = procedureCallName + ")}";
  return procedureCallName;
 }


 private int getDataType(String typeName) {
  if (typeName.equals("varchar"))
   return Types.VARCHAR;
  if (typeName.equals("int"))
   return Types.INTEGER;
  if (typeName.equals("bit"))
   return Types.BIT;
  if (typeName.equals("float"))
   return Types.FLOAT;
  return 0;
 }


 @SuppressWarnings("static-access")
 public void set_DRIVER(String _DRIVER) {
  this._DRIVER = _DRIVER;
 }


 @SuppressWarnings("static-access")
 public void set_PASSWORD(String _PASSWORD) {
  this._PASSWORD = _PASSWORD;
 }

 
 @SuppressWarnings("static-access")
 public void set_URL(String _URL) {
  this._URL = _URL;
 }

 
 @SuppressWarnings("static-access")
 public void set_USER_NA(String _USER_NA) {
  this._USER_NA = _USER_NA;
 }

}
