var table;
var popTable;
var type;
$(document).ready(function() {
	// GRID
	initGrid();
	// START STOP PAUSE
	initJobOperations();
	// jobConfig
	initJobConfSave();
	// BUTTONS
	initJobBtn();
	initServerBtn();
	initExeBtn();
});

// TODO
function initJobConfSave() {
	$('#saveJobConf').click(function() {
		_alert('该功能哥还没空开发，现在仅当个查看使用。');
	});
}

function initJobOperations() {
	$('#triggerBtn').click(function() {
		type = 'trigger';
		doOperation('trigger');
	});
	$('#pauseBtn').click(function() {
		type = 'pause';
		doOperation('pause');
	});
	$('#resumeBtn').click(function() {
		type = 'resume';
		doOperation('resume');
	});
}

function doOperation(type) {
	var msg = getConfirmMsg(type);
	_confirm("提示", "确认要<font color='red'>" + msg + "</font>全部节点吗？", trigger);
}

function trigger() {
	var jobName = $('#serverJobName').val();
	if (jobName == null || jobName == '') {
		_alert('获取作业异常请刷新页面!');
		return;
	}
	
	var params = {};
	params.jobName = jobName;
	var url = getOperationUrl();
	if (url == '') {
		_alert('失败，未知的操作类型');
		return;
	}

	$.post(url, params, function() {
		reloadPopTable();
		_alert('操作完成');
	});
}

function getOperationUrl() {
	if (type == 'trigger') {
		return "job/triggerAll/name";
	} else if (type == 'pause') {
		return "job/pauseAll/name";
	} else if (type == 'resume') {
		return "job/resumeAll/name";
	}

	return '';
}

function getConfirmMsg(type) {
	var msg = "";
	if (type == 'trigger') {
		msg = '触发';
	} else if (type == 'pause') {
		msg = '暂停';
	} else if(type=='resume') {
		msg = '恢复';
	}
	return msg;
}

// job-server-detail-table
function initExeBtn() {
	$('#exeBtn').click(function() {
		var params = getSelectJobNameParam();
		if (params == null || params == '') {
			_alert('请先选中一行数据!');
			return;
		}
		initExecGrid(params);
	});
}

function initServerBtn() {
	$('#serverBtn').click(function() {
		var params = getSelectJobNameParam();
		if (params == null || params == '') {
			_alert('请先选中一行数据!');
			return;
		}
		$('#serverJobName').val(params.jobName);
		initServersGrid(params);
	});
}

function initJobBtn() {
	$('#jobBtn').click(function() {
		var params = getSelectJobNameParam();
		if (params == null || params == '') {
			_alert('请先选中一行数据!');
			return;
		}

		var rst = _ajax("/job/settings", "GET", false, false, params);
		if (rst.ok) {
			showJobDetail(rst.data);
		} else {
			_alert('内部异常!')
		}
	});
}

function showJobDetail(data) {
	setIdValueTitle('jobName', data.jobName);
	setIdValueTitle('jobType', data.jobType);
	setIdValueTitle('jobClass', data.jobClass);
	$('#jobClass').val("com.jd..." + subStrProperties(data.jobClass));
	$('#jobClass').attr('title', data.jobClass);
	setIdValueTitle('cron', data.cron);
	setIdValueTitle('segmentTotalCount', data.segmentTotalCount);
	setIdValueTitle('segmentItemParameters', data.segmentItemParameters);
	setIdValueTitle('jobParameter', data.jobParameter);
	setIdValueTitle('maxTimeDiffSeconds', data.maxTimeDiffSeconds);
	setIdValueTitle('monitorPort', data.monitorPort);
	setIdValueTitle('jobSegmentStrategyClass', data.jobSegmentStrategyClass);
	setIdValueTitle('description', data.description);
	setIdValueTitle('reconcileIntervalMinutes', data.reconcileIntervalMinutes);
	$('#executorServiceHandler').val(
			subStrProperties(data.jobProperties.executor_service_handler));
	$('#executorServiceHandler').attr('title',
			data.jobProperties.executor_service_handler);
	$('#exceptionHandler').val(
			subStrProperties(data.jobProperties.job_exception_handler));
	$('#exceptionHandler').attr('title',
			data.jobProperties.job_exception_handler);

	$('#monitorExecution').val(data.monitorExecution);
	$('#streamingProcess').val(data.streamingProcess);
	$('#failover').val(data.failover);
	$('#misfire').val(data.misfire);
	$('#jobDetail').modal('show');
}

function subStrProperties(str) {
	str = str.substring(str.lastIndexOf('.') + 1);
	return str;
}

function setIdValueTitle(id, value) {
	$('#' + id + '').val(value);
	$('#' + id + '').attr('title', value);
}

