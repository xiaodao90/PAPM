<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>并行程序性能监测系统</title>
    
    <script language="JavaScript">
		if (window != top) 
			top.location.href = location.href; 
	</script> 	

    <link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <script src="lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>    
    <script src="lib/ligerUI/js/ligerui.min.js" type="text/javascript"></script> 
    <script src="static/data/indexdata.js" type="text/javascript"></script>
        <script type="text/javascript">
            var tab = null;
            var accordion = null;
            var tree = null;
            $(document).ready(function(){
            	var exp_name = "<%=session.getAttribute("exp_name").toString()%>"
            	if (exp_name != "NULL")
            		$("#exp_name").html(exp_name);
            });
            $(function ()
            {
                //布局
                $("#layout1").ligerLayout({ leftWidth: 190, height: '100%',heightDiff:-34,space:4, onHeightChanged: f_heightChanged });

                var height = $(".l-layout-center").height();

                //Tab
                $("#framecenter").ligerTab({ height: height });
                tab = $("#framecenter").ligerGetTabManager();

                //面板
                $("#accordion1").ligerAccordion({ height: height - 24, speed: null });

                $(".l-link").hover(function ()
                {
                    $(this).addClass("l-link-over");
                }, function ()
                {
                    $(this).removeClass("l-link-over");
                });
                //树
                $("#tree1").ligerTree({
                    data : indexdata,
                    checkbox: false,
                    slide: false,
                    nodeWidth: 120,
                    attribute: ['nodename', 'url'],
                    onSelect: function (node)
                    {
                        if (!node.data.url) return;
                        var tabid = $(node.target).attr("tabid");
                        if (!tabid)
                        {
                            tabid = new Date().getTime();
                            $(node.target).attr("tabid", tabid)
                        }
                        f_addTab(tabid, node.data.text, node.data.url);
                    }
                });
				$("#exp_list").ligerTree({
					data:[
						{text:'实验选择',url:'exp_display.jsp',tabid:101},
						{text:'实验修改',url:'exp_modify.jsp',tabid:102},
						{text:'函数分类',url:'exp_cat.jsp',tabid:103},
						{text:'显示选择',url:'fun_display.jsp', tabid:104}
					],
					checkbox: false,
					onSelect: function (node)
                    {
                    	var exp_name = "<%=session.getAttribute("exp_name").toString()%>";		
						if (exp_name == "NULL" && node.data.tabid != 101)
						{
							exp_name = $("#exp_name").html();
							
							if (exp_name == "无")
							{
								$.ligerDialog.warn('请先选择实验!',"",function (){
									if (!tab.isTabItemExist())
									{
										tab.addTabItem({ tabid : 101,text: "实验选择", url: "exp_display.jsp" });
									}
									else
										tab.selectTabItem(101);
								});
								return ;
							}
						}
                        if (!node.data.url) return;
       
                        f_addTab(node.data.tabid, node.data.text, node.data.url);
                    }
				});
				$("#graph_list").ligerTree({
					data:[
						{text:'树状图',url:'tree.jsp',tabid:201},
						{text:'Trace图',url:'trace.jsp',tabid:202},
						{text:'Profile图',url:'profile.jsp',tabid:203},
						{text:'蜘蛛网图',url:'spider.jsp',tabid:204},
						{text:'饼状图',url:'pie.jsp',tabid:205},
						{text:'proc蜘蛛网图',url:'proc_spider.jsp',tabid:206},
						{text:'proc节点状态图',url:'proc_node_info.jsp',tabid:207},
						{text:'proc节点对比图',url:'proc_node_compare.jsp',tabid:208},
						{text:'proc栈图',url:'proc_stack.jsp', tabid:209},
						{text:'PAPI折线图',url:'papi_line.jsp',tabid:210},
						{text:'通信棋盘图',url:'chess.jsp',tabid:211},
						{text:'通信棋盘图_pv',url:'chess_pv.jsp',tabid:212},
						{text:'分类Trace图',url:'category_trace.jsp',tabid:213},
						{text:'分类Profile图',url:'category_profile.jsp',tabid:214}
						//{text:'trace_highstock', url:'trace_highstock.jsp', tabid:213}
					],
					checkbox: false,
					onSelect: function (node)
                    {
						var exp_name = $("#exp_name").html();
						if (exp_name == "" || exp_name == "无")
                    		exp_name = "<%=session.getAttribute("exp_name").toString()%>";
                    	
						if (exp_name == "NULL")
						{
							$.ligerDialog.warn("请先选择实验!","",function (){
								f_addTab(101, "实验选择", "exp_display.jsp");
							});
							return ;
						}
						
						var tree = $("#tree").text();
						if (tree == "")
							tree = "<%=session.getAttribute("tree").toString()%>";
						
						if (tree == 0)
						{
							if (node.data.tabid == "201")
							{
								$.ligerDialog.confirm("树状图数据不存在,是否要现在进行处理？",function (value){
									var wait_dialog = $.ligerDialog.waitting("处理数据中，请稍候...");
									if (value)
									{
										$.getJSON ("servlet/Trace2Profile?req_type=trace2tree&exp_name="+exp_name, function (result){
											if (result["result"] == "success")
											{
												wait_dialog.close();
												$.ligerDialog.success("数据处理完成");
												f_addTab(node.data.tabid, node.data.text, node.data.url);
											}
											else 
											{
												wait_dialog.close();
												$.ligerDialog.success("数据处理出现错误!");
											}
										});
									}
									else
									{
										wait_dialog.close();
										return ;
									}
									
								});
								return ;
							}				
						}
						
						var profile = $("#profile").text();
						if (profile == "")
							profile = "<%=session.getAttribute("profile").toString()%>";
						if (profile == 0)
						{
							if (node.data.tabid == "203" || node.data.tabid == "204" || node.data.tabid == "205")
							{
								$.ligerDialog.confirm("Profile数据不存在,是否要现在进行处理？",function (value){
									var wait_dialog = $.ligerDialog.waitting("处理数据中，请稍候...");
									if (value)
									{
										$.getJSON ("servlet/Trace2Profile?req_type=trace2profile&exp_name="+exp_name, function (result){
											if (result["result"] == "success")
											{
												wait_dialog.close();
												$.ligerDialog.success("数据处理完成");
												f_addTab(node.data.tabid, node.data.text, node.data.url);
											}
											else 
											{
												wait_dialog.close();
												$.ligerDialog.success("数据处理出现错误!");
											}
										});
									}
									else
									{
										wait_dialog.close();
										return ;
									}
									
								});
								return ;		
							}
							
						}
						
						
						var proc = $("#proc").text();
						if (proc == "")
							proc = "<%=session.getAttribute("proc").toString()%>";
						
						if (proc == 0)
						{
							if (node.data.tabid == "206" || node.data.tabid == "207" || node.data.tabid == "208" || node.data.tabid == "209")
							{
								$.ligerDialog.warn("所选实验无proc数据!");
								return ;
							}
						}
						
						var papi = $("#papi").text();
						if (papi == "")
							papi = "<%=session.getAttribute("papi").toString()%>";
						
						if (papi == 0)
						{
							if (node.data.tabid == "210")
							{
								$.ligerDialog.warn("所选实验无papi数据!");
								return ;
							}
						}
						
						var comm = $("#comm").text();
						if (comm == "")
							comm = "<%=session.getAttribute("comm").toString()%>";
						if (comm == 0)
						{
							if (node.data.tabid == "211" ||node.data.tabid == "212")
							{
								$.ligerDialog.warn("所选实验无通信数据!");
								return ;
							}
						}
						
                        if (!node.data.url) return;
         
                        f_addTab(node.data.tabid, node.data.text, node.data.url);
                    }
				});
				$("#graph_test").ligerTree({
					data:[
						{text:'PAPI列表',url:'protovis/papi_list.jsp',tabid:301},
						{text:'test2',url:'protovis/Parallel Coordinates.jsp',tabid:302},
						{text:'Node-Link Trees',url:'protovis/Node-Link Trees.jsp',tabid:303},
						{text:'matrix', url:'protovis/matrix.jsp',tabid:304},
						{text:'chess', url:'protovis/chess.jsp',tabid:305},
						{text:'trace', url:'trace_highstock.jsp',tabid:306}
					],
					checkbox: false,
                	onSelect: function (node)
                    {
                    	
                        if (!node.data.url) return;
         
                        f_addTab(node.data.tabid, node.data.text, node.data.url);
                    }
				})
                $("#device_list").ligerTree({
                	data:[
                		{text:'查询',url:'device_manage/device_query.jsp',tabid:401},
                		{text:'统计',url:'device_manage/device_statics.jsp',tabid:402},
                		{text:'管理',url:'device_manage/device_manager.jsp',tabid:403}
                	],
                	checkbox: false,
                	onSelect: function (node)
                    {
                    	
                        if (!node.data.url) return;
         
                        f_addTab(node.data.tabid, node.data.text, node.data.url);
                    }
                })
                accordion = $("#accordion1").ligerGetAccordionManager();
                tree = $("#tree1").ligerGetTreeManager();
                
                $("#pageloading").hide();

            });
            function f_heightChanged(options)
            {
                if (tab)
                    tab.addHeight(options.diff);
                if (accordion && options.middleHeight - 24 > 0)
                    accordion.setHeight(options.middleHeight - 24);
            }
            function f_addTab(tabid,text, url)
            { 
            	if (!tab.isTabItemExist(tabid))
				{
					tab.addTabItem({ tabid : tabid,text: text, url: url });
				}
				else
				{
					tab.reload (tabid);
					tab.selectTabItem(tabid);
				}
            }  
     </script> 
