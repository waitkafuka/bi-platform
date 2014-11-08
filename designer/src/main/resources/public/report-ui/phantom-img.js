var page = require('webpage').create();
var fs = require('fs');

page.open(
    // 'http://localhost/page-sample/console.html', 
    'http://www.baidu.com',
    function () {
        setTimeout(
            function () {
                fs.write('dipng.png', page.render('png'), 'w');
                fs.write('dipng.txt', page.renderBase64('png'), 'w');
                console.log(page.content);
                phantom.exit();
            },
            3000
        );
    }
);
