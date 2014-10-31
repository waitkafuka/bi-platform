// 外壳的mockup
(function() {
    
    var isArray = xutil.lang.isArray;

    function random () {
        return Math.round(Math.random() * 10000000);
    }

    var GET_DEFAULT_IMAGENAME = function (url, options) {
        return {
                data: {
                	 defaultImageName: 'testImageName',
                },
                "status": 0,
                "statusInfo": ""
        };
    };


    var SAVE = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {}
        };
    };

    var CLEAR = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {}
        };
    };
    
    xmock.data.RTPL_CLONE_GET_DEFAULT_IMAGENAME = GET_DEFAULT_IMAGENAME;
    xmock.data.RTPL_CLONE_SAVE = SAVE;
    xmock.data.RTPL_CLONE_CLEAR = CLEAR;

})();