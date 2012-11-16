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
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>Rank:</h6><select id='rank' style='font-size:14px;display:inline;' onchange='sel_rank()'></select>
	</div>
	<div id="papi_graph" style="min-width: 400px; height: 400px; margin: 28px auto"></div>
<script type="text/javascript">
	$(document).ready(function(){
		$.getJSON ("servlet/highchart?req_type=get_rank", function (result){
			$.each (result, function (i, temp){
				$("#rank").append ("<option>"+temp["rank"]+"</option>");
			});
			draw ();
		});

		
	});
	
	function draw ()
	{
		var options = {
			chart : {
				renderTo : 'papi_graph'
				
			},
			rangeSelector: {
        		selected: 4
 			},
 			yAxis: {
		    	labels: {
		    		formatter: function() {
		    			return (this.value > 0 ? '+' : '') + this.value + '%';
		    		}
		    	},
		    	plotLines: [{
		    		value: 0,
		    		width: 2,
		    		color: 'silver'
		    	}]
		    },
			title : {
				text : 'PAPI'
			},
			plotOptions: {
    		
    		},
			series :[]
		}
		$.getJSON ('servlet/highchart?req_type=get_PAPI_data&rank='+$("#rank option:selected").text(), function (result){
			
		 	$.each (result, function (i,temp){
				var series = {
          			data:[]
       			};
      			series.name = temp["name"];
       			series.data = temp["data"];
       			//series.type = "area";
    
       			options.series.push(series);
			})  
		 
			var chart = new Highcharts.StockChart(options);
		})
	}
	
	function sel_rank ()
	{
		draw ();
	}
</script>
</body>
</html>