<!DOCTYPE unspecified PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" href="static/css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="static/artd/skins/default.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="static/css/btn.css" media="screen"/>

	<link rel="stylesheet" media="screen" type="text/css" href="static/css/jquery.miniColors.css" />

	<script type="text/javascript" src="static/jquery.js"></script>
	<script type="text/javascript" src="static/utils.js"></script>
	<script type="text/javascript" src="static/artd/artDialog.min.js"></script>
	<script type="text/javascript" src="static/jquery.miniColors.js"></script>
	
	<style>
		/*body{background:url('static/images/back.png');}*/
		body{background-color: white;}
		.clear{clear:both;}
		a{text-decoration: none;}
		#header{width:1240px;height:110px;border-bottom:1px solid black;margin:0 auto;}
		#header img{float:left;margin:5px 0px 5px 10px;}
		#header #sel{margin-left:5px;margin-top:10px;height:100px;float: left; }
		#header #sel #upper{display: block; height:35px; background-color:#ddd;padding-left: 8px;padding-top: 8px;font-size: 18px;font-weight:bold;}
		#header #sel #upper p{width:40%;float: left;}		
		#header #sel #upper ul{float:right;right: 20px;clear: right;}
		#header #sel #upper li{display: inline-block;}
		#main{width:1240px;margin:0 auto;margin-top:20px;}
		#list
		{
			position:absolute;
			top:40%;
			left:44%;
		}
		#select
		{
			font-size:15px;
			float:right;
		}
		.button 
		{
			padding: 0 1.5em;
			margin: 0.5em;
			font: bold 1em/2em Arial, Helvetica;
		}
		.color-picker
		{
			width:90px;
		}
	</style>
	
	
	<!-- **********************表格样式 ********************** -->
	<style type="text/css">
		*{font-family:Tahoma, Arial, Helvetica, Sans-serif;}
		table{
			width:350px;
			font:Georgia 11px;
			font-size:12px;
			color:#333333;
			text-align:center;
			border-collapse:collapse;/*细线表格*/
		}
		table td{
			border:1px solid #999;/*细线条颜色*/
			height:22px;
		}
		#table1 
		{
			margin-left:100px;
			float:left;
		}
		#table2 
		{
			margin-right:100px;
			float:right;
		}
		#table1 caption {text-align:left;font-size:20px;font-weight:bold;margin:10px 20px 15px 20px;}
		#table2 caption {text-align:left;font-size:20px;font-weight:bold;margin:10px 20px 15px 20px;}
		tr.t1 td {background-color:#fff;}/* 交替行第一行的背景色 */
		tr.t2 td {background-color:#eee;}/* 交替行第二行的背景色 */
		tr.t3 td {background-color:#ccc;}/* 鼠标经过时的背景色 */
		th,tfoot tr td{font-weight:bold;text-align:center;background:#c5c5c5;}
		th{line-height:30px;height:30px;}
		tfoot tr td{background:#fff;line-height:26px;height:26px;}
		thead{border:1px solid #999;}
		thead tr td{text-align:center;}
		#page{text-align:center;float:right;}
		#page a,#page a:visited{width:60px;height:22px;line-height:22px;border:1px black solid;display:block;float:left;margin:0 3px;background:#c9c9c9;
		text-decoration:none;}
		#page a:hover{background:#c1c1c1;text-decoration:none;}
		.grayr {padding:2px;font-size:11px;background:#fff;float:right;}
		.grayr a {padding:2px 5px;margin:2px;color:#000;text-decoration:none;;border:1px #c0c0c0 solid;}
		.grayr a:hover {color:#000;border:1px orange solid;}
		.grayr a:active {color:#000;background: #99ffff}
		.grayr span.current {padding:2px 5px;font-weight:bold; margin:2px; color: #303030;background:#fff;border:1px orange solid;}
		.grayr span.disabled {padding:2px 5px;margin:2px;color:#797979;background: #c1c1c1;border:1px #c1c1c1 solid;}
	</style>
</head>

<body>
	<div id="main">
			<table id=table1>
				<caption>不展示列表</caption>
				<thead>
					<tr>
						<th><input onclick="if(this.checked==true) { checkAll('check1'); } else { clearAll('check1'); }" type="checkbox" value="0" name="check1" title="全选/取消"/></th>
						<th>函数名</th>
					</tr>
				</thead>
				<tbody id="tab1"></tbody>
			</table>
			<ul id=list>
				<li><button id="add" class="button right_arrow" onclick="dis_func()">进行展示</button></li>
				<li><button id="del" class="button left_arrow" onclick="hide_func()">取消展示</button></li>	
			</ul>
			
			<table id=table2>
				<caption>展示列表</caption>			
				<thead>
					<tr>
						<th><input onclick="if(this.checked==true) { checkAll('check2'); } else { clearAll('check2'); }" type="checkbox" value="0" name="check2" title="全选/取消"/></th>
						<th>函数名</th>
					</tr>
				</thead>
				<tbody id="tab2"></tbody>
			</table>	
		
	</div>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$("#header #sel").css('width',parseInt($("#header").css('width'))-140+"px");//to decide the postion of #sel
			
			$.getJSON ("servlet/Func_dis?req_type=get_dis", function (result){
				$.each(result["dis"],function (i,temp){
					$("#tab2").append ("<tr><td><input type='checkbox' name='check2'/></td>"+ 
										"<td>"+temp+"</td>");
				})
				$.each(result["undis"],function (i,temp){
					$("#tab1").append ("<tr><td><input type='checkbox' name='check1'/></td>"+ 
										"<td>"+temp+"</td>");
				})
			})
		});
	
		function dis_func ()
		{
			var chk_def = document.getElementById('table1').getElementsByTagName('input');
			var table1 = document.getElementById('table1');
			var rows = table1.rows;
			var list = "test=[";
			var flag=0;
	
			for (var i = 1,j = 1; i < chk_def.length; i++,j++)
			{
				if (chk_def[i].type=='checkbox' && chk_def[i].name=='check1' && chk_def[i].checked)
				{
					if (flag==0)
						flag=1;
					else 
						list=list+",";
					list=list+"{func_name:'"+rows[j].cells[1].innerHTML+"',flag:1}";
					$("#tab2").append("<tr><td><input type='checkbox' name='check2'/></td>"+
									"<td>"+rows[j].cells[1].innerHTML+"</tb>");
				}
			}
			list=list+"]";
			
	
			if (flag == 0)
				alert ("请选择要展示的函数");
			else 
			{
				$.post('servlet/Add_dis',list);
				for (var i = 1; i < chk_def.length; i++)
				{
					for (var j = i; j < chk_def.length; j++)
					{
						if (chk_def[j].type=='checkbox' && chk_def[j].name=='check1' && chk_def[j].checked)
						{
							table1.deleteRow(j);
							i=j;
							break;
						}
					}
				}
			}
		}
		
		function hide_func ()
		{
			var chk_def = document.getElementById('table2').getElementsByTagName('input');
			var table2 = document.getElementById('table2')
			var rows = table2.rows;
			var list="test=[";
			var flag=0;
	
			for (var i = 1,j = 1; i < chk_def.length; i++,j++)
			{
				if (chk_def[i].type=='checkbox' && chk_def[i].name=='check2' && chk_def[i].checked)
				{
					if (flag==0)
						flag=1;
					else 
						list=list+",";
			
					list=list+"{func_name:'"+rows[j].cells[1].innerHTML+"',flag:0}";
					$("#tab1").append("<tr><td><input type='checkbox' name='check1'/></td>"+
										"<td>"+rows[j].cells[1].innerHTML+"</tb>");
				}
			}
			list=list+"]";
	
			if (flag == 0)
				alert ("请选择要取消展示的函数！");
			else 
			{
				$.post('servlet/Add_dis',list);
				for (var i = 1; i < chk_def.length; i++)
				{
					for (var j = i; j < chk_def.length; j++)
					{
						if (chk_def[j].type=='checkbox' && chk_def[j].name=='check2' && chk_def[j].checked)
						{
							table2.deleteRow(j);
							i=j-1;
							break;
						}
					}
				}
			}
			
		}
		
		function checkAll(name)
		{
			var el = document.getElementsByTagName('input');
			var len = el.length;
			for(var i=0; i<len; i++)
			{
				if((el[i].type=="checkbox") && (el[i].name==name))
					el[i].checked = true;
			}	
		}
		function clearAll(name)
		{
			var el = document.getElementsByTagName('input');
			var len = el.length;
			for(var i=0; i<len; i++)
			{
				if((el[i].type=="checkbox") && (el[i].name==name))
					el[i].checked = false;
			}
		}
	</script>
</body>
</html>