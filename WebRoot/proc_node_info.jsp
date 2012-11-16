<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	<script src="static/jquery.js"></script>
	<script src="static/highstock.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
</head>

<body>
	<div id="head">
		<div><h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>主机:</h6><select id='node_proc_host' onchange='node_proc_show(this.value)'></select></div>
	</div>
	<div id="node_proc_image">
		<form id="node_proc_main" layoutH="42">
		</form>
	</div>
<script type="text/javascript">
	$(document).ready(function(){
		$.getJSON('servlet/Get_exp_info?req_type=get_node_info',function(result){
			$.each(result,function (i,temp){
				$("#node_proc_host").append("<option value='"+temp["hostname"]+"'>"+temp["hostname"]+"</option");
			});
			node_proc_show($("#node_proc_host option:selected").text());
		});
	});
	function node_proc_show(value)
	{
		$.getJSON ('servlet/Get_exp_info?req_type=get_graph&host_name='+value,function(result){
			$("#node_proc_main").empty();
			$.each(result, function(i,temp){	
				$("#node_proc_main").append ("<div id='graphbox' style=' width:50%; height: 300px;float: left;'><div id='"+temp["instance"]+"_"+temp["metric"]+"' class='grid' style=' width:400px; height: 250px;padding:40px 80px 40px 80px'></div></div>");
		
				node_proc_draw (value, temp["instance"], temp["metric"]);			
			});
		});
	}
	function node_proc_draw(host_name, instance, metric)
	{
		if (metric == "%")
			metric_temp = "%25";
		else 
			metric_temp = metric;
		$.getJSON ("servlet/highchart?req_type=get_single_proc_data&host_name="+host_name+"&instance="+instance+"&metric="+metric_temp, function(result){
			var options = {
				chart : {
					renderTo : instance+"_"+metric,
					type: 'spline',
					borderColor: '#000000',
	           		borderWidth: 2,
	           		borderRadius: 10
				},
			
				rangeSelector: {
			       	enabled:false	
			 	},
			 	navigator: {
		            enabled: false
		        },
		        scrollbar: {
		            enabled: false
		        },
			 	yAxis: {   
			 		min: 0,
			 		offset: 25,
			 		labels :{
			 			align:"left",
			 			
			 		} 
				},
				title : {
					text : instance+"  ["+metric+"]"
				},
				tooltip: {
		            formatter: function() {
		                return ''+
		                   instance +':   '+ this.y +metric+'<br>Time:'+this.x;
		            }
		        },
				plotOptions: {
			    		
			    },
			    credits: {  
					enabled: false  
				}, 
				series :[]
			};
			var series = {
	               data:[]
	        };
	    
	        series.data = result;
	
	        options.series.push(series);
			var chart = new Highcharts.StockChart(options);
		});
	}
</script>
</body>
</html>




