<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Error</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
    <script src="lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 
  </head>
  
  <body>
    <script type="text/javascript">
    	var error_num = "<%=session.getAttribute("error_code").toString()%>";
    	if (error_num == "1")
	    	$.ligerDialog.error('请先登陆!','错误',function (){
	    		window.location = "login.jsp";
	    	});
	    else if (error_num == "2")
	    	$.ligerDialog.error('用户名或密码错误,请重新登陆!','错误',function (){
	    		window.location = "login.jsp";
	    	});
    </script>
  </body>
</html>