<style type="text/css"> 
    body,html{height:100%;}
    body{ padding:0px; margin:0;   overflow:hidden;}  
    .l-link{ display:block; height:26px; line-height:26px; padding-left:10px; text-decoration:underline; color:#333;}
    .l-link2{text-decoration:underline; color:white; margin-left:2px;margin-right:2px;}
    .l-layout-top{background:#102A49; color:White;}
    .l-layout-bottom{ background:#E5EDEF; text-align:center;}
    #pageloading{position:absolute; left:0px; top:0px; background:white url('loading.gif') no-repeat center; width:100%; height:100%;z-index:99999;}
    .l-link{ display:block; line-height:22px; height:22px; padding-left:16px;border:1px solid white; margin:4px;}
    .l-link-over{ background:#FFEEAC; border:1px solid #DB9F00;} 
    .l-winbar{ background:#2B5A76; height:30px; position:absolute; left:0px; bottom:0px; width:100%; z-index:99999;}
    .space{ color:#E7E7E7;}
    /* 顶部 */ 
    .l-topmenu{ margin:0; padding:0; height:80px; line-height:31px; background:url('lib/images/top.png') repeat-x bottom;  position:relative; border-top:1px solid #1D438B;  }
    .l-topmenu-logo{ color:#E7E7E7; height:78px; padding-left:35px; line-height:26px;background:url('static/images/logo_school.png') no-repeat;}
    .l-topmenu-welcome{  position:absolute; height:24px; line-height:24px;  right:30px; top:2px;color:#070A0C; margin-top:30px;}
    .l-topmenu-welcome a{ color:#E7E7E7; text-decoration:underline;} 
 </style>
</head>
<body style="padding:0px;background:#EAEEF5;">  
<div id="pageloading"></div>
<div id="topmenu" class="l-topmenu">
    <div class="l-topmenu-logo"></div>
    <div class="l-topmenu-welcome">
        <a href="#" class="l-link2">欢迎你，<%=session.getAttribute("user")%></a>
        <span class="space">|</span>
		<span style="color:white">当前实验:</span>
        <a href="#" class="l-link2" id="exp_name" value="null">无</a>
        <span class="space">|</span>
        <a href="servlet/LoginCheck?req_type=logout" class="l-link2" >退出</a> 
    </div> 
</div>
  <div id="layout1" style="width:99.2%; margin:0 auto; margin-top:4px; "> 
        <div position="left"  title="主要菜单" id="accordion1"> 
			<div title="实验列表" class="l-scroll">
				<ul id="exp_list" style="margin-top:3px;"></ul>
			</div>
			<div title="图表展示" class="l-scroll">
				<ul id="graph_list" style="margin-top:3px"></ul>
			</div>
			<div title="图表测试" class="l-scroll">
				<ul id="graph_test" style="margin-top:3px"></ul>
			</div>
			<div title="设备管理" class="l-scroll">
				<ul id="device_list" style="margin-top:3px"></ul>
			</div>
			<div title="功能列表" class="l-scroll">
				<ul id="tree1" style="margin-top:3px;"></ul>
			</div>           	
        </div>
        <div position="center" id="framecenter"> 
            <div tabid="home" title="我的主页" style="height:300px" >
                <iframe frameborder="0" name="home" id="home" src="welcome.htm"></iframe>
            </div> 
        </div> 
        
    </div>
    <div style = "display:none">
    	<div id="proc"></div>
    	<div id="papi"></div>
    	<div id="comm"></div>
    	<div id="profile"></div>
    	<div id="tree"></div>
    </div>
    <div  style="height:32px; line-height:32px; text-align:center;">
            Copyright © 2011-2012 北航系统结构所
    </div>
    <div style="display:none"></div>
</body>
</html>
