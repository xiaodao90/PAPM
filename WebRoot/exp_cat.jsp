<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link rel="stylesheet" href="static/css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="static/artd/skins/default.css" type="text/css" media="screen" />
	<link rel="stylesheet" media="screen" type="text/css" href="static/css/jquery.miniColors.css" />
	<link rel="stylesheet" href="static/css/btn.css" media="screen"/>
	
	<script type="text/javascript" src="static/jquery.js"></script>
	<script type="text/javascript" src="static/utils.js"></script>
	<script type="text/javascript" src="static/artd/artDialog.min.js"></script>
	<script type="text/javascript" src="static/jquery.miniColors.js"></script>
	<script type="text/javascript" src="static/jquery.leanModal.min.js"></script>
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
		#lean_overlay {
		    position: fixed;
		    z-index:100;
		    top: 0px;
		    left: 0px;
		    height:100%;
		    width:100%;
		    background: #000;
		    display: none;
		}
	</style>
	<!-- table style -->
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
			<caption>未分类表</caption>
			<thead>
				<tr>
					<th><input onclick="if(this.checked==true) { checkAll('check1'); } else { clearAll('check1'); }" type="checkbox" value="0" name="check1" title="全选/取消"/></th>
					<th>函数名</th><th>颜色</th>
				</tr>
			</thead>
			<tbody id="tab1"></tbody>
		</table>
		<ul id=list>
			<li><button id="add" class="button right_arrow" onclick="add_func()">进行分类</button></li>
			<li><button id="del" class="button left_arrow" onclick="remove_func()">取消分类</button></li>
			<li><button id="new" class="button add" onclick="add_cat()">添加分类</button></li>	
		</ul>
		
		<table id=table2>
			<caption>已分类表<select id=select onchange="setChange(this.value)">
								<option value="0">全部</option>
							</select>
			</caption>			
			<thead>
				<tr>
					<th><input onclick="if(this.checked==true) { checkAll('check2'); } else { clearAll('check2'); }" type="checkbox" value="0" name="check2" title="全选/取消"/></th>
					<th>函数名</th>
					<th>颜色</th>
				</tr>
			</thead>
			<tbody id="tab2"></tbody>
		</table>
		
	</div>

	
	<script type="text/javascript">
		$(document).ready(function(){
			$.getJSON("servlet/Get_cat?req_type=get_undef", function (result){
				$.each(result,function(Index,temp){
					$("#tab1").append("<tr id=tl><td><input type='checkbox' value='a' name='check1'/></td>"+
										"<td>"+temp["func_name"]+"</tb>"+
										"<td id='"+temp["color"]+"'><input type='text' class='color-picker' style='background-color:"+temp["color"]+"' old_color='' id='"+temp["func_name"]+"' value='"+temp["color"]+"'/></td></tr>");
				});
				
			});
			
			$.getJSON("servlet/Get_cat?req_type=get_classify", function (result){
				$.each(result,function(i){
					$("#select").append("<option value="+i+">"+result[i]+"</option>"); 	
				});	
			});
			
			$.getJSON("servlet/Get_cat?req_type=get_def", function (result){
				$.each(result,function(Index,temp){
					$("#tab2").append("<tr id="+temp["id"]+"><td><input type='checkbox' value='"+temp["id"]+"' name='check2'/></td>"+
										"<td>"+temp["func_name"]+"</tb>"+
										"<td id='"+temp["color"]+"'><input type='text' class='color-picker' style='background-color:"+temp["color"]+"' old_color='' id='"+temp["func_name"]+"' value='"+temp["color"]+"'/></td></tr>");
				 	
				});
				$(".color-picker").miniColors({
							letterCase: 'uppercase',
							change: function(hex, rgb) {
							
							},
							open: function(hex, rgb) {
								this.old_color=hex;
					
							},
							close: function(hex, rgb) {
								if (this.old_color!=hex)
								{
									$.get("servlet/Modify_cat",{req_type:"change_color",func_name:this.id,color:hex});
									this.style.background=hex;
									$("#"+this.id).parent().attr("id",hex);
								}
							}
						}); 
			});
	
		});
		
	
		function setChange(id)
		{
			var table = document.getElementById('table2');
			if (id=='0')
				for(var rowIndex=1;rowIndex<table.rows.length;rowIndex++)
					table.rows[rowIndex].style.display = "";
			else 
			{
				for(var rowIndex=1;rowIndex<table.rows.length;rowIndex++)
			    {
					if (table.rows[rowIndex].id==id)
						table.rows[rowIndex].style.display = "";
					else 
						table.rows[rowIndex].style.display = "none";
			    }
			}	
		}
		
		function add_func ()
		{
			var chk_def = document.getElementById('table1').getElementsByTagName('input');
			var table1 = document.getElementById('table1')
			var rows = table1.rows;
			var list = "test=[";
			var flag=0;
		
			for (var i = 1,j = 1; i < chk_def.length; i=i+2,j++)
			{
				if (chk_def[i].type=='checkbox' && chk_def[i].name=='check1' && chk_def[i].checked)
				{
					if (flag==0)
						flag=1;
					else 
						list=list+",";
					list=list+"{func_name:'"+rows[j].cells[1].innerHTML+"',color:'"+rows[j].cells[2].id+"',usr_classified_id:'"+$("#select").attr('value')+"'}";
					if ($("select").attr('value')!=0)
					{
						$("#tab2").append("<tr id="+$("#select").attr('value')+"><td><input type='checkbox' value='"+$("select").attr('value')+"' name='check2'/></td>"+
										"<td>"+rows[j].cells[1].innerHTML+"</tb>"+
										"<td id='"+rows[j].cells[2].id+"'><input type='text' class='color-picker' style='background-color:"+rows[j].cells[2].id+"' id='"+rows[j].cells[1].innerHTML+"' value='"+rows[j].cells[2].id+"'/></tb></tr>");
					}
					$(".color-picker").miniColors({
							letterCase: 'uppercase',
							change: function(hex, rgb) {
							
							},
							open: function(hex, rgb) {
								this.old_color=hex;
					
							},
							close: function(hex, rgb) {
								
								if (this.old_color!=hex)
								{
									$.get("servlet/Modify_cat",{req_type:"change_color",func_name:this.id,color:hex});
									this.style.background=hex;
									$("#"+this.id).parent().attr("id",hex)
								}
							}
					}); 
				}
			}
			list=list+"]";
			
	
			if (flag == 0)
				alert ("请选择要分类的函数");
			else if ($("select").attr('value')==0 && flag==1)
				alert("请选择一个类别!")
			else 
			{
				$.post('servlet/Modify_cat',list);
				for (var i = 1; i < chk_def.length; i=i+2)
				{
					for (var j = i; j < chk_def.length; j=j+2)
					{
						if (chk_def[j].type=='checkbox' && chk_def[j].name=='check1' && chk_def[j].checked)
						{
							table1.deleteRow((j+1)/2);
							i=j-2;
							break;
						}
					}
				}
			}
		}
		
		function remove_func ()
		{
			var chk_def = document.getElementById('table2').getElementsByTagName('input');
			var table2 = document.getElementById('table2')
			var rows = table2.rows;
			var list="test=[";
			var flag=0;
	
			for (var i = 1,j = 1; i < chk_def.length; i=i+2,j++)
			{
				if (chk_def[i].type=='checkbox' && chk_def[i].name=='check2' && chk_def[i].checked)
				{
					if (flag==0)
						flag=1;
					else 
						list=list+",";
			
					list=list+"{func_name:'"+rows[j].cells[1].innerHTML+"',color:'"+rows[j].cells[2].id+"',usr_classified_id:'4'}";
					$("#tab1").append("<tr id=tl><td><input type='checkbox' value='a' name='check1'/></td>"+
										"<td>"+rows[j].cells[1].innerHTML+"</tb>"+
										"<td id='"+rows[j].cells[2].id+"'><input type='text' class='color-picker' style='background-color:"+rows[j].cells[2].id+"' id='"+rows[j].cells[1].innerHTML+"' value='"+rows[j].cells[2].id+"'/></tb></tr>");
					$(".color-picker").miniColors({
							letterCase: 'uppercase',
							change: function(hex, rgb) {
							
							},
							open: function(hex, rgb) {
								this.old_color=hex;
					
							},
							close: function(hex, rgb) {
								if (this.old_color!=hex)
								{
									$.get("servlet/Modify_cat",{req_type:"change_color",func_name:this.id,color:hex});
									this.style.background=hex;
									$("#"+this.id).parent().attr("id",hex)
								}
							}
					}); 
				}
			}
			list=list+"]";
	
			if (flag == 0)
				alert ("请选择要取消分类的函数！");
			else 
			{
				$.post('servlet/Modify_cat',list);
				for (var i = 1; i < chk_def.length; i=i+2)
				{
					for (var j = i; j < chk_def.length; j=j+2)
					{
						if (chk_def[j].type=='checkbox' && chk_def[j].name=='check2' && chk_def[j].checked)
						{
							table2.deleteRow((j+1)/2);
							i=j-2;
							break;
						}
					}
				}
			}
			
		}
		
		function add_cat()
		{
			art.dialog({
					title:"添加分类",
					content:"<style>\
								input\
								{\
									border:#999 1px solid;\
									adding:2px 0px 2px 4px;\
									border-radius:2px;\
									margin:4px 0px;\
									width:200px;\
									height:25px;\
								}\
							</style>\
							<div id='userForm'>\
								<input type='text' name='cat_name' id='cat_name' placeholder='类别名'/><br/>\
								<input type='text' name='cat_id' id='cat_id' placeholder='类别ID(>10)' /><br/>\
								<input type='text' name='cat_color' id='cat_color' placeholder='颜色' /><br/>\
							</div>",
					okValue:"添加",
					ok:function(){
						var name=$("#cat_name").val();
						var id=$("#cat_id").val();
						var color=$("#cat_color").val();
						$.getJSON ('servlet/Add_cat',{req_type:'add_cat',cat_name:name, cat_id:id, cat_color:color},function(result){
								if (result['result']=='success')
								{
									$("#select").append("<option value="+id+">"+name+"</option>"); 	
									Utils.popAlert("添加分类成功");
								}
								else if (result['result']=='cat_exist')
									Utils.popAlert("分类已存在");
								else if (result['result']=='color_exist')
									Utils.popAlert("颜色已存在");
								else if (result['result']=='id_exist')
									Utils.popAlert("分类ID已存在");
								else 
									Utils.popAlert("错误！");
							});
						},
						cancelValue:"取消",
						cancel:function(){
						this.unlock();		
					}
					
				}).lock();
		}
		
		function selectAll()
		{
			if(this.checked==true)
				checkAll('test');  
			else  
				clearAll('test'); 
		}
		function checkAll(name)
		{
			var el = document.getElementsByTagName('input');
			var len = el.length;
			if (name == 'check1' || ((name == 'check2')&&($("#select").attr('value')=='0')))
			{
				for(var i=0; i<len; i++)
				{
					if((el[i].type=="checkbox") && (el[i].name==name))
						el[i].checked = true;
				}
			}
			else if (name == 'check2' && $("#select").attr('value')!='0')
			{
				
				for(var i=0; i<len; i++)
				{
					if ((el[i].type=="checkbox") && (el[i].name==name) && (el[i].value=='0'))
						el[i].checked = true;
					else if((el[i].type=="checkbox") && (el[i].name==name) && (el[i].value==$("#select").attr('value')))
						el[i].checked = true;
					else if ((el[i].type=="checkbox") && (el[i].name==name) && (el[i].value!=$("#select").attr('value')))
						el[i].checked = false;
				}
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