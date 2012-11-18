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
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>Trace图</h6>
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
		$("#head").append("<button id='comm_line' class='button' style='float:right;display:inline;margin-right:10px;' value=0 onclick='comm_line()'>显示通信线</button>");
	}
	else
	{
		$("#head").append("<button id='disMode' class='button' style='float:right;display:inline;margin-right:10px;' value=0 onclick='disMode()'>显示函数</button>");
	}
	
	var totalTime = 0;
 	var totalNum;
 	var data_num;
 	var traceData;
 	var chart;
 	var Y_Categories;
 	var Y_title;
 	
 	if (exp_type == "mpi")
 		Y_title = "MPI_Rank";
 	else if (exp_type == "ompi")
 		Y_title = "OMPI_Rank";
 	else if (exp_type == "omp")
 		Y_title = "OMP_Thread";
 	else 
 		Y_title = "MPI_Rank";
 	
 	var comm_data = {
       	data:[]
	};
 	
	$(document).ready(function(){
		$.getJSON("servlet/highchart?req_type=get_trace_data",function(result){
			traceData = result;
	
			totalNum = traceData[0]["num"];
			totalTime = traceData[0]["totalTime"];
			
			if (exp_type != "omp")
			{
				data_num = traceData.length;
				comm_data.name = traceData[data_num -1]["name"];
				comm_data.color = traceData[data_num - 1]["color"];
				comm_data.lineWidth = 1;
				comm_data.data = traceData[data_num - 1]["data"];
			}
			
			draw ();
		});
	});

	function draw()
	{
		var min=0, max=totalTime;
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
		        		return "task_id:"+this.series.name+"<br/>TASK_State:"+this.point.status+"<br/>Fun_name:"+this.point.funName;
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
		                radius:2,
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
		    	minorTickInterval: 'auto',
     			maxPadding: 0.2,
            	gridLineWidth: 2,
            	tickWidth:1,
	            tickLength:5,
	            tickColor:'#ccc',
	            tickmarkPlacement:'on',
		    	categories: [],
		        title:{
		            text:Y_title
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
		
    	$.each (traceData,function(i,temp)
    	{
    		if (exp_type == "omp")
    		{
    			if (i == 0)
    			{
    				Y_Categories = temp ["thread"];
        			Y_Categories.reverse();
        			options.yAxis.categories = Y_Categories;
    			}
    			else 
    			{
					var series = {
						data:[]
       	            };
                   	series.name = temp ["task_id"];
                   	series.color = temp["color"];
                   	series.data = temp ["data"];
                   	options.series.push(series); 
    			}
    		}
    		else 
    		{
    			if (i == 0)
        		{
        			if (exp_type == "mpi")
        			{
            			Y_Categories = temp["rank"];
            			Y_Categories.reverse();
            			options.yAxis.categories = Y_Categories;
        			}
        			else if (exp_type == "ompi")
        			{
        				Y_Categories_rank = [];
            			$.each (temp["omp_rank"], function(j, omp_rank){
            				Y_Categories.push(j);
            				$.each(omp_rank, function (k, data){
            					Y_Categories.push(j+":"+data);
            				});
            			});	
            			Y_Categories.reverse();
            			options.yAxis.categories = Y_Categories;
        			}
        			else if (exp_type == "cmpi")
        			{
            			Y_Categories = temp["rank"];
            			options.yAxis.categories = Y_Categories;
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

    	options.yAxis.max = totalNum;

    	if (totalNum < 4)
    		options.chart.height = 250;
    	else if (totalNum >= 4 && totalNum <= 8)
    		options.chart.height = 400;
    	else if (totalNum > 8 && totalNum <= 12)
    		options.chart.height = 550;
    	else
    		options.chart.height = 35 * totalNum + 50;
    	chart = new Highcharts.Chart(options);
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
	
	function disMode ()
	{
		if ($("#disMode").val() == 0)
		{
			$("#disMode").val(1);
			$("#disMode").html ("显示函数");
		}
		else
		{
			$("#disMode").val(1);
			$("#disMode").html ("显示任务");
		}
	}
</script>​
</html>