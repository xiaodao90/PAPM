<!DOCTYPE script PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
	<link href="lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" href="static/css/btn.css" media="screen"/>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
	<script src="static/jquery.js"></script>
	<script src="static/utils.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 
</head>

<body>
	<div id="head">
		<div><h6 style='font-size:14px;width:auto;margin-left:15px;display:inline;'>显示类型:</h6><select id='trace_type'  style='font-size:14px' onchange=sel_type(this.value)><option value='rank'>按rank显示</option><option value='pid'>按pid显示</option></select></div>
	</div>
	<div id="image" style="margin-top:28px;">
		<form id="form" align="center" layoutH="42">
			<div id="trace_graph" ></div>
		</form>
	</div>
</body>


<script type="text/javascript" >
 	var rank_num;
 	var data_num;
 	var trace_data;
 	var chart;
 	var Y_Categories_pid;
 	var Y_Categories_rank;

	$(document).ready(function(){
		$.getJSON("servlet/highchart?req_type=get_cat_trace_data",function(result){
			trace_data = result;
			rank_num = trace_data[0]["num"];
			data_num = trace_data.length;
			draw();
		});
	});

	function draw()
	{	
		
		var options = {
		    chart: {
		        renderTo:'trace_graph',
		        type: 'scatter',
	            borderWidth:0,
		        zoomType: 'x',
		        marginTop: 50,
		    },
		    title:{text:''},
	    	credits:{enabled:false},
		    tooltip:{
		        formatter:function(){
		            return this.point.pid+"<br/>func_name:"+this.point.name+"<br/>x:"+this.x+";<br/>y:"+this.y+"<br/>time:"+this.point.time;
		        }
		    },
			plotOptions: {
		        series: {
		            shadow:false,
		            color:'rgba(24,90,169,.75)',
		           	lineWidth:40,
		            marker:{
		                symbol:'diamond',
		                radius:0.2,
		                lineWidth:1,
		                states:{
		                    hover:{
		                        radius:2,
		                        lineWidth:0,
		                    }
		                }
		            }
		        }
		    },
		    yAxis:{
		    	endOnTick: false,
		    	minorTickInterval: 'auto',
     			maxPadding: 0.2,
            	gridLineWidth: 2,
            	tickWidth:1,
	            tickLength:5,
	            tickColor:'#ccc',
	            tickmarkPlacement:'on',
		    	categories: [],
		        title:{
		            text:"Rank"
		        },
		        gridLineWidth:1,
	            gridLineColor:'#ddd',
	            alternateGridColor:'rgba(156,156,156,.15)',
	            min:-1,
	            showFirstLabel:false,
	            showLastLabel:false,
	            labels:{
	            	overflow: 'justify',
	           		align:'right',
	            }
		    },
		    xAxis:{
		        minPadding:0,
		        maxPadding:0,
		  		min:0,
		        minorTickInterval:60 * 15 * 1000,
		       
		        lineColor:'#999',
		        lineWidth:1,
		        tickColor:'#999',
		        tickLength:5,
		        title:{
		        	text:'Time (ms)'
		        },
		        gridLineWidth:1,
		        gridLineColor:'#ccc',
		        minorGridLineWidth:1,
		        minorGridLineColor:'#ddd',
		        minorGridLineDashStyle:'ShortDot',
		        startOnTick:true,
		        endOnTick:true,
		        showFirstLabel:true,
		        showLastLabel:true,
	
		    },
		    exporting: {
	            enabled: true,
	        },
		    series:[]
		};
		
		chart  = new Highcharts.Chart(options);
		chart.showLoading('Loading data from server...');
		
    	$.each (trace_data,function(i,temp)
    	{
    		if (i == 0)
    		{
    			Y_Categories_pid = temp["pid"];
    			Y_Categories_rank = temp["rank"];
    		}
    		else
    		{
    			var series = {
	               		data:[]
	            	};
    	
            	series.name = temp["name"];
            	series.color = temp["color"];
            	series.data = temp["data"];
            	//series.data = [];

            	options.series.push(series);
    		}
	    });
		
  		options.plotOptions.series.lineWidth = 15;
		alert (options.series[0])
    	options.yAxis.max = rank_num;
    	if (rank_num < 4)
    		options.chart.height = 250;
    	else if (rank_num >= 4 && rank_num <= 8)
    		options.chart.height = 400;
    	else if (rank_num > 8 && rank_num <= 12)
    		options.chart.height = 550;
    	else
    		options.chart.height = 35 * rank_num + 50;
    	chart = new Highcharts.Chart(options);
   
	}
	
	function sel_type (value)
	{
		if (value == "pid")
		{
			chart.yAxis[0].setCategories(Y_Categories_pid);
			chart.yAxis[0].setTitle({
				text: 'Pid'
			});
		}
		else 
		{
			chart.yAxis[0].setCategories(Y_Categories_rank);
			chart.yAxis[0].setTitle({
				text: 'Rank'
			});
		}
	}
	
</script>

</html>