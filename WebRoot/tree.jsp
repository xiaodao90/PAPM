<!DOCTYPE div PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	<script src="static/jquery.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
</head>

<body bgcolor="#CBDAF2">
	<div id="head">
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>MPI Rank:</h6>
		<select id='rank' onchange='draw_tree()'>
			<option>all</option>
		</select>
		<h6 style='font-size:14px;width:auto;display:inline;'>方向:</h6>
		<select id='dir' onchange='draw_tree()'>
			<option value='TB'>由上到下</option>
			<option value='LR'>由左到右</option>
		</select>
		<button id="export" onclick="download()" style="float:right">下载</button>
	</div>
	<div id="empty" style="height:128px">
	</div>
	<div id="image">
		<form id="form" align="center" >

		</form>
	</div>

</body>

<script type="text/javascript">
	var url;
	$(document).ready(function(){
		$.getJSON ('servlet/Get_exp_info?req_type=get_tree_rank', function (result){
			$.each(result, function(i, temp){
				$("#rank").append ("<option>rank"+temp+"</option>");
			});
		});
		$.getJSON ('servlet/Get_exp_info?req_type=get_tree&graph_type=svg&rank=all&dir=TB',function(result){
			url = result ["url"];
			$("#form").append ("<a href="+result ["url"]+"><img id='tree' alt='树状图' width='100%' height='380' src='"+result ["url"]+"' /></a>");
		});
		
	})
	function draw_tree ()
	{
		var temp = $("#rank option:selected").text ();
		var rank;
		if (temp == "all")
			rank = "all";
		else
			rank = temp.substring(4);
		
		$.getJSON ("servlet/Get_exp_info?req_type=get_tree&graph_type=svg&rank="+rank+"&dir="+$("#dir option:selected").val(),function(result){
			url = result ["url"];
			$("#form").empty();
			$("#form").append ("<a href="+result ["url"]+"><img id='tree' alt='树状图' width='1120' height='380'  src='"+result ["url"]+"' /></a>");
		})
	}
	
	function download ()
	{
		window.location.href="Download_page.jsp?upfile_path=/&upfile_name="+url+"&upfile_newname=test.svg";
	}
</script>
</html>