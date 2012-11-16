package tools;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBConnectionManager {
static private DBConnectionManager instance; 

static private int clients;
private Vector drivers = new Vector();
private PrintWriter log;
private Hashtable pools = new Hashtable();

static synchronized public DBConnectionManager getInstance() {
if (instance == null) {
   instance = new DBConnectionManager();
}
clients++;
return instance;
}


private DBConnectionManager() {
init();
}


public void freeConnection(String name, Connection con) {
DBConnectionPool pool = (DBConnectionPool) pools.get(name);
if (pool != null) {
   pool.freeConnection(con);
}
}


public Connection getConnection(String name) {
DBConnectionPool pool = (DBConnectionPool) pools.get(name);
if (pool != null) {
   return pool.getConnection();
}
return null;
}


public Connection getConnection(String name, long time) {
DBConnectionPool pool = (DBConnectionPool) pools.get(name);
if (pool != null) {
   return pool.getConnection(time);
}
return null;
}


public synchronized void release() {
if (--clients != 0) {
   return;
}

Enumeration allPools = pools.elements();
while (allPools.hasMoreElements()) {
   DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
   pool.release();
}
Enumeration allDrivers = drivers.elements();
while (allDrivers.hasMoreElements()) {
   Driver driver = (Driver) allDrivers.nextElement();
   try {
    DriverManager.deregisterDriver(driver);
    log("Deregister JDBC Driver " + driver.getClass().getName() );
   } catch (SQLException e) {
    log(e, "Cannot deregister this JDBC Driver: " + driver.getClass().getName());
   }
}
}


private void createPools(Properties props) {
Enumeration propNames = props.propertyNames();
while (propNames.hasMoreElements()) {
   String name = (String) propNames.nextElement();
   if (name.endsWith(".url")) {
    String poolName = name.substring(0, name.lastIndexOf("."));
    String url = props.getProperty(poolName + ".url");
    if (url == null) {
     log("Not specify URL for connection Pool: " + poolName );
     continue;
    }
    String user = props.getProperty(poolName + ".user");
    String password = props.getProperty(poolName + ".password");
    String maxconn = props.getProperty(poolName + ".maxconn", "0");
    int max;
    try {
     max = Integer.valueOf(maxconn).intValue();
    } catch (NumberFormatException e) {
     log("Wrong max connection limit " + maxconn + " .connection pool " + poolName);
     max = 0;
    }
    DBConnectionPool pool = new DBConnectionPool(poolName, url,
      user, password, max);
    pools.put(poolName, pool);
    log("Create success Connction Pool: " + poolName);
   }
}
}


private void init() {
InputStream is = getClass().getResourceAsStream("/db.properties");
Properties dbProps = new Properties();
try {
   dbProps.load(is);
} catch (Exception e) {
   System.err.println("Cannot find file. "+"and make sure db.properties have been in in CLASSPATH");
   return;
}
String logFile = dbProps.getProperty("logfile","DBConnectionManager.log");
try {
   log = new PrintWriter(new FileWriter(logFile, true), true);
} catch (IOException e) {
   System.err.println("Cannot Open Log File: " + logFile);
   log = new PrintWriter(System.err);
}
loadDrivers(dbProps);
createPools(dbProps);
}


private void loadDrivers(Properties props) {
String driverClasses = props.getProperty("drivers"); 
StringTokenizer st = new StringTokenizer(driverClasses);
while (st.hasMoreElements()) {
   String driverClassName = st.nextToken().trim(); 
   try {
    Driver driver = (Driver) Class.forName(driverClassName).newInstance();
    DriverManager.registerDriver(driver);
    drivers.addElement(driver);
    log("Register JDBC Driver success: " + driverClassName);
   } catch (Exception e) {
    e.printStackTrace();
    log("Cannot register JDBC Driver: " + driverClassName + ", Error: " + e);
   }
}
}


private void log(String msg) {
log.println(new Date() + ": " + msg);
}


private void log(Throwable e, String msg) {
log.println(new Date() + ": " + msg);
e.printStackTrace(log);
}


class DBConnectionPool {
private int checkedOut;
private Vector freeConnections = new Vector();
private int maxConn;
private String name;
private String password;
private String URL;
private String user;


public DBConnectionPool(String name, String URL, String user,
    String password, int maxConn) {
   this.name = name;
   this.URL = URL;
   this.user = user;
   this.password = password;
   this.maxConn = maxConn;
}


public synchronized void freeConnection(Connection con) {
   freeConnections.addElement(con);
   checkedOut--;
   notifyAll();
}


public synchronized Connection getConnection() {
   Connection con = null;
   if (freeConnections.size() > 0) {
    con = (Connection) freeConnections.firstElement();
    freeConnections.removeElementAt(0);
    try {
     if (con.isClosed()) {
      log("Delete wrong connection from connetion pool: " + name );
      con = getConnection();
     }
    } catch (SQLException e) {
      log("Delete wrong connection from connetion pool: " + name );
     // 递归调用自己,尝试再次获取可用连接
     con = getConnection();
    }
   } else if (maxConn == 0 || checkedOut < maxConn) {
    con = newConnection();
   }
   if (con != null) {
    checkedOut++;
   }
   return con;
}


public synchronized Connection getConnection(long timeout) {
   long startTime = new Date().getTime();
   Connection con;
   while ((con = getConnection()) == null) {
    try {
     wait(timeout);
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
    if ((new Date().getTime() - startTime) >= timeout) {// wait()返回的原因是超时
     return null;
    }
   }
   return con;
}


public synchronized void release() {
   Enumeration allConnections = freeConnections.elements();
   while (allConnections.hasMoreElements()) {
    Connection con = (Connection) allConnections.nextElement();
    try {
     con.close();
     log("Close a connection from connction pool : " + name  );
    } catch (SQLException e) {
     log(e, "Cannot close connection from connction pool :" + name );
     e.printStackTrace();
    }
   }
   freeConnections.removeAllElements();
}


private Connection newConnection() {
   Connection con = null;
   try {
    if (user==null||"".equals(user)) {
     con = DriverManager.getConnection(URL);
    } else {
     con = DriverManager.getConnection(URL, user, password);
    }
    log("Create a new connection in pool: " + name );
   } catch (SQLException e) {
    log(e, "Cannot create new connection using this URL: " + URL);
    return null;
   }
   return con;
}
}


}
