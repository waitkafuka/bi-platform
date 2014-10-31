//var sys = require("sys");
var sys = require("util")
var http = require("http");
var grunt = require("grunt");
// var resolve = require('resolve').sync;
var basedir = process.cwd();

http.createServer(function(request, response) {
 //   response.sendHeader(200, {"Content-Type": "text/html"});
    response.writeHeader(200, {"Content-Type": "text/html"});
    response.write(basedir);
    response.write("Hello World!12e3");
    // response.write(writeObj(grunt.cli.options));
    response.write(grunt.tasks(['aaa:rr:tt:qq']), null, function () {response.write("donnnnnnnnnnnnnnn");});
    // response.write("" + grunt.tasks);
    // grunt.cli.tasks = ['aaa:rr:tt:qq'];
    // response.write(grunt.cli());
 //   response.close();
    response.end();

}).listen(9000);

sys.puts("Server running at http://localhost:8080/");



    /**
     * 打印对象到控制台
     */
    function writeObj(obj) {
        var arr = [];
        try {
            arr.push(JSON.stringify(obj));
        }
        catch (e) {
            for (var i in obj) {
                if (obj.hasOwnProperty(i) && typeof obj[i] != 'function') {
                    arr.push(i + ': ' + obj[i]);
                }
            }
        }
        return arr.join(' <br /> ');
    }

