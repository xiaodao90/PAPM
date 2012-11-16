<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <link href="lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" /> 
    <link href="lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />

    <script src="lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script> 
    <script src="lib/ligerUI/js/core/base.js" type="text/javascript"></script> 
	<script src="lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="lib/ligerUI/js/plugins/ligerSpinner.js" type="text/javascript"></script>


    <script type="text/javascript">
        function itemclick(item)
        {
            alert(item.text);
        }
        $(document).ready(function()
        {
        	var exp_type = "<%=session.getAttribute("exp_type").toString()%>";
        	if (exp_type == "mpi")
        	{
        		window['g'] =
                $("#maingrid").ligerGrid({
                    height:'100%',
                    columns: [
                    { display: 'Rank号', name: 'rank' },
                    { display: '进程号', name: 'pid',editor: { type: 'int' }},
                    { display: '函数编号', name: 'eid', minWidth: 140, editor: { type: 'int' }},
                    { display: '函数名', name: 'name', editor: { type: 'text' } },
                    { display: '类型', name: 'type',editor: { type: 'int' } },
                    { display: '通信域', name: 'comm', editor: { type: 'text' } },
                    { display: '时间', name: 'time', editor: { type: 'int' } },
                    { display: '结束标志', name: 'finish', editor: { type: 'int' } }
                    ], 
                    root: 'Rows',								
                    record: 'Total',							//记录总数
                    dataAction :'server',
                    url:"servlet/Get_exp_info?req_type=get_exp_data",
                    pageSize:30 ,								//页面大小
                    rownumbers:true,
                    enabledEdit: true,
                    toolbar: {
                    	items: [
    		                { text: '增加', click: addRow, icon: 'add' },
    		                { line: true },
    		                { text: '修改', click: itemclick, icon: 'modify' },
    		                { line: true },
    		                { text: '删除', click: deleteRow, img: 'lib/ligerUI/skins/icons/delete.gif' }
                    	]
                    },
                    onSelectRow: function (rowdata, rowindex)
                    {
                        $("#txtrowindex").val(rowindex);
                    }
                });
        	}
        	else if (exp_type == "ompi")
        	{
        		window['g'] =
                $("#maingrid").ligerGrid({
                    height:'100%',
                    columns: [
                    { display: 'MPI Rank', name: 'mpi_rank' },
                    { display: 'OMP Rank', name: 'omp_rank'},
                    { display: '进程号', name: 'pid',editor: { type: 'int' }},
                    { display: '函数编号', name: 'eid', minWidth: 140, editor: { type: 'int' }},
                    { display: '函数名', name: 'name', editor: { type: 'text' } },
                    { display: '类型', name: 'type',editor: { type: 'int' } },
                    { display: '通信域', name: 'comm', editor: { type: 'text' } },
                    { display: '时间', name: 'time', editor: { type: 'int' } },
                    { display: '结束标志', name: 'finish', editor: { type: 'int' } }
                    ], 
                    root: 'Rows',								
                    record: 'Total',							//记录总数
                    dataAction :'server',
                    url:"servlet/Get_exp_info?req_type=get_exp_data",
                    pageSize:30 ,								//页面大小
                    rownumbers:true,
                    enabledEdit: true,
                    toolbar: {
                    	items: [
    		                { text: '增加', click: addRow, icon: 'add' },
    		                { line: true },
    		                { text: '修改', click: itemclick, icon: 'modify' },
    		                { line: true },
    		                { text: '删除', click: deleteRow, img: 'lib/ligerUI/skins/icons/delete.gif' }
                    	]
                    },
                    onSelectRow: function (rowdata, rowindex)
                    {
                        $("#txtrowindex").val(rowindex);
                    }
                });
        	}
        	else if (exp_type == "cmpi")
        	{
        		window['g'] =
                $("#maingrid").ligerGrid({
                    height:'100%',
                    columns: [
                    { display: 'MPI Rank', name: 'mpi_rank' },
                    { display: '进程号', name: 'pid',editor: { type: 'int' }},
                    { display: '函数编号', name: 'eid', minWidth: 140, editor: { type: 'int' }},
                    { display: '函数名', name: 'name', editor: { type: 'text' } },
                    { display: '类型', name: 'type',editor: { type: 'int' } },
                    { display: '通信域', name: 'comm', editor: { type: 'text' } },
                    { display: '时间', name: 'time', editor: { type: 'int' } },
                    { display: '结束标志', name: 'finish', editor: { type: 'int' } }
                    ], 
                    root: 'Rows',								
                    record: 'Total',							//记录总数
                    dataAction :'server',
                    url:"servlet/Get_exp_info?req_type=get_exp_data",
                    pageSize:30 ,								//页面大小
                    rownumbers:true,
                    enabledEdit: true,
                    toolbar: {
                    	items: [
    		                { text: '增加', click: addRow, icon: 'add' },
    		                { line: true },
    		                { text: '修改', click: itemclick, icon: 'modify' },
    		                { line: true },
    		                { text: '删除', click: deleteRow, img: 'lib/ligerUI/skins/icons/delete.gif' }
                    	]
                    },
                    onSelectRow: function (rowdata, rowindex)
                    {
                        $("#txtrowindex").val(rowindex);
                    }
                });
        	}
            $("#pageloading").hide();
        });
		
        function deleteRow()
        {
            g.deleteSelectedRow();
        }
		function addRow()
		{
		    g.addRow();
		} 
		
    </script>
</head>
<body style="overflow-x:hidden; padding:2px;">
<div class="l-loading" style="display:block" id="pageloading"></div>
 <a class="l-button" style="width:120px;float:left; margin-left:10px; display:none;" onclick="deleteRow()">删除选择的行</a>

 
 <div class="l-clear"></div>

    <div id="maingrid"></div>
   
  <div style="display:none;">
  
</div>
 
</body>
</html>
