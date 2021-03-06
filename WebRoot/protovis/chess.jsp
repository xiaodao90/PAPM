<html>
  <head>
    <title>NBA per-game performance of top 50 scorers</title>
    <script type="text/javascript" src="../static/protovis.min.js"></script>
    <script type="text/javascript" src="../static/data/nba.js"></script>
    <style type="text/css">

#fig {
  width: 580px;
  height: 740px;
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
  <body><div id="center"><div id="fig">
    <div class="subtitle">2008-2009 season</div>
    <div class="title">NBA per-game performance of top 50 scorers</div>
    <script type="text/javascript+protovis">

/* Convert from tabular format to array of objects. */
var cols = nba.shift();
nba = nba.map(function(d) pv.dict(cols, function() d[this.index]));
cols.shift();

/* The color scale ranges 3 standard deviations in each direction. */
var x = pv.dict(cols, function(f) pv.mean(nba, function(d) d[f])),
    s = pv.dict(cols, function(f) pv.deviation(nba, function(d) d[f])),
 fill = pv.dict(cols, function(f) pv.Scale.linear()
        .domain(0, 100)
        .range("white", "red"));

/* The cell dimensions. */
var w = 24, h = 13;

var vis = new pv.Panel()
    .width(cols.length * w)
    .height(nba.length * h)
    .top(30)
    .left(100);

vis.add(pv.Panel)
    .data(cols)
    .left(function() this.index * w)
    .width(w)
  .add(pv.Panel)
    .data(nba)
    .top(function() this.index * h)
    .height(h)
    .fillStyle(function(d, f) fill[f](d[f]))
    .strokeStyle("white")
    .lineWidth(1)
    .antialias(false)
    .title(function(d, f) d.Name + "'s " + f + ": " + d[f]);

vis.add(pv.Label)
    .data(cols)
    .left(function() this.index * w + w / 2)
    .textAngle(-Math.PI / 2)
    .textBaseline("middle");

vis.add(pv.Label)
    .data(nba)
    .top(function() this.index * h + h / 2)
    .textAlign("right")
    .textBaseline("middle")
    .text(function(d) d.Name);

vis.render();

    </script>
    <div class="footer">
      Source: <a href="http://flowingdata.com/2010/01/21/how-to-make-a-heatmap-a-quick-and-easy-solution/">FlowingData</a><br>
    </div>
  </div></div></body>
</html>