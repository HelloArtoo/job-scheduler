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
	_confirm("提示", "确认要<font color='red'>" + msg + "</font>该作业吗？", trigger);
}

function trigger() {
	var params = getSelectJobNameParam();
	if (params == null || params == '') {
		_alert('请先选中一行数据!');
		return;
	}

	var url = getOperationUrl();
	if(url==''){
		_alert('失败，未知的操作类型');
		return;
	}
	
	$.post(url, params, function() {
		table.draw();
		_alert('操作成功');
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
	} else {
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

	popTable = $('#job-server-detail-table').DataTable({
		searching : true,
		paging : false,
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
		} ]
	});
	$('#jobServerDetail').modal('show');
}

function initExecGrid(params) {
	if (popTable != null)
		popTable.destroy();

	popTable = $('#job-exec-detail-table').DataTable({
		searching : true,
		paging : false,
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
