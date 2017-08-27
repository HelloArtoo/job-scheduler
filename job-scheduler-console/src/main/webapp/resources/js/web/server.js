var table;
var popTable;
$(document).ready(function() {
	initGrid();
	initServerJobsBtn();
});

function initGrid() {
	table = $('#server-detail-table').DataTable({
		searching : false,
		bLengthChange : false,
		language : {
			url : "/resources/js/dataTables/lang/Chinese.lang"
		},
		ajax : "server/servers",
		// ajax : "/resources/js/web/objects.txt",
		columns : [ {
			"data" : "serverIp"
		}, {
			"data" : "serverHostName"
		}, {
			"data" : "status"
		} ]
	});

	$('#server-detail-table tbody').on('click', 'tr', function() {
		if ($(this).hasClass('success')) {
			$(this).removeClass('success');
		} else {
			table.$('tr.success').removeClass('success');
			$(this).addClass('success');
		}
	});
}

function initServerJobsBtn() {
	$('#jobConfBtn').click(function() {
		var params = getSelectServerIpParam();
		if (params == null || params == '') {
			_alert('请先选中一行数据!');
			return;
		}
		initServerJobsGrid(params);
	});
}

function getSelectServerIpParam(){
	var row = table.row('.success').data();
	if (row != null && row != '' && row != 'undefined') {
		var data = {};
		data.serverIp = row.serverIp;
		return data;
	}
	return '';
}

function initServerJobsGrid(params) {
	if (popTable != null)
		popTable.destroy();

	popTable = $('#server-jobs-detail-table').DataTable({
		searching : true,
		paging : false,
		language : {
			url : "/resources/js/dataTables/lang/Chinese.lang"
		},
		ajax : "server/jobs?ip=" + params.serverIp,
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
	$('#serverJobsDetail').modal('show');
}