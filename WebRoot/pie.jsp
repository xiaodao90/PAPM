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
	<div id="head">
		<div><h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>Rank:</h6><select id='pie_rank' style='font-size:14px;display:inline;' onchange='sel_rank()'><option>All</option></select></div>
	</div>

	<div id="pie_graph" style="min-width: 400px; height: 500px; margin:28px auto;"></div>
<script type="text/javascript">

	$(document).ready(function(){
	
		$.getJSON("servlet/highchart?req_type=get_rank", function (result){
			$.each (result, function (i, temp){
				$("#pie_rank").append ("<option>Rank"+temp["rank"]+"</option>");
			});
			draw ();
		})
	})
	function draw ()
	{
		var options = {
	           chart: {
	               renderTo: 'pie_graph',
	               plotBackgroundColor: null,
	               plotBorderWidth: null,
	               plotShadow: false
	           },
	           title: {
	               text: '分类饼状图'
	           },	
	           tooltip: {
	               formatter: function() {
	                   return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +' %';
	               }
	           },
	           credits:{
	        	   enabled:false
	           },
	           plotOptions: {
	               pie: {
	                   allowPointSelect: true,
	                   cursor: 'pointer',
	                   showInLegend: true,
	                   dataLabels: {
	                       enabled: true,
	                       color: '#000000',
	                       connectorColor: '#000000',
	                       formatter: function() {
	                           return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +' %';
	                       }
	                   }
	               }
	           },
	           series: []
	       };
	       $.getJSON ("servlet/highchart?req_type=get_pie_data&rank="+$("#pie_rank option:selected").text(), function(result){
	       	var series = {
				type: 'pie',
				name: 'CAT',
				data:[]
			};
			series.data=result;
			options.series.push(series);
	
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




