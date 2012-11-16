<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <link href="../lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
    <link href="../lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
	<link href="../static/css/table.css" rel="stylesheet" type="text/css" />
    <script src="../lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script> 
    <script src="../lib/ligerUI/js/core/base.js" type="text/javascript"></script> 
	<script src="../lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="../lib/ligerUI/js/plugins/ligerSpinner.js" type="text/javascript"></script>
    <script type="text/javascript">
    	var device_info;
    	var grid;
        function itemclick(item)
        {
            alert(item.text);
        }
        $(function ()
        {
        	
            grid = $("#maingrid").ligerGrid({
            	height:'100%',
                columns: [
                	{ display: '设备编号', name: 'DeviceID' },
	                { display: '机房编号', name: 'ServRoom'},
	                { display: '机柜编号', name: 'CabinetID'},
	                { display: '所处位置', name: 'Position'},
	                { display: '设备类型', name: 'DeviceType'},
	                { display: '品牌', name: 'Brand'},
	                { display: 'IP', name: 'IP'},
	                { display: '所属部门', name: 'Department'},
	                { display: '硬件负责人', name: 'respPerson' },
	                { display: '承载业务', name: 'business' },
	                { display: '设备功能', name: 'Function' },
	                { display: '资产编号', name: 'SetID', minWidth: 150 },
	                { display: '其它', name: 'other',minWidth: 150, render: function (row)
	                {
	                	
	                    var h = "<a href='javascript:showDetail(" +row.id+ ")'>详细信息</a> "+
	                    		"<a href='javascript:showTopo(" +row.id+ ")'>拓扑图</a> ";
	                    return h;
	                }
	                },
                ], 
                root: 'Rows',
                record: 'Total',							//记录总数
                width: '100%',
                pageSize:30 ,								//页面大小
                rownumbers:true,
            });
			query();
            $("#pageloading").hide();
        });
       
		function showTopo (row)
		{
			alert (device_info["Rows"][row].id);
		}      	
		function showDetail (row)
		{
			//alert (device_info["Rows"][row].id)
			var table = device_info["Rows"][row].table_name;
			
			var datafrom, datato, highid, lowid, mac, ifHA, memory, cputype, cpucount, diskcount, diskvolume, os, value, interfacetype, deadline;
			var IfTrusteeship, Ifabroad, IfHA, UseStatus, ValueType, FixPattern, PowerPattern, PowerLinePatt;
			
			device_info["Rows"][row]["highID"]==null?highid="无":highid=device_info["Rows"][row]["highID"];
			device_info["Rows"][row]["lowID"]==null?lowid="无":lowid=device_info["Rows"][row]["highID"];
			
			(table!="ServerInfo")?datafrom="—":datafrom=device_info["Rows"][row]["Datafrom"];
			(table!="ServerInfo")?datato="—":datato=device_info["Rows"][row]["Datato"];
			(table!="ServerInfo")?deadline="—":deadline=device_info["Rows"][row]["Deadline"];
			
			(table=="ControlInfo"||table=="MonitorInfo")?mac="—":mac=device_info["Rows"][row]["Mac"];
			(table=="ControlInfo"||table=="MonitorInfo")?ifHA="—":ifHA=device_info["Rows"][row]["IfHA"];
			(table=="ControlInfo"||table=="MonitorInfo")?memory="—":memory=device_info["Rows"][row]["Memory"];
			(table=="ControlInfo"||table=="MonitorInfo")?cputype="—":cputype=device_info["Rows"][row]["CPUType"];
			(table=="ControlInfo"||table=="MonitorInfo")?cpucount="—":cpucount=device_info["Rows"][row]["CPUcount"];
			(table=="ControlInfo"||table=="MonitorInfo"||table=="NetworkInfo")?diskcount="—":diskcount=device_info["Rows"][row]["DiskCount"];
			(table=="ControlInfo"||table=="MonitorInfo"||table=="NetworkInfo")?diskvolume="—":diskvolume=device_info["Rows"][row]["DiskVolume"];
			(table=="ControlInfo"||table=="MonitorInfo")?os="—":os=device_info["Rows"][row]["OS"];
			(table=="MonitorInfo")?value="—":value=device_info["Rows"][row]["Value"];
			(table=="ServerInfo"||table=="StorageInfo")?interfacetype="—":interfacetype=device_info["Rows"][row]["InterfaceType"];
			
			if (device_info["Rows"][row]["IfTrusteeship"] == "0")
				IfTrusteeship = "非托管";
			else if (device_info["Rows"][row]["IfTrusteeship"] == "1")
				IfTrusteeship = "托管";
			else 
				IfTrusteeship = "未知";
				
			if (device_info["Rows"][row]["Ifabroad"] == "0")
				Ifabroad = "国内";
			else if (device_info["Rows"][row]["Ifabroad"] == "1")
				Ifabroad = "国外";
			else 
				Ifabroad = "未知";
			
			if (table!="ControlInfo"||table!="MonitorInfo")
			{
				if (device_info["Rows"][row]["IfHA"] == "0")
					IfHA = "否";
				else if (device_info["Rows"][row]["IfHA"] == "1")
					IfHA = "是";
				else 
					IfHA = "未知";
			}
			
			if (device_info["Rows"][row]["UseStatus"] == "0")
				UseStatus = "在用";
			else if (device_info["Rows"][row]["UseStatus"] == "1")
				UseStatus = "闲置";
			else if (device_info["Rows"][row]["UseStatus"] == "3")
				UseStatus = "损坏";
			else 
				UseStatus = "未知";
				
			if (device_info["Rows"][row]["ValueType"] == "0")
				ValueType = "原价";
			else if (device_info["Rows"][row]["ValueType"] == "1")
				ValueType = "暂估";
			else 
				ValueType = "未知";
				
			if (device_info["Rows"][row]["FixPattern"] == "0")
				FixPattern = "地板";
			else if (device_info["Rows"][row]["FixPattern"] == "1")
				FixPattern = "支架";
			else 
				FixPattern = "未知";
			
			if (device_info["Rows"][row]["PowerPattern"] == "0")
				PowerPattern = "单路";
			else if (device_info["Rows"][row]["PowerPattern"] == "2")
				PowerPattern = "双路";
			else 
				PowerPattern = "未知";
				
			if (device_info["Rows"][row]["PowerLinePatt"] == "0")
				PowerLinePatt = "工业连接器";
			else if (device_info["Rows"][row]["PowerLinePatt"] == "1")
				PowerLinePatt = "插座面板";
			else if (device_info["Rows"][row]["PowerLinePatt"] == "2")
				PowerLinePatt = "微型断路器";
			else 
				PowerLinePatt = "其它";
			
			$("#detail_table").empty();
			
			$("#detail_table").append("<caption>设备详细信息</caption>");

			$("#detail_table").append("<tr>"+
											"<td class='row1'>设备编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["DeviceID"] +"</td>"+
											"<td class='row1'>所属单位</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Department"] +"</td>"+
											"<td class='row1'>管理方式</td>"+
											"<td class='row2'>"+ IfTrusteeship +"</td>"+
											"<td class='row1'>设备类型</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["DeviceType"] +"</td>"+
									  "</tr>");

			$("#detail_table").append("<tr>"+
											"<td class='row1'>设备名称</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["HostName"] +"</td>"+
											"<td class='row1'>设备功能</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Function"] +"</td>"+
											"<td class='row1'>硬件负责人</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["respPerson"] +"</td>"+
											"<td class='row1'>电话</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["HardTele"] +"</td>"+
									  "</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>机房编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ServRoom"] +"</td>"+
											"<td class='row1'>机柜编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["CabinetID"] +"</td>"+
											"<td class='row1'>位置</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Position"] +"</td>"+
											"<td class='row1'>序列号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["DeviceSeries"] +"</td>"+
									 "</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>品牌</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Brand"] +"</td>"+
											"<td class='row1'>国别</td>"+
											"<td class='row2'>"+ Ifabroad +"</td>"+
											"<td class='row1'>型号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Model"] +"</td>"+
											"<td class='row1'>MAC</td>"+
											"<td class='row2'>"+ mac +"</td>"+
									 "</tr>");
	
			$("#detail_table").append("<tr>"+
											"<td class='row1'>IP</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["IP"] +"</td>"+
											"<td class='row1'>承载业务</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["business"] +"</td>"+
											"<td class='row1'>业务负责人</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["busiResPerson"] +"</td>"+
											"<td class='row1'>电话</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["BusiTele"] +"</td>"+
									 "</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>数据来源IP</td>"+
											"<td class='row2'>"+ datafrom +"</td>"+
											"<td class='row1'>数据目标IP</td>"+
											"<td class='row2'>"+ datato +"</td>"+
											"<td class='row1'>上联设备编号</td>"+
											"<td class='row2'>"+ highid +"</td>"+
											"<td class='row1'>下联设备编号</td>"+
											"<td class='row2'>"+ lowid +"</td>"+
									"</tr>");
		
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>设备尺寸</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["DeviceSize"] +"</td>"+
											"<td class='row1'>占据空间</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SpaceDevice"] +"</td>"+
											"<td class='row1'>使用期限</td>"+
											"<td class='row2'>"+ deadline +"</td>"+
											"<td class='row1'>是否HA</td>"+
											"<td class='row2'>"+ ifHA +"</td>"+
									 "</tr>");
	
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>CPU类型</td>"+
											"<td class='row2'>"+ cputype +"</td>"+
											"<td class='row1'>CPU个数</td>"+
											"<td class='row2'>"+ cpucount +"</td>"+
											"<td class='row1'>磁盘容量</td>"+
											"<td class='row2'>"+ diskvolume +"</td>"+
											"<td class='row1'>磁盘个数</td>"+
											"<td class='row2'>"+ diskcount +"</td>"+
									 "</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>物理内存</td>"+
											"<td class='row2'>"+ memory +"</td>"+
											"<td class='row1'>操作系统</td>"+
											"<td class='row2'>"+ os +"</td>"+
											"<td class='row1'>设备重量</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["DeviceWeight"] +"</td>"+
											"<td class='row1'>固定方式</td>"+
											"<td class='row2'>"+ FixPattern +"</td>"+
									 "</tr>");
		
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>承重要求</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["LoadWeight"] +"</td>"+
											"<td class='row1'>工作温度</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["WorkTemp"] +"</td>"+
											"<td class='row1'>工作湿度</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Workhumidity"] +"</td>"+
											"<td class='row1'>散热量</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Heat"] +"</td>"+
									  "</tr>");

			$("#detail_table").append("<tr>"+
											"<td class='row1'>进风量</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Wind"] +"</td>"+
											"<td class='row1'>电源数量</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["PowerNum"] +"</td>"+
											"<td class='row1'>供电方式</td>"+
											"<td class='row2'>"+ PowerPattern +"</td>"+
											"<td class='row1'>电源进线方式</td>"+
											"<td class='row2'>"+ PowerLinePatt +"</td>"+
									 "</tr>");
		
			$("#detail_table").append("<tr>"+
											"<td class='row1'>额定电压</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Vol"] +"</td>"+
											"<td class='row1'>额定电流</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Ampere"] +"</td>"+
											"<td class='row1'>额定功率</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["RatedPower"] +"</td>"+
											"<td class='row1'>零地电压</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["zeroVoltage"] +"</td>"+
										"</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>资产编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SetID"] +"</td>"+
											"<td class='row1'>购买日期</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["LoginDate"] +"</td>"+
											"<td class='row1'>价格</td>"+
											"<td class='row2'>"+ value +"</td>"+
											"<td class='row1'>价格类型</td>"+
											"<td class='row2'>"+ ValueType +"</td>"+
										"</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>状态</td>"+
											"<td class='row2'>"+ UseStatus +"</td>"+
											"<td class='row1'>供应商</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SupplName"] +"</td>"+
											"<td class='row1'>供应商联系人</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SuppCon"] +"</td>"+
											"<td class='row1'>电话</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SuppTele"] +"</td>"+
									"</tr>");
			
			$("#detail_table").append("<tr>"+
											"<td class='row1'>责任人</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["UserPerson"] +"</td>"+
											"<td class='row1'>保修期</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["warranty"] +"</td>"+
											"<td class='row1'>上次维修日期</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["repairDate"] +"</td>"+
											"<td class='row1'>维修次数</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["RepaireTime"] +"</td>"+
									"</tr>");
			
			if (table == "ServerInfo" || table == "StorageInfo"){
				$("#detail_table").append("<tr>"+
											"<td class='row1'>合同编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ConID"] +"</td>"+
									 "</tr>");
			}
			else if (table == "NetworkInfo")
			{
				$("#detail_table").append("<tr>"+
											"<td class='row1'>合同编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ConID"] +"</td>"+
											"<td class='row1'>端口总数</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["PortCount"] +"</td>"+
											"<td class='row1'>空闲端口数</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["FreeCount"] +"</td>"+
											"<td class='row1'>空的槽数</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["SpaceCount"] +"</td>"+
									 "</tr>");
				$("#detail_table").append("<tr>"+
											"<td class='row1'>网络设备编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["NetworkCode"] +"</td>"+
											"<td class='row1'>板卡个数</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["boardCount"] +"</td>"+
											"<td class='row1'>板卡序列号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["BoardSeri"] +"</td>"+
									 "</tr>");
			}
			else if (table == "ControlInfo")
			{
				$("#detail_table").append("<tr>"+
											"<td class='row1'>合同编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ConID"] +"</td>"+
											"<td class='row1'>能连接的设备数目</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ConnectDevice"] +"</td>"+
											"<td class='row1'>空闲端口</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["FreePort"] +"</td>"+
									 "</tr>");
			}
			else if (table == "MonitorInfo")
			{
				$("#detail_table").append("<tr>"+
											"<td class='row1'>合同编号</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["ConID"] +"</td>"+
											"<td class='row1'>分辨率</td>"+
											"<td class='row2'>"+ device_info["Rows"][row]["Resolution"] +"</td>"+	
									 "</tr>");
			}
									 
			$.ligerDialog.open({ target: $("#detail"),width: 800,height:500 });
		}
		function query()
	  	{
	  		var type = $("#type option:selected").val();
	  		var dep = $("#department option:selected").text();
	  		var serverroom = $("#serverroom option:selected").val();
	  		var cabinet = $("#cabinet").val();
	  		var location = $("#location").val();
	  		var resp = $("#resp").val();
	  		var business = $("#business").val();
	  		var ip = $("#ip").val();
	  		var brand = $("#brand").val();
	  		var setid = $("#setid").val();
	  		
	  		$.getJSON ("../servlet/Device_manage?req_type=Getinfo&type="+type+"&dep="+dep+"&servroom="+serverroom+"&cab="+cabinet+"&location="+location+"&resp="+resp+"&bus="+business+"&ip="+ip+"&brand="+brand+"&setid="+setid, function(result){
	            $("#pageloading").show();
	            device_info = result;
	            grid.loadData(result);
	            $("#pageloading").hide();
	  		}) 
	  	}
    </script>
