$(function() {
	Date.prototype.format = function (fmt) { // author: meizz
	    var o = {
	        "M+": this.getMonth() + 1, // 月份
	        "d+": this.getDate(), // 日
	        "h+": this.getHours(), // 小时
	        "m+": this.getMinutes(), // 分
	        "s+": this.getSeconds(), // 秒
	        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
	        "S": this.getMilliseconds() // 毫秒
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	};
});

/**
 * 通用ajax调用
 * 
 * @param _url
 * @param _type
 * @param _async
 * @param _cache
 * @param _data
 * @returns {ok: true/false, data: xx}
 */
function _ajax(_url, _type, _async, _cache, _data) {
	var rst = {};
	rst.ok = false;
	$.ajax({
		url : _url,
		type : _type, // GET
		async : _async, // 或false,是否异步
		cache : _cache,
		data : _data,
		timeout : 5000, // 超时时间
		dataType : 'json',
		success : function(data, textStatus, jqXHR) {
			rst.ok = true;
			rst.data = data;
		},
		error : function(xhr, textStatus) {
			console.log('ajax error:' + textStatus);
			rst.msg = 'AJAX INTERNAL ERROR';
		}
	});
	return rst;
}

var Common = {
		alert:function(params){
	        var alertModal = $("#alertModal");
	        alertModal.find(".title").html(params.title);
	        alertModal.find(".message").html(params.message);
	        alertModal.modal('show');
	    },
	    confirm:function(params){
	        var model = $("#confirmModal");
	        model.find(".title").html(params.title);
	        model.find(".message").html(params.message);
	        $("#common_confirm_btn").click();
	        // 每次都将监听先关闭，防止多次监听发生，确保只有一次监听
	        model.find(".cancel").off("click");
	        model.find(".ok").off("click");
	        model.find(".ok").on("click",function(){
	            params.operate(true)
	        });

	        model.find(".cancel").on("click",function(){
	            params.operate(false)
	        });
	        model.modal('show');
	    }
	}

function _alert(_msg) {
	Common.alert({
	      title: '提示',
	      message: _msg,
	  });
}

function _confirm(_title, _msg, _callback) {
	Common.confirm({
	      title: _title,
	      message: _msg,
	      operate: function (reselt) {
	          if (reselt) {
	        	  _callback.call(this);
	          } else {
	        	  	// nothing
	          }
	      }
	  });
}
