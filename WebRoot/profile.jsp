<!DOCTYPE script PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	<script src="static/jquery.js"></script>
	<script src="static/utils.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
	
</head>

<body>
	<div id='head'>
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>节点:</h6><select id='profile_rank' style='font-size:14px' onchange='show_rank(this)'><option>全部节点</option></select>
		<h6 style='font-size:14px;width:auto;display:inline;'>显示类型:</h6><select id='profile_type' style='font-size:14px' onchange='show_type(this)'><option>rank号</option><option>pid号</option></select>
	</div>

	<div id="sigle_node"></div>
	<div id="image">
		<form id="profile_form" align="center" layoutH="42">
			<div id="profile_graph" style="min-width: 400px; margin: 0 auto"></div>
		</form>
	</div>

<script>
	$(document).ready(function(){
		$.getJSON ('servlet/Get_exp_info?req_type=get_rank_info',function(result){
			$.each(result,function(i,temp){
				$("#profile_rank").append("<option value='"+temp["rank"]+"'>"+temp["rank"]+"</option>");
			})
			/* pid_list.menu.items.push("{text: 'TXT'}");
			alert(pid_list.menu.items.toString());
			tool_bar.addItem(pid_list); */
			draw ("rank");
		})
		
	});
	function show_rank(t)
	{
		if ($("#profile_rank option:selected").text()=="全部节点")
		{
			$("#sigle_node").empty();
			$("#profile_graph").show();		
		}
		else
		{
			$("#profile_graph").hide();
			$("#sigle_node").empty();
			$("#sigle_node").append ("<div id='image'>"+
									"<form align='center'>"+
										"<img title='profile图' src='servlet/Get_chart?type=bar&format=png&pid="+$("#profile_rank option:selected").text()+"' />"+
									"</form>"+
								"</div>");
		}
	}
	function show_type ()
	{
		if ($("#profile_type option:selected").text()=="rank号")
			draw("rank");
		else if ($("#profile_type option:selected").text()=="pid号")
			draw("pid");
	}
	function draw (type)
	{
		var options = {
	    	chart: {
	        	renderTo: 'profile_graph',
	            type: 'bar',
	            zoomType: 'y',
	        },
	        title: {
	            text: 'profile'
	        },
	        xAxis: {
	            categories: [],
	            title:{
	            	text:type
	            },
	        },
	        yAxis: {
	            min: 0,
	            title: {
	                text: 'Time consume (ms)'
	            },
	        },
	        legend: {
	            backgroundColor: '#FFFFFF',
	            
	        },
	        tooltip: {
	            formatter: function() {
	                return ''+
	                   this.series.name +': '+ this.y +'ms';
	            }
	        },
	        plotOptions: {
	            series: {
	                stacking: 'normal'
	            }
	        },
	        credits:{enabled:false},
	        series: []
	    };
 	
    	$.getJSON("servlet/highchart?req_type=get_profile_data",function(result){
    		if (type == "rank")														//show rank
    		{
    			$.each (result[0]["pid"],function(i,temp){
	    			options.xAxis.categories.push (i);
	    		});
    		}
    		else if (type == "pid")													//show pid
    		{
    			$.each (result[0]["pid"],function(i,temp){
	    			options.xAxis.categories.push (temp);
	    		}) 
    		}
    		
    		$.each (result[1]["data"],function (i, temp){
    			var series = {
               		data:[]
            	};
            	series.name = temp["func_name"];
            	series.color = temp["color"];
            
            	$.each (temp["data"],function(j,data){
            		series.data.push(data/1000)
            	})
            	options.series.push(series);
    		}) 
    		
    		if (options.xAxis.categories.length < 4)
	    		options.chart.height = 250;
	    	else if (options.xAxis.categories.length >= 4 && options.xAxis.categories.length <= 8)
	    		options.chart.height = 400;
	    	else if (options.xAxis.categories.length > 8 && options.xAxis.categories.length <= 12)
	    		options.chart.height = 550;
	    	else
	    		options.chart.height = 35 * options.xAxis.categories.length + 50;
    		
    		
			var chart  = new Highcharts.Chart(options);			 
    	})
	}
</script>
</body>

</html>
