<!DOCTYPE div PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />

    <link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" href="static/css/reset.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="static/css/jquery.ui.slider.css">
	<link rel="stylesheet" href="static/css/jquery.ui.theme.css">
	<link rel="stylesheet" href="static/css/btn.css" media="screen"/>
	
	<script src="static/jquery.js"></script>
	<script src="static/utils.js"></script>
	<script src="static/jqueryui/jquery.ui.core.js"></script>
	<script src="static/jqueryui/jquery.ui.widget.js"></script>
	<script src="static/jqueryui/jquery.ui.mouse.js"></script>
	<script src="static/jqueryui/jquery.ui.slider.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 

	<style type="text/css">
		.showtime {display: inline-block;}
		.box{border:1px solid #ddd;}
		#time_range{display: inline-block;margin-left:5px;margin-right:5px}
		#main .test{width: 13.72%;float: left;height: 80px;margin-left:0.5%;}
		#main .intest
		{
			width: 100%;
			background-color:#9ACD32;
			height: 74px;
			border:1px solid #999;
			box-shadow: 1px 0 0 rgba(0, 0, 0, .5), 2px 0 0 rgba(255, 255, 255, .5);
			-moz-border-radius: .50em 0 0 .50em;
			-webkit-border-radius: .50em 0 0 .50em;
			border-radius: .30em .30em .30em .30em;
		}
		#main .intest:hover{border:1px solid purple;opacity:0.8;}
		.intest{position: relative;cursor:pointer;}
		.intest h6{text-align: center;}
		.intest p{text-align: center;}
		#header #time_range{width:90%;}
		#exp_name 
		{
			font-weight: bold;
			font-size: 13px;
			height:18px;
			border-bottom:1px solid black;
			background-color:#ddd;
		}
		#tlist
		{
		
			list-style-type:circle;
			list-style-position:inside;
			font-size:14px;
		}
		#th
		{
			font-weight: bold;
			font-size: 13px;
		}
		#tc
		{
			margin-left:10px;
		}
		#tlist
		{
			margin-left:2px;
		}
	
	</style>

</head>

<body>
	<div id='head'>
		<div>
			<h6 style='font-size:14px;width:auto;display:inline;margin-left:3px'>选择时间:</h6>
			<div style='width:15%;' id='time_range'></div>
			<div class='showtime'>
				<h6 style='font-size:14px;width:auto;display:inline;'>开始时间:</h6>	
				<input type='text' class='box' id='starttime'/>
			</div>
			<div class='showtime'>
				<h6 style='font-size:14px;width:auto;display:inline;'>结束时间:</h6>
				<input type='text' class='box' id='endtime'/>
			</div>
			<button id='reloadAll' class='button' style='display:inline;margin-left:2px;'>载入数据</button>
			<button id='select_all' class='button' style='display:inline;margin-left:2px;' onclick='select_all()'>全选</button>
			<button id='select_else' class='button' style='display:inline;margin-left:2px;' onclick='select_else()'>反向选择</button>
			<button id='remove_sel' class='button' style='display:inline;margin-left:2px;' onclick='remove_sel()'>删除所选</button>
		</div>
	</div>
	<div id="main" style="margin-top:28px"></div>




</body>

<script type="text/javascript">
	var exp_temp;
	var that;

	$(document).ready(function(){
		var fs=$(".finish");
		
		for(var i=0;i<fs.length;i++){
			var f=fs[i];
			if($(f).attr('rel')==0){
				$(f).parent().css('background-color','#EE7600');
			}
		}
		
		(function(){    //add click handlers of .intest
			var clickFlag=false;
			$(".intest").live('mousedown',function(e){
				that=this;
				var e = window.event || e;
				var value = e.button;
				
				if(value == 2 || value ==3)					//右键点击
		       	{
		       		exp_temp = $(that).children("h6").text();
		     		/* $.ligerDialog.confirm('是否要删除实验'+$(that).children("h6").text()+"?",function (value){
						if (value)
						{
							$.ajax({
								url:'servlet/Delete_experiment?req_type=delete_exp&exp_name='+$(that).children("h6").text(),
								success:function(r){
									$(that).parent(".test").css("display","none");
								}
							});
						}
					}); */
	            }
	            else										//左键点击
	            {
	            	exp_temp = $(that).children("h6").text();
	           	}
			});
		})();
		
		$.getJSON ("servlet/Get_setime?req_type=get_setime", function(result){
			var st;
			var et;
			$.each(result,function(entryIndex,entry)
			{
				if (entryIndex == 0)
					st = new Date(Utils.cvtSToMs(entry['start']));
				else if (entryIndex == 1)
					et = new Date(Utils.cvtSToMs(entry['end']));
	         	});
	         	
			var tap=et.getTime()-st.getTime();
			result[0]=st.getTime();
			result[1]=et.getTime();
		
			$("#starttime").attr('rel',result[0]);
			$("#endtime").attr('rel',result[1]);
			$("#starttime").val(Utils.stDateFormat(st));
			$("#endtime").val(Utils.stDateFormat(et));
			$("#time_range" ).slider({
				range: true,
				min: 0,
				max: tap,
				values: [ 0, tap ],
				slide: function( event, ui ) {
					$("#starttime").attr('rel',result[0]+ui.values[0]);
					$("#endtime").attr('rel',result[0]+ui.values[1]);
						
					$("#starttime" ).val(Utils.dateFormat(result[0]+ui.values[0]));
					$("#endtime").val(Utils.dateFormat(result[0]+ui.values[1]));
				}
			});
			showExprs();
			$("#reloadAll").click(function(){
				$("#main").html("");
				showExprs();
			});
			
			menu1 = $.ligerMenu({ top: 100, left: 100, width: 120, items:
            [
            { text: '选择', click: select_exp,icon:'add' },
            { text: '删除', click: delete_exp,icon:'delete' },
            { text: '预处理', click: pretreat,icon:'edit' },
            { line: true },
            { text: '重置实验', click: reset_exp },
            { line: true },
            { text: '关闭', click: close_menu }
            ]
            });
		 
			$("#main").bind("contextmenu", function (e)
            {
                menu1.show({ top: e.pageY, left: e.pageX });
                return false;
            });
			
		});
		
	});
	
	function showExprs ()
	{
		var sstart=$("#starttime").attr('rel');
		var send=$("#endtime").attr('rel');
		$.getJSON('servlet/Get_experiment?req_type=get_experiment&sstart='+Utils.antiCvtSToMs(parseInt(sstart))+"&send="+Utils.antiCvtSToMs(parseInt(send)),function(result){
			for(var i=0;i<result.length;i++){
				var res=result[i];
				var tstart=Utils.stDateFormat(new Date(Utils.cvtSToMs(res['start'])));
				var tstop=Utils.stDateFormat(new Date(Utils.cvtSToMs(res['stop'])));
				var ttype=res['type'];
				var towner=res['owner'];
				
				if(res.finished==0){
					$("#main").append("<div class='test'><div class='intest' id='exp' style='background-color:#FFA500;' title='开始时间:"+tstart+"'><h6 id='exp_name'>"+res.name+"<input type='checkbox' style='float:right;margin-right:2px;margin-top:2px' name='"+res.name+"' /></h6><ul id='tlist' onclick=exp_sel()><li id='tlist'><span id='th'>Type:&nbsp&nbsp</span><span id='tc'>"+ttype+"</span></li><li id='tlist'><span id='th'>Owner:&nbsp;</span><span id='tc'>"+towner+"</span></li><li id='tlist'><span id='th'>Status:</span><span id='tc'>Unfinished</span></li></ul>");								
				}
				else{
					$("#main").append("<div class='test'><div class='intest' id='exp' title='开始时间:"+tstart+"&#13结束时间:"+tstop+"'><h6 id='exp_name'>"+res.name+"<input type='checkbox' style='float:right;margin-right:2px;margin-top:2px' name='"+res.name+"' /></h6><ul id='tlist' onclick=exp_sel()><li id='tlist'><span id='th'>Type:&nbsp&nbsp</span><span id='tc'>"+ttype+"</span></li><li id='tlist'><span id='th'>Owner:&nbsp;</span><span id='tc'>"+towner+"</span></li><li id='tlist'><span id='th'>Status:</span><span id='tc'>Finished</span></li></ul>");	
				}
			} 
		});
	}
	
	function select_exp()
	{
		$.ligerDialog.success("<h6 style='font-size:14px;width:auto;display:inline;'>已选择实验:</h6><br><br>"+exp_temp,"",function (){
			$.getJSON ('servlet/Select_experiment?req_type=select_exp&exp_name='+exp_temp, function (result){
				
			});
			$('#exp_name', window.parent.document).html(exp_temp);
		});
	}
	function delete_exp()
	{
		$.ligerDialog.confirm("是否要删除实验"+exp_temp+"?",function (value){
			if (value)
			{
				$.ajax({
					url:'servlet/Delete_experiment?req_type=delete_exp&exp_name='+exp_temp,
					success:function(r){
						$(that).parent(".test").css("display","none");
					}
				});
			}
		});
	}
	
	function pretreat ()
	{
		var wait_dialog = $.ligerDialog.waitting("正在处理数据中,请稍候...");
		$.getJSON ("servlet/Trace2Profile?req_type=pretreatment&exp_name="+exp_temp, function (result){
			wait_dialog.close();
			$('#profile', window.parent.document).html(1);
			$('#tree', window.parent.document).html(1);
			$.ligerDialog.success("预处理完成");
		});
	}
	function close_menu()
    {
        menu1.hide();
    }
	
	function reset_exp ()
	{
		var wait_dialog = $.ligerDialog.waitting("正在重置实验中,请稍候...");
		$.getJSON ("servlet/Get_experiment?req_type=reset_exp&exp_name="+exp_temp, function (result){
			if (result["result"] == "success")
			{
				wait_dialog.close();
				$.ligerDialog.success("实验已重置");
			}
			else 
				alert (result["result"])
			
		})
	}
	
	function remove_sel ()
	{
		$.ligerDialog.confirm("是否要删除所选实验?",function (value){
			if (value)
			{
				var chk = document.getElementById('main').getElementsByTagName('input');
				var exp_list = "exp_list=[";
				var flag=0;
				for (var i = 0; i < chk.length;i++)
				{
					if (chk[i].type=='checkbox' && chk[i].checked)
					{
						if (flag==0)
							flag=1;
						else 
							exp_list=exp_list+",";
						exp_list=exp_list+"{exp_name:'"+chk[i].name+"'}";
					}
				}
				exp_list=exp_list+"]";
			
				$.post('servlet/Delete_experiment',exp_list,function(){
					$("#main").html("");
					showExprs();
				});
				
			}
		});
	}
	
	function select_all ()
	{
		var chk = document.getElementById('main').getElementsByTagName('input');
		for (var i = 0; i < chk.length; ++i)
		{
			if (chk[i].type == 'checkbox')
				chk[i].checked = true;
		}
	}
	
	function select_else ()
	{
		var chk = document.getElementById('main').getElementsByTagName('input');
		for (var i = 0; i < chk.length; ++i)
		{
			if (chk[i].type == 'checkbox')
				chk[i].checked = !chk[i].checked;
		}
	}
	
	
	function exp_sel()
	{
		$.ligerDialog.success("<h6 style='font-size:14px;width:auto;display:inline;'>已选择实验:</h6><br><br>"+$(that).children("h6").text(),"",function (){
			//window.location.href='servlet/Select_experiment?req_type=select_exp&exp_name='+$(that).children("h6").text();
			var init_dialog = $.ligerDialog.waitting("实验初始化中，请稍候...");
			$.getJSON ('servlet/Select_experiment?req_type=select_exp&exp_name='+$(that).children("h6").text(), function (result){
				init_dialog.close();
				$('#papi', window.parent.document).html(result["papi"]);
				$('#proc', window.parent.document).html(result["proc"]);
				$('#comm', window.parent.document).html(result["comm"]);
				$('#profile', window.parent.document).html(result["profile"]);
				$('#tree', window.parent.document).html(result["tree"]);
				if (result["profile"] == 0 || result["tree"] == 0)
				{
					$.ligerDialog.confirm("所选实验有部分数据未处理，是否要现在进行预处理？",function (value){
						if (value)
						{
							var wait_dialog = $.ligerDialog.waitting("处理数据中，请稍候...");
							if (result["profile"] == 0 && result["tree"] == 1)
							{
								$.getJSON ("servlet/Trace2Profile?req_type=trace2profile&exp_name="+$(that).children("h6").text(), function (result){
									wait_dialog.close();
									$.ligerDialog.success("预处理完成");
									$('#profile', window.parent.document).html(1);
								});
							}
							else if (result["profile"] == 1 && result["tree"] == 0)
							{
								$.getJSON ("servlet/Trace2Profile?req_type=trace2tree&exp_name="+$(that).children("h6").text(), function (result){
									wait_dialog.close();
									$.ligerDialog.success("预处理完成");
									$('#tree', window.parent.document).html(1);
								});
							}
							else if (result["profile"] == 0 && result["tree"] == 0)
							{
								var flag = 0;
								$.getJSON ("servlet/Trace2Profile?req_type=trace2profile&exp_name="+$(that).children("h6").text(), function (result){
									flag ++;
									if (flag == 2)
									{
										wait_dialog.close();
										$.ligerDialog.success("预处理完成");
										$('#profile', window.parent.document).html(1);
										$('#tree', window.parent.document).html(1);
									}
								});
								$.getJSON ("servlet/Trace2Profile?req_type=trace2tree&exp_name="+$(that).children("h6").text(), function (result){
									flag ++;
									if (flag == 2)
									{
										wait_dialog.close();
										$.ligerDialog.success("预处理完成");
										$('#profile', window.parent.document).html(1);
										$('#tree', window.parent.document).html(1);
									}
								});	
							}	
						}
					});
				}
			});
			$('#exp_name', window.parent.document).html($(that).children("h6").text());
		});
	}
</script>
</html>