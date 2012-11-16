<html>
  <head>
    <title>棋盘图</title>
    <link rel="stylesheet" href="static/css/liger.css" type="text/css"/>
    <script type="text/javascript" src="static/jquery.js"></script>
    <script type="text/javascript" src="static/protovis.min.js"></script>
    <script type="text/javascript" src="static/FileSaver.min.js"></script>
    <script type="text/javascript" src="static/BlobBuilder.js"></script>
    <style type="text/css">
		#fig {
		  width: 480px;
		  height: 480px;
		}
		
		.title, .subtitle {
		  font-size: 16px;
		  font-weight: bold;
		  padding-bottom: 10px;
		}
		
		.subtitle {
		  float: right;
		  color: #999;
		}
		
		.footer {
		  padding-top: 10px;
		  text-align: right;
		}
		body {
		  margin: 0;
		  display: table;
		  height: 100%;
		  width: 100%;
		  font: 14px/134% Helvetica Neue, sans-serif;
		}
		
		#center {
		  display: table-cell;
		  vertical-align: middle;
		}
		
		#fig {
		  position: relative;
		  margin: auto;
		}
    </style>
</head>
<body>
	<div id="head">
		<h6 style='font-size:14px;width:auto;display:inline;margin-left:15px'>通信棋盘图</h6>
		
		<button id="export" onclick="download()" style="float:right">下载</button>
	</div>

	<div id="center" style="padding-top:40px;margin-left:20px">
		<form align="center">
			<div id="fig"></div>
		</form>
	</div>
	
</body>

<script type="text/javascript+protovis">

	var dataURL = "servlet/Protovis_chart?req_type=get_chess_data";
 	
	var JSONdata = $.ajax({ type: "GET", url: dataURL,
        async: false }).responseText;
	var data = JSON.parse(JSONdata);
	
	var chess_data = data["data"];
	var max = data["max"];
	var min = data["min"];
	var matric ;
	var multiple = 1;

	if (max <1024)
	{
		matric = "Bytes";
		multiple = 1;
	}
	else if (max >=1024 && max < 1024000)
	{
		matric = "KB";
		multiple = 1024;
	}	
	else if (max >= 1024000)
	{
		matric = "MB";
		multiple = 1024000;
	}
	/* Convert from tabular format to array of objects. */
	var cols = chess_data.shift();
	chess_data = chess_data.map(function(d) pv.dict(cols, function() d[this.index]));
	cols.shift();

	/* The color scale ranges 3 standard deviations in each direction. */
	var x = pv.dict(cols, function(f) pv.mean(chess_data, function(d) d[f])),
		s = pv.dict(cols, function(f) pv.deviation(chess_data, function(d) d[f])),
	fill = pv.dict(cols, function(f) pv.Scale.linear()
        .domain(0, max)
        .range("#1A71ED", "#B00807"));

	/* The cell dimensions. */
	
	var w = 380/chess_data.length, h = 380/chess_data.length;

	var vis = new pv.Panel()
		.width(cols.length * w)
		.height(chess_data.length * h + 45)
		.top(30)
		.left(100)
		.canvas('fig');
	
	var gap = chess_data.length * h / 1024;
	
	vis.add(pv.Bar)
    	.data(pv.range(0, 1, 1/1024))
		.left(-75)
		.width(30)
    	.bottom(function() (this.index * gap+45))
    	.height(gap+1)
    	.fillStyle(pv.Scale.linear(0, 1).range("#1A71ED", "#B00807"))
		.title(function() this.index / (1024*multiple) * max+ matric)

	vis.add(pv.Panel)
		.data(cols)
		.left(function() this.index * w)
		.width(w)
		.add(pv.Panel)
		.data(chess_data)
		.top(function() (chess_data.length - this.index - 1) * h )
		.height(h)
		.fillStyle(function(d, f) fill[f](d[f]))
		.strokeStyle("white")
		.lineWidth(1)
		.antialias(false)
		.title(function(d, f) d.Name + " to " + f + ": " + d[f]/multiple + " " +matric);

	vis.add(pv.Label)
		.data(cols)
		.left(function() this.index * w )
		.bottom(30)
		.textBaseline("middle")
		.text(function() this.index)
		.font("16px sans-serif");

	vis.add(pv.Label)
		.data(chess_data)
		.top(function() this.index * h + h / 2)
		.left(0)
		.textAlign("right")
		.textBaseline("middle")
		.text(function() chess_data.length - this.index - 1)
		.font("16px sans-serif");

	vis.render();
	$('#export').click(function() {
			
		var bb = new BlobBuilder();

		bb.append("<svg width=\"500\" height=\"500\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
		bb.append($('#fig').html());
		
		var blob = bb.getBlob("application/svg+xml;");
		saveAs(blob,"<%=session.getAttribute("exp_name").toString()%>" + "_ChessChart.svg");
	});
</script>

</html>