<!DOCTYPE script PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
	<link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
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
	</div>
	<div id="image" style="margin-top:28px;">
		<form id="form" align="center" layoutH="42">
			<div id="trace_graph" ></div>
		</form>
	</div>
</body>

<script type="text/javascript" >
	var exp_type = "<%=session.getAttribute("exp_type").toString()%>";
	if (exp_type != "omp")
	{
		$("#head").append("<div><h6 style='font-size:14px;width:auto;margin-left:15px;display:inline;'>显示类型:</h6><select id='trace_type'  style='font-size:14px' onchange=sel_type(this.value)><option value='rank'>按rank显示</option><option value='pid'>按pid显示</option></select><button id='comm_line' class='button' style='display:inline;margin-left:10px;' value=0 onclick='comm_line()'>显示通信线</button></div>");
	}
	var totalTime = 0;
 	var rank_num;
 	var data_num;
 	var trace_data;
 	var chart;
 	var Y_Categories_pid;
 	var Y_Categories_rank;
 	var Y_Categories_thread;
 	var comm_data = {
       	data:[]
	};
	$(document).ready(function(){
		$.getJSON("servlet/highchart?req_type=get_trace_data",function(result){
			trace_data = result;
			rank_num = trace_data[0]["num"];
			totalTime = trace_data[0]["totalTime"];
			
			if (exp_type != "omp")
			{
				data_num = trace_data.length;
				comm_data.name = trace_data[data_num -1]["name"];
				comm_data.color = trace_data[data_num - 1]["color"];
				comm_data.lineWidth = 1;
				comm_data.data = trace_data[data_num - 1]["data"];
			}

			draw();
			if (exp_type == "omp")
				sel_type ("omp_thread");
			else 
				sel_type ("rank");
		});
	});

	function draw()
	{
		var min=0,max=totalTime;
		var options = {
		    chart: {
		        renderTo:'trace_graph',
		        type: 'scatter',
	            borderWidth:0,
		        zoomType: 'x',
		        marginTop: 50,
		        events: {
					redraw: function(event) {
						min = this.xAxis[0].getExtremes().min;				
						
						if (min == 0)											//set all the datalabels to empty when min =0
						{
							$('.datalabel').each(function(i,label){
								$(this).text("");
							});
						}
					}
				},
		    },
		    title:{text:''},
	    	credits:{enabled:false},
		    tooltip:{
		        formatter:function(){
		        	if (exp_type == "omp")
		        		return "task_id:"+this.series.name;
		        	else
		          		return "func_name:"+this.series.name+"<br/>x:"+this.x+";<br/>y:"+this.y+"<br/>time:"+this.point.time;
		        }
		    },
			plotOptions: {
		        series: {
		            shadow:false,
		            color:'rgba(24,90,169,.75)',
		           	lineWidth:40,
		           	dataLabels: {
	                	align: 'left',
	                	enabled: true,
	                	useHTML:true,
	                	y:5,
	                	formatter: function(event) {
	                		var extremesObject = chart.xAxis[0].getExtremes();
	    					min = extremesObject.min;
	    					max = extremesObject.max;
	    					
	                		if ((((max - min) / this.point.time) < 15) && this.point.x >= min && this.point.x <= max && this.point.tag == 1)
	                			return '<span class="datalabel" id="dl'+this.point.x+'">' + this.series.name + '</span>';
	                		else
	                			return ;
	                	}
	              	},
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
    		if (exp_type == "omp")
    		{
    			if (i == 0)
    			{
    				Y_Categories_thread = temp["thread"];
        			Y_Categories_thread.reverse();
    			}
    			else 
    			{
					var series = {
						data:[]
       	            };
                   	series.name = temp["task_id"];
					if (temp["name"] == "LDMC_Application" || temp["name"] == "main()")
                   		series.lineWidth = 18;
                   	series.data = temp["data"];
                   	options.series.push(series);
    			}
    		}
    		else 
    		{
    			if (i == 0)
        		{
        			if (exp_type == "mpi")
        			{
            			Y_Categories_pid = temp["pid"];
            			Y_Categories_rank = temp["rank"];
            			Y_Categories_pid.reverse();
            			Y_Categories_rank.reverse();
        			}
        			else if (exp_type == "ompi")
        			{
        				Y_Categories_rank = [];
            			$.each (temp["omp_rank"], function(j, omp_rank){
            				Y_Categories_rank.push(j);
            				$.each(omp_rank, function (k, data){
            					Y_Categories_rank.push(j+":"+data);
            				});
            			});
            			Y_Categories_rank.reverse();
        			}
        			else if (exp_type == "cmpi")
        			{
        				Y_Categories_pid = temp["pid"];
            			Y_Categories_rank = temp["rank"];
        			}
        		}
        		else if (i < data_num -1 )
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
    		}
	    });

  		options.plotOptions.series.lineWidth = 15;

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
		else if (value == "rank")
		{
			chart.yAxis[0].setCategories(Y_Categories_rank);
			chart.yAxis[0].setTitle({
				text: 'Rank'
			});
		}
		else if (value == "omp_thread")
		{
			chart.yAxis[0].setCategories(Y_Categories_thread);
			chart.yAxis[0].setTitle({
				text: 'OMP_Thread'
			});
		}
		
	}
	
	function comm_line ()
	{
		if ($("#comm_line").val() == 0)
		{	
			chart.addSeries(comm_data);
			$("#comm_line").val(1);
			$("#comm_line").html ("隐藏通信线");
		}
		else if ($("#comm_line").val() == 1)
		{
			chart.series[chart.series.length - 1].remove();
			$("#comm_line").val(0);
			$("#comm_line").html ("显示通信线");
		}
		
	}
</script>​
</html>