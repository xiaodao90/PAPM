<html>
<head>
	<script src="static/jquery.js"></script>
</head>
<body>
	<div id="main">
		<div id="image">
			<form align="center">
				<img alt="chess Chart" src="servlet/Get_chart?type=chess&format=png" >
			</form>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#header #sel").css('width',parseInt($("#header").css('width'))-140+"px");//to decide the postion of #sel
		});
	</script>
</body>
</html>