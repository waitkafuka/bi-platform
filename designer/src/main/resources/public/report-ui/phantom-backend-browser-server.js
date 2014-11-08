/**
 * ·在开发机/线上机的phantomjs安装方式是：
 *      (1) 先装jumbo，参见http://jumbo.baidu.com/
 *      (2) 命令行键入
 *              jumbo install phantomjs
 *      等它装好即可。
 * 
 * ·这个文件提供了一个WebServer，用HTTP访问，用报表的url等作为参数。
 *  然后此server会用报表的url去请求报表，并渲染成图片的base64编码，然后用HTTP返回。
 *  这个Server的启动方式是：
 *  命令行键入：
 *      phantomjs phantom-backend-browser-server.js
 *
 * ·然后浏览器中输入URL进行测试:
 *  http://yf-crm-datainsight02.vm.baidu.com:8899/?resultType=html&imgType=png&url=http%3A%2F%2F10.48.29.75%3A8080%2F_report%2FreportTemplate%2Fcomplex%2FgenerateReport.action%3FreportTemplateId%3DPERSISTENT%5E_%5Evirtualdatasource%5E_%5E%5E_%5E-290498121%26os_dim_pos%3D40397
 *
 * ·注意，这个server没有写全，遗留的问题是：
 *      (1) RedHat上没有字体，要装。否则图片中显示不出来字体。具体参见phantomjs官网上的linux安装说明。
 *      (2) 报表加载完的判断，现在随便写了个setTimeout延迟5秒钟，然后认为加载完了可以渲染了。
 *          实际不应该这么写
 *          应该：
 *          <1> 向page注册onCallback响应，在callback时触发onDILoaded：
 *              page.onCallback = onDILoaded;
 *          <2> 在onDomLoaded的时候，向。。。中注册响应函数：
 *                  page.open(
 *                      reportURL, 
 *                      function (status) {
 *                          log('on page loaded');
 *                          page.evaluate(
 *                              // 在这里向DI注册rendered事件，
 *                              // 具体可以会涉及略改DI的layout-page的代码，
 *                              // 提供这种对外的rendered事件注册功能。
 *                              // 这是要注册的rendered事件的响应函数：
 *                              function() { window.callPhantom(); }
 *                          );
 *                      }
 *                  );
 *              这里callPhantom()回触发page的onCallback事件（参见phantomjs官网的API说明），
 *              从而出发刚才注册的onDILoaded。
 *      (3) 需要测试，server长期使用时候的内存情况。
 *      (4) 渲染成静态HTML而非图片，
 *          这里没写，只需在page.renderBase64(...)处，换成var html = page.content; ，就得到了静态HTML。
 */

//======================================
// Constants
//======================================

/**
 * Server的端口设置
 */
var SERVER_PORT = 8899;

//======================================
// HTTP Server
//======================================

var server = require('webserver').create();
var webpage = require('webpage');
var service = server.listen(SERVER_PORT, handleHTTP);
console.log('Server started: ' + service);

/**
 * http响应
 * 
 * @param {string} reportURL 如http://xxxxx/_report/generateReport.action?aaa=1&bbb=asdf，
 *      整体需要经过encodeURIComponent编码
 * @param {string} imgType 可取值为：png（默认）, jpg, gif
 * @param {string} resultType 可取值为：base64（默认）, html
 */
function handleHTTP(request, response) {
    var reportURL = getQueryValue(request, 'url');
    var imgType = getQueryValue(request, 'imgType');
    var resultType = getQueryValue(request, 'resultType');
    var log = getLogger((new Date()).getTime() + '_' + Math.random());

    // 如果是别的请求（如请求favicon.ico）
    if (reportURL == null) {
        response.statusCode = 404;
        response.close();
        return;
    }

    log('Request: reportURL=' + reportURL + ' imgType=' + imgType);

    // 默认值
    imgType = imgType || 'png';
    resultType = resultType || 'base64';

    requestReport(reportURL, imgType, callback, log);

    function callback(result) {
        response.statusCode = 200;
        response.setEncoding('UTF-8');
        if (result.imgBase64 != null) {
            if (resultType == 'base64') {
                writeRspBase64(response, result);
            }
            else if (resultType == 'html') {
                writeRspImgHTML(response, result);
            }
        }
        else {
            writeRspError(response, reportURL);
        }
        response.close();
    }
}

function requestReport(reportURL, imgType, callback, log) {
    // 请求报表，每次使用新的page实例，防止page反复使用，heap越来越大
    var page = webpage.create();
    page.open(reportURL, onDomloaded);

    function onDomloaded() {
        log('on page loaded');
        // FIXME
        // 不应该使用setTimeout，这里只是试验。
        // 见本文件头的说明
        setTimeout(onDILoaded, 15000);
    }

    function onDILoaded() {
        log('on DI loaded');
        var result = {};
        result.imgBase64 = page.renderBase64(imgType);
        log('renderBase64: ' + result.imgBase64);

        callback(result);

        // 关闭page
        // 在phantom api上说，page在close后，不一定能全释放，
        // 这主要发生在page被反复使用的时候。
        // 这里page没有被反复使用，后续观察内存情况，如果真出现内存泄露，
        // 那么就写个定时任务，每天或每周自动重启下这个phantom server吧 ...
        page.close();
        page = null;
    }
}

function writeRspError(response, reportURL) {
    response.write('图片生成失败，请检查URL：' + encodeHTML(reportURL));
}

function writeRspBase64(response, result, imgType) {
    response.write(result.imgBase64);
}

function writeRspImgHTML(response, result, imgType) {
    var html = [];
    html.push(
        '<!DOCTYPE html>',
        '<html>',
            '<head>',
                '<meta http-equiv="Content-type" content="text/html; charset=utf-8" />',
                '<title>Report Image</title>',
            '</head>',
            '<body>',
                '<div>',
                    '这是后端生成的图片：',
                '</div>',
                '<div>',
                    '<img src="data:img/', imgType, ';base64,', result.imgBase64, '" />',
                '</div>',
                '<div>',
                    '这是图片的base64：',
                '</div>',
                '<div sytle="border:1px solid blue; width: 800px; padding: 10px;">',
                    encodeHTML(result.imgBase64),
                '</div>',
            '</body>',
        '</html>'
    );
    response.write(html.join(''));
}

//======================================
// Utils
//======================================

function getLogger(uid) {
    return function log(msg) {
        var prefix = '[DI PHANTOM ' + uid + '] ';
        console.log(prefix + msg);
    }
}

function getQueryValue(request, key) {
    var post = request.post || {};
    var value = post[key];
    if (value == null) {
        value = decodeURIComponent(getQueryValueFromURL(request.url, key));
    }
    return value;
}

function getQueryValueFromURL(url, key) {
    var reg = new RegExp(
            '(^|&|\\?|#)'
                + escapeReg(key) 
                + '=([^&#]*)(&|\x24|#)',
            ''
        );
    var match = url.match(reg);
    if (match) {
        return match[2];
    }
    
    return null;
}

function escapeReg(source) {
    return String(source).replace(
        new RegExp("([.*+?^=!:\x24{}()|[\\]\/\\\\])", "g"), '\\\x241'
    );
}

function encodeHTML(source) {
    return String(source)
                .replace(/&/g,'&amp;')
                .replace(/</g,'&lt;')
                .replace(/>/g,'&gt;')
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
}

