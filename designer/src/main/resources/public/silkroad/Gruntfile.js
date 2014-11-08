/**
 * grunt
 * Copyright 2012 Baidu Inc. All rights reserved.
 *
 * @file:    构建程序
 * @author:  lizhantong(lztlovely@126.com)
 */

module.exports = function (grunt) {
    start();
    function start() {
            grunt.initConfig({
                pkg: grunt.file.readJSON("repo-conf.json"),
                prop: (prop ={
                    dirDep: "../dep",
                    dirAst: "../asset",
                    dirSrc: "src",
                    dirSrcCore: "core",
                    dirSrcCommon: "common",
                    dirBusisDataSource: "src/data-source",
                    dirBusisNav: "src/nav"
                }),
                srcDir: prop.srcDir,
                destDir: prop.dirAst,
                requirejs: {
                    "main": {
                        "options": {
                            "baseUrl": prop.dirSrc,
                            "paths": {

                            },
                            "include": [
                                "enter",
                                "index"
                            ],
                            "out": "asset/di-console-mock.js"
                        }
                    }
                }
        });
        grunt.loadNpmTasks("grunt-contrib-requirejs");
        grunt.registerTask("build", taskRebuildBiz);
    }

    function taskRebuildBiz() {
        var requirejs = grunt.config.get("requirejs");
        var pkg = grunt.config.get("pkg");
        grunt.log.writeln('参数' + JSON.stringify(pkg))
        var options = requirejs.main.options;
        options.paths = pkg.enter;
        grunt.config.set("requirejs",requirejs)
        grunt.log.writeln('参数' + JSON.stringify(grunt.config.get("requirejs")))
        //运行任务
        grunt.task.run(['requirejs']);
    }

    function extend(sourceObj, tartgetObj) {
        for(var key in sourceObj) {
            if (key && typeof key === 'string') {
                tartgetObj[key] = sourceObj[key];
            } else {
                arguments.callee(sourceObj[key], tartgetObj);
            }
        }
    }
};