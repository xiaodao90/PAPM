<html>

<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
	<script src="static/jquery.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/highcharts-more.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script>
</head>
<body>
	<div id="head">
		<div style="margin-left:15px"><h6 style='font-size:14px;width:auto;display:inline;'>Rank:</h6><select id='spider_rank' style='font-size:14px;display:inline;' onchange='sel_rank()'><option>All</option></select></div>
	</div>
	
	<div id="spider_graph" style="min-width: 400px; height: 500px; margin: 28 auto"></div>

<script type="text/javascript">

	$(document).ready(function(){
		$.getJSON("servlet/highchart?req_type=get_rank", function (result){
			$.each (result, function (i, temp){
				$("#spider_rank").append ("<option>Rank"+temp["rank"]+"</option>");
			});
			draw ();
		})
	})
	function draw ()
	{
		var options = {
            chart: {
	            renderTo: 'spider_graph',
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
	        credits:
	        {
	        	enabled:false
	        },
	        xAxis: {
	            categories: [],
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
        $.getJSON ("servlet/highchart?req_type=get_spider_data&rank="+$("#spider_rank option:selected").text(), function(result){
        	$.each (result, function(i, data){
        		if (i == 0)
        			options.xAxis.categories = data;
        		else
        		{
        			var series = {
			            data: [],
			            type: 'area',
			            pointPlacement: 'on'
		            };
		            series.name = "Rank" + data ["rank"];
		            series.data = data ["data"];
					options.series.push(series);
        		}
        	});
 
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