</head>
<body style="overflow-x:hidden; padding:2px;">
<div class="l-loading" style="display:block" id="pageloading"></div>
 <div class="l-clear"></div>
	  <div id="search_box">
  		<span id="type_box"> 
  			设备类型:<select id="type" onchange="changeType()">
	  				<option value="all">全部设备</option>
	  				<option value="ServerInfo">计算设备</option>
	  				<option value="NetworkInfo">网络设备</option>
	  				<option value="StorageInfo">存储设备</option>
	  				<option value="ControlInfo">控制设备</option>
	  				<option value="MonitorInfo">显示设备</option>
	  				<option value="MachineCabinetInfo">机柜设备</option>
	  			</select>
  		</span>
  		
  		
  		<span id="dep_box">
  			单位:<select id = "department" >
	  				<option value="all">全部单位</option>
	  				<option value="run">运控室</option>
	  				<option value="comm">通信台</option>
	  				<option value="compute">计算室</option>
	  				<option value="doc">资料室</option>
	  				<option value="res">研究室</option>
	  				<option value="sys">系统室</option>
	  				<option value="sup">保障室</option>
	  			</select>
  		</span>
  		
  		<span>
	  		机房:<select id = "serverroom">
	  				<option value="all">全部机房</option>
	  				<option value="N2-3F-C">三楼通信机房</option>
	  				<option value="N2-3F-S">三楼存储机房</option>
	  				<option value="N2-2F-N">二楼网络机房</option>
	  				<option value="N2-3F-T">二楼托管机房</option>
	  				<option value="N1-7F">西七楼机房</option>
	  				<option value="N1-2F">电讯机房</option>
	  			</select>
  		</span>
  		<span id="cab_box">机柜编号:<input id = "cabinet"></></span>
  		<span id="loc_box">位置:<input id = "location"></></span>
  		
  	</div>
  	<div>
  		<span id="res_box">硬件负责人:<input id = "resp"></></span>
  		<span id="bus_box">业务系统:<input id = "business"></></span>
  		<span id="ip_box">IP:<input id = "ip"></></span>
  		<span id="brand_box">品牌:<input id = "brand"></></span>
  		<span id="setid_box">资产编号:<input id = "setid"></></span>
  		<span>
  			<button type="button" onclick='query()'>查询</button>
  		</span>
  	</div>
  
  	</div>
	
    <div id="maingrid"></div>
   	<div id = "detail" style="width:800px; margin:3px; display:none;">
   		<table id='detail_table' border=1>
   			
   		</table>
   	</div>
  <div style="display:none;">
  
  
  
  
</div>
 <script type="text/javascript">
  	$(document).ready(function(){
  		
  	});
  	
  	function changeType()
  	{
  		if ($("#type option:selected").text() == "存储设备")
  			$("#ip_box").hide();
  		else if ($("#type option:selected").text() == "显示设备")
  			$("#ip_box").hide();
  		else
  			$("#ip_box").show();
  	
  	
  	}
  	
  </script>
</body>
</html>
