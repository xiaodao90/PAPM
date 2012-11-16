<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	
	<script src="static/jquery.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/highcharts-more.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
	
</head>

<body>
	<div id='head'>
		<div><h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>Rank:</h6><select id='proc_spider_rank' style='font-size:14px;display:inline;' onchange='sel_rank()'><option>All</option></select></div>
	</div>
	<div id="image" style="margin-top:28px">
		<form id="form" align="center">
			<div id="spider_test" style="min-width: 400px; height: 500px; margin: 0 auto"></div>
		</form>
	</div>
<script type="text/javascript">
	$(document).ready(function(){
		$.getJSON("servlet/highchart?req_type=get_rank", function (result){
			$.each (result, function (i, temp){
				$("#proc_spider_rank").append ("<option>Rank"+temp["rank"]+"</option>");
			});
			draw ();
		});
	});
	function draw ()
	{
		var options = {
            chart: {
	            renderTo: 'spider_test',
	            polar: true,
	            type: 'line'
	        },
	        
	        title: {
	            text: 'Time consuming',
	            x: -80
	        },
	        
	        pane: {
	            size: '80%'
	        },
	       /*  exporting:{
	        	url : 'servlet/Highcharts_export'
	        }, */
	        xAxis: {
	            categories: ["Iosend", "Iorecv", "Netsend", "Netrecv"],
	            tickmarkPlacement: 'on',
	            lineWidth: 0
	        },
	            
	        yAxis: {
	            gridLineInterpolation: 'polygon',
	            lineWidth: 0,
	            min: 0
	        },
	        
	        tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b>: '+ this.y +' us';
                }
            },
	        
	        legend: {
	            align: 'right',
	            verticalAlign: 'top',
	            y: 100,
	            layout: 'vertical'
	        },
	        
	        series: []
        };
        $.getJSON ("servlet/highchart?req_type=get_proc_spider_data", function(result){
        	$.each(result, function(i,temp){
        		var series = {
		            data: [],
		            pointPlacement: 'on'
	            };
	            series.name = temp["name"];
	            series.data = temp["data"];
	            options.series.push(series);
        	})

            var chart  = new Highcharts.Chart(options);	
		})
	}
	function sel_rank()
	{
		draw();
	}
</script>
</body>
</html>




