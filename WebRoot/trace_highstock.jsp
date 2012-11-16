<!DOCTYPE script PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
	<link href="lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
	<script src="static/jquery.js"></script>
	<script src="static/utils.js"></script>
	<script src="static/highcharts.js"></script>
	<script src="static/highstock.js"></script>
	<script src="static/exporting.js"></script>
	<script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 
</head>

<body>
	<div id='title_bar'></div>
	<div id='tool_bar'></div>
	<div id="image">
		<form id="form" align="center" layoutH="42">
			<div id="trace_graph" ></div>
		</form>
	</div>
</body>


<script type="text/javascript" >
 	var rank_num;
	$(document).ready(function(){
		 $("#tool_bar").ligerMenuBar({ items: [
        		{ text: "<div><h6 style='font-size:14px;width:auto;display:inline;'>显示类型:</h6><select id='profile_type' style='font-size:14px' onchange='draw(this.value)'><option value='rank'>按rank显示</option><option value='pid'>按pid显示</option></select><div>"}	
        	]
        });
		draw("rank");
	});

	function draw(type)
	{
		var options = {
		    chart: {
		        renderTo:'trace_graph',
		        type: 'scatter',
	            borderWidth:0,
	         	height:400,

		      //  zoomType: 'x',
		        marginTop: 50
		    },
		 
			//legend:{
			//	enabled:true
			//},
		    title:{text:'Trace'},
	    	credits:{enabled:false},
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
		                        lineWidth:0
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
		            text:type
		        },
		        gridLineWidth:1,
	            gridLineColor:'#ddd',
	            alternateGridColor:'rgba(156,156,156,.15)',
	            min:-1,
	            showFirstLabel:false,
	            showLastLabel:false,
		        /* 
	            showLastLabel:false,
	            tickWidth:1,
	            tickLength:60,
	            tickColor:'#ccc',
	            tickmarkPlacement:'on', */
	            
	            labels:{
	            	overflow: 'justify'
	   
	            /*    align:'right',
	                x:-2,
	                y:-15,
	                style:{
	                    fontSize:'14px',
	                    fontWeight:'bold'
	                }*/
	            }
		    },
		    xAxis:{
		        minPadding:0,
		        maxPadding:0,
		        type:'datetime',
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
		        
		        labels:{
		            y:18,
		            style:{
		                fontSize:'14px',
		                fontWeight:'bold'
		            },
		            formatter:function(){
		                return this.value;
		            }
		        }, 
		        
		    },
		   
		    exporting: {
	            enabled: true,
	        },
		    series:[]
		};
		var chart  = new Highcharts.Chart(options);
	
		chart.showLoading('Loading data from server...');
		
		
	    $.getJSON("servlet/highchart?req_type=get_trace_data_highstock",function(result){
	    	$.each (result,function(i,temp){
	    		if (i == 0)
	    		{
	    			if (type == "pid")
	    			{
	    				$.each(temp["pid"],function (j,pid){
	    					options.yAxis.categories.push (pid);
	    				}); 
	    			}
	    			else if (type == "rank")
	    			{
	    				$.each(temp["pid"],function (j,pid){
		    				options.yAxis.categories.push (j);
		    			}); 
	    			}
	    		}
	    		else 
	    		{
	    			var series = {
	               		data:[]
	            	};
	            	series.name = temp["name"];
	            	series.color = temp["color"];
	            	
	            	if (temp["name"] == "LDMC_Application" || temp["name"] == "main()")
	            		series.lineWidth = 18;
	            	else if (temp["name"] == "comm_line")
	            	{
	            		series.lineWidth = 1;
	            	}
	            		
	            	series.data = temp["data"];

	            	options.series.push(series);
	    		}
	    	})
	
	  		options.plotOptions.series.lineWidth = 15;
	    
	    //	options.chart.height = 30 * rank_num;
	    //	options.yAxis.labels.y = 0 - options.chart.height / 8
	    	options.yAxis.max = options.yAxis.categories.length;
	    
	    	if (options.yAxis.categories.length < 4)
	    		options.chart.height = 250;
	    	else if (options.yAxis.categories.length >= 4 && options.yAxis.categories.length <= 8)
	    		options.chart.height = 400;
	    	else if (options.yAxis.categories.length > 8 && options.yAxis.categories.length <= 12)
	    		options.chart.height = 550;
	    	else
	    		options.chart.height = 35 * options.yAxis.categories.length + 50;
	    	
	    	//options.yAxis.labels.y = 15 - options.chart.height / rank_num / 2 +rank_num
	    	//alert (options.yAxis.labels.y)
	    
	    	//chart  = new Highcharts.Chart(options);
	    	alert (options.series[0].data)
	    	chart = new Highcharts.StockChart(options);
	    });
	}
	
</script>

</html>