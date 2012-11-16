<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	<script src="static/jquery.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
</head>

<body>
	<div id='head'>
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>PROC数据类型:</h6><select id='instance' style='font-size:14px;display:inline;' onchange='sel_instance()'></select>
		<span id='metric_box'><h6 style='font-size:14px;width:auto;display:inline;'>单位:</h6><select id='metric' style='font-size:14px;display:inline;' onchange='sel_metric()'><option>%</option></select></span>
	</div>
	<div id="proc_stack" style="min-width: 400px; height: 400px; margin: 28px auto"></div>
<script type="text/javascript">
	var instance = "cpu";
	var metric = "%";

	$(document).ready(function(){
		draw ();
		$.getJSON("servlet/highchart?req_type=get_proc_instace", function (result){
			$.each (result, function (i, temp){
				$("#instance").append ("<option>"+temp+"</option>");
			});
			draw ();
		});
	});
	function draw ()
	{

		var options = {
			chart: {
                renderTo: 'proc_stack',
                type: 'area',
                zoomType: 'x'
            },
            title: {
                text: 'proc_stack'
            },
            xAxis: {
            	type: 'datetime',
            	dateTimeLabelFormats: {
            		second: '%H:%M:%S',
            		
                },
                categories: [],
                tickmarkPlacement: 'on',
                title: {
                    text: "time"
                },
                labels:{
                	formatter: function() {
                		return Highcharts.dateFormat(' %d %b %H:%M:%S', this.value);
                	}
                }
            },
            yAxis: {
            	title: {
                    text: instance
                },
               
            },
            tooltip: {
  				formatter: function() {
  					if ($("#instance option:selected").text() == "cpu" || $("#instance option:selected").text() == "memutiliztion")
						return this.series.name + "<br>"+Highcharts.numberFormat(this.y, 2)+metric;
					else 
						return this.series.name + "<br>"+this.y+metric;
						
				}   
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#666666'
                    }
                }
            },
            series: []
		};
		var temp;
		if (metric == "%")
			temp = "%25";
		else 
			temp = metric;
		$.getJSON ("servlet/highchart?req_type=get_proc_stack_data&instance="+$("#instance option:selected").text()+"&metric="+temp, function(result){
			
			options.xAxis.categories = result ["time"];
			options.series = result ["series"];
		
			var chart  = new Highcharts.Chart(options);	
		})
		
	}
	function sel_instance()
	{
		instance = $("#instance option:selected").text();
		if ($("#instance option:selected").text() == "cpu" || $("#instance option:selected").text() == "memutiliztion")
		{
			metric = "%";
			$("#metric").empty();
			$("#metric").append ("<option>%</option>");
		}
			
		else if ($("#instance option:selected").text() == "mem_total" || $("#instance option:selected").text() == "mem_use")
		{
			metric = "KB";
			$("#metric").empty();
			$("#metric").append ("<option>KB</option>");
		}
			
		else if ($("#instance option:selected").text() == "Netsend" || $("#instance option:selected").text() == "Netrecv" )
		{
			$("#metric").empty();
			$("#metric").append ("<option>Byte</option>");
			$("#metric").append ("<option>package</option>");
			metric = $("#metric option:selected").text();

		}
		else if ($("#instance option:selected").text() == "IO_rchar" || $("#instance option:selected").text() == "IO_wchar" || $("#instance option:selected").text() == "IO_read" || $("#instance option:selected").text() == "IO_write")
		{
			metric = "Byte";
			$("#metric").empty();
			$("#metric").append ("<option>Byte</option>");
		}
		draw();
	}
	
	function sel_metric ()
	{
		metric = $("#metric option:selected").text();
		draw ();
	}
</script>
</body>
</html>




