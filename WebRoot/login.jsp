<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script src="static/jquery.js" type="text/javascript"></script>
	<script src="static/jquery.flexslider-min.js" type="text/javascript"></script>
	<link href="static/css/login.css" rel="stylesheet" type="text/css" />
	<link href="static/css/flexslider.css" rel="stylesheet" type="text/css"/>
	<script language="JavaScript"> 
		if (window != top) 
			top.location.href = location.href; 
	</script>
	<title>并行程序性能监测系统</title>
	
</head>

<body>
	<div id="login">
		<div id="login_header">
			<h1 class="login_logo">
				<a href="#"><img src="static/images/logo_school_1.png" /></a>
			</h1>
			<div class="login_headerContent">
				<div class="navList">
					<ul>
						<li><a href="mailto:xiaodao90@gmail.com">反馈</a></li>
						<li><a href="doc/dwz-user-guide.pdf" target="_blank">帮助</a></li>
					</ul>
				</div>
				<h2 class="login_title"><img src="static/images/login_title.png" /></h2>
			</div>
		</div>
		
		<div id="login_content">
			<div class="loginForm">
				<form action="servlet/LoginCheck?req_type=login" method="post">
					<p>
						<label>用户名：</label>
						<input type="text" name="username" size="20" class="login_input" />
					</p>
					<div>
						<span><label>密码：</label></span>
						<span><input type="password" name="password" size="20" class="login_input" /></span>
					</div>
					
					<div class="login_bar" style="margin-top:30px;margin-left:80px">
						<input class="sub" type="submit" value=" " />
					</div>
				</form>
			</div>
			
			<div style="width:600px">
				<div class="flexslider">
				<ul class="slides">
					<li>
						<img src="static/images/1.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/2.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/3.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/4.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/5.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/6.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/7.jpg" />
				    </li>
				    <li>
				    	<img src="static/images/8.jpg" />
				    </li>
				</ul>
				</div>
			</div>
			<div class="login_main">
				<div class="login_inner">
					<p>本系统可以让您对程序的运行状况有个直观的认识</p>
					<p>帮助您分析系统瓶颈,明确优化方向</p>
					<p>是程序优化的一大利器</p>
				</div>
			</div>
		</div>
		<div id="login_footer">
			Copyright &copy; 2012 北航系统结构所. All Rights Reserved.
		</div>
	</div>
</body>
<script type="text/javascript" >
	$(document).ready(function(){
		$('.flexslider').flexslider({
		    animation: "slide",
		    smoothHeight:true
		  });
	});
</script>
</html>