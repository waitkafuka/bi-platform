// 外壳的mockup
(function() {
	
    var ERROR = xmock.data.common.error = {};

	ERROR.notLogin = {
		status : 100,
		statusInfo : '哈哈未登录',
		data : '' 
	}
	
	ERROR.notInSys = {
		status : 104, 
		statusInfo : '哈哈用户不在系统中',
		data : ''
	}
	
})();