function initServersGrid(params) {
	if (popTable != null)
		popTable.destroy();

	popTable = $('#job-server-detail-table')
			.DataTable(
					{
						searching : true,
						paging : true,
						bLengthChange : false,
						language : {
							url : "/resources/js/dataTables/lang/Chinese.lang"
						},
						ajax : "job/servers?jobName=" + params.jobName,
						columns : [ {
							"data" : "jobName"
						}, {
							"data" : "ip"
						}, {
							"data" : "hostName"
						}, {
							"data" : "status"
						}, {
							"data" : "segment"
						} ],
						columnDefs : [ {
							// 定义操作列
							"targets" : 5,// 操作按钮目标列
							"data" : null,
							"render" : function(data, type, row) {
								
								return getEditHtml(data);
							}
						} ]
					});
	$('#jobServerDetail').modal('show');
}

/**
 * 行内编辑按钮
 * @param data
 * @returns
 */
function getEditHtml(data){
	var params = JSON.stringify(data);
	//SHUTDOWN
	if(data.status=='SHUTDOWN'){
		return "<small class='text-danger'>需重启作业恢复</small>";
	}
	
	//CRASHED
	if(data.status=='CRASHED'){
		return "<a href='javascript:void(0);'  class='btn btn-danger btn-xs' title='只有停止运行的作业才能删除(主节点选举中无法移除)' onclick='removeSingleIp("
		+ params + ")'> 移除</a>";
	}
	
	var html = "<a href='javascript:void(0);'  class='btn btn-primary btn-xs' title='手动触发作业执行' onclick='triggerSingleIp("
			+ params + ")'> 触发</a>&nbsp;&nbsp;";
	if(data.status=='PAUSED'){
		html += "<a href='javascript:void(0);' class='down btn btn-success btn-xs' title='恢复被暂停的作业' onclick='resumeSingleIp("
			+ params + ")'> 恢复</a>&nbsp;&nbsp;";
	}else{
		html += "<a href='javascript:void(0);' class='btn btn-info btn-xs' title='暂停作业的执行' onclick='pauseSingleIp("
			+ params + ")'> 暂停</a>&nbsp;&nbsp;";
	}
	
	if(data.status=='DISABLED'){
		html += "<a href='javascript:void(0);' class='btn btn-success btn-xs' title='启用被禁用的作业' onclick='enableSingleIp("
			+ params + ")'> 启用</a>&nbsp;&nbsp;";
	}else{
		html += "<a href='javascript:void(0);' class='btn btn-warning btn-xs' title='禁用作业，不停止进程，会重新进行分段' onclick='disableSingleIp("
			+ params + ")'> 禁用</a>&nbsp;&nbsp;";
	}
	
	html += "<a href='javascript:void(0);' class='btn btn-danger btn-xs' title='关闭作业会关闭运行的作业，杀死进程' onclick='shutSingleIp("
		+ params + ")'> SHUT</a>";
	return html;
}

function enableSingleIp(serverInfo){
	var url = "job/enable";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function removeSingleIp(serverInfo){
	var url = "job/remove";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function disableSingleIp(serverInfo){
	var url = "job/disable";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function shutSingleIp(serverInfo){
	var url = "job/shutdown";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function triggerSingleIp(serverInfo) {
	var url = "job/trigger";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function pauseSingleIp(serverInfo) {
	var url = "job/pause";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

function resumeSingleIp(serverInfo) {
	var url = "job/resume";
	$.post(url, serverInfo, function() {
		_alert('操作完成');
		reloadPopTable();
	});
}

//刷新弹出table
function reloadPopTable(){
	popTable.ajax.reload();
}


//刷新table
function reloadTable(){
	table.ajax.reload();
}

function initExecGrid(params) {
	if (popTable != null)
		popTable.destroy();

	popTable = $('#job-exec-detail-table').DataTable({
		searching : true,
		paging : true,
		language : {
			url : "/resources/js/dataTables/lang/Chinese.lang"
		},
		ajax : "job/execution?jobName=" + params.jobName,
		columns : [ {
			"data" : "item"
		}, {
			"data" : "status"
		}, {
			"data" : "failoverIp"
		}, {
			"data" : "lastBeginTimeStr"
		}, {
			"data" : "nextFireTimeStr"
		}, {
			"data" : "lastCompleteTimeStr"
		} ]
	});
	$('#jobExecDetail').modal('show');
}

function initGrid() {
	table = $('#job-detail-table').DataTable({
		searching : false,
		bLengthChange : false,
		language : {
			url : "/resources/js/dataTables/lang/Chinese.lang"
		},
		ajax : "job/jobs",
		// ajax : "/resources/js/web/objects.txt",
		columns : [ {
			"data" : "jobName"
		}, {
			"data" : "status"
		}, {
			"data" : "cron"
		}, {
			"data" : "description"
		} ]
	});

	$('#job-detail-table tbody').on('click', 'tr', function() {
		if ($(this).hasClass('success')) {
			$(this).removeClass('success');
		} else {
			table.$('tr.success').removeClass('success');
			$(this).addClass('success');
		}
	});
}

function getSelectJobNameParam() {
	var row = table.row('.success').data();
	if (row != null && row != '' && row != 'undefined') {
		var data = {};
		data.jobName = row.jobName;
		return data;
	}
	return '';
}
