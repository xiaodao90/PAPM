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
	<div id='head'>
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>主机:</h6><select id='host' onchange='sel_host()'></select>
		<h6 style='font-size:14px;width:auto;display:inline;'>类型:</h6><select id='type' onchange='sel_type()'><option value='CPU'>CPU</option><option value='I/O'>I/O</option></select>
		<span id='metric'><h6 style='font-size:14px;width:auto;display:inline;'>单位:</h6><select id='inmetric' onchange='sel_mertic()'><option value='Byte'>Byte</option><option value='package'>package</option>	</select></span>
	</div>
	<div id="image" style="margin-top:28px">
		<form id="node_compare_proc_graph" align="center" layoutH="42">
		</form>
	</div>
<script type="text/javascript">
	$(document).ready(function(){
		$("#metric").hide();
		
		$.getJSON ('servlet/Get_exp_info?req_type=get_node_info',function(result){
			$.each(result,function (i,temp){
				$("#host").append("<option value='"+temp["hostname"]+"'>"+temp["hostname"]+"</option");
			})
			draw ($("#host option:selected").text(), "cpu", "%25");
		});
	});
	function draw(host_name, instance, metric)
	{
		$.getJSON ("servlet/highchart?req_type=get_proc&host_name="+host_name+"&instance="+instance+"&metric="+metric, function (result){
				var options = {
					chart : {
						renderTo : 'node_compare_proc_graph',
						type: 'spline'
					},
			
					rangeSelector: {
			        	selected: 4
			 		},
			 			legend: {
		                align: "right",
		                layout: "vertical",
		                enabled: true,
		                verticalAlign: "middle"
		                /*labelFormatter: function() {
		                    return this.name + ' (T)';
		                }*/
		
		            },
			 		yAxis: {
					    
					},
					title : {
						text : 'PROC'
					},
					plotOptions: {
			    		
			    	},
					series :[]
				}
				$.each (result, function (i,temp){
					var series = {
	               		data:[]
	            	};
	            	series.name = temp["name"];
	            	series.data = temp["data"];

	            	options.series.push(series);
				})  
				var chart = new Highcharts.StockChart(options);
			});
	}

	function sel_type()
	{
		if ($("#type option:selected").text()=="I/O")
		{
			$("#metric").show();
			draw ($("#host option:selected").text(), "Io", $("#metric option:selected").text());
		}
		else if ($("#type option:selected").text()=="CPU")
		{
			$("#metric").hide();
			draw ($("#host option:selected").text(), "cpu", "%25");
		}
	}
	function sel_mertic()
	{
		draw ($("#host option:selected").text(), "Io", $("#metric option:selected").text());
	}
	function sel_host()
	{
		if ($("#type option:selected").text()=="I/O")
		{
			$("#metric").show();
			draw ($("#host option:selected").text(), "Io", $("#metric option:selected").text());
		}
		else if ($("#type option:selected").text()=="CPU")
		{
			$("#metric").hide();
			draw ($("#host option:selected").text(), "cpu", "%25");
		}
	}
</script>
</body>
</html>




