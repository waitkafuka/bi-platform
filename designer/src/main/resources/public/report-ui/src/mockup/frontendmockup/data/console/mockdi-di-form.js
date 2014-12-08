// 外壳的mockup
(function() {
    
    var NS = xmock.data.console.DIForm = {};

    function random () {
        return Math.round(Math.random() * 10000000);
    }

    var o;
           
    /**
     * 表单提交
     */
    NS.FORM_DATA = function(url, options) {
        var re = {
            status: 0,
            message: 'OKOKmsg',
            data: {
                params: {
                    // select控件
                    'ProdLine^_^product_line': {
                        datasource: [
                            { text: 'asdf', value: 1},
                            { text: '范德萨的', value: 2, selected: 0 },
                            { text: '旅游', value: 3 },
                            { text: '打三分的', value: 4 },
                            { text: '四谛法收到', value: 5 },
                            { text: 'tggtg', value: 16 },
                            { text: '建研集团有', value: 13 },
                            { text: '个人股 ', value: 54 },
                            { text: '个人股 ', value: 541 },
                            { text: '个人股 ', value: 542 },
                            { text: '个人股 ', value: 543 },
                            { text: '个人股 ', value: 545 },
                            { text: '个人股 ', value: 546 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5411 },
                            { text: '个人股 ', value: 5422 },
                            { text: '个人股 ', value: 5433 },
                            { text: '个人股 ', value: 5444 },
                            { text: '个人股 ', value: 5455 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5488 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 }
                        ],
                        value: [2]
                    },
                    // tree
                    'Pos': {
                        datasource: {
                            text: '北京分公司',
                            value: (o = Math.random()),
                            isLeaf: false //,
                            // children: [
                                // { text: 'asdfasdf', value: 444444 },
                                // { text: 'asdfasdf', value: 444444 }                                
                            // ]
                        },
                        value: o
                    },
                    'ProductLine': {
                        datasource: [
                            { text: '网页', value: 1},
                            { text: '范德萨的' + Math.random(), value: 2, selected: 0 },
                            { text: '旅游', value: 3 },
                            { text: '打三分的', value: 4 },
                            { text: '四谛法收到', value: 5 },
                            { text: 'tggtg', value: 16 },
                            { text: '建研集团有', value: 13 },
                            { text: '个人股 ', value: 54 },
                            { text: '个人股 ', value: 541 },
                            { text: '个人股 ', value: 542 },
                            { text: '个人股 ', value: 543 },
                            { text: '个人股 ', value: 545 },
                            { text: '个人股 ', value: 546 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5411 },
                            { text: '个人股 ', value: 5422 },
                            { text: '个人股 ', value: 5433 },
                            { text: '个人股 ', value: 5444 },
                            { text: '个人股 ', value: 5455 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5488 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 }
                        ]
                    },
                    'Trade': {
                        datasource: [
                            { text: '网页', value: 1},
                            { text: '范德萨的' + Math.random(), value: 2, selected: 0 },
                            { text: '旅游', value: 3 },
                            { text: '打三分的', value: 4 },
                            { text: '四谛法收到', value: 5 },
                            { text: 'tggtg', value: 16 },
                            { text: '建研集团有', value: 13 },
                            { text: '个人股 ', value: 54 },
                            { text: '个人股 ', value: 541 },
                            { text: '个人股 ', value: 542 },
                            { text: '个人股 ', value: 543 },
                            { text: '个人股 ', value: 545 },
                            { text: '个人股 ', value: 546 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5411 },
                            { text: '个人股 ', value: 5422 },
                            { text: '个人股 ', value: 5433 },
                            { text: '个人股 ', value: 5444 },
                            { text: '个人股 ', value: 5455 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5488 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 }
                        ],
                        value: [3]
                    },
                    'ISPos': {
                        value: { asdf: 1212 }
                    }                    
                }
            }
        };

        var d = re.data.params['ProdLine^_^product_line'].datasource;
        d = d.slice(0, Math.round(Math.random() * 5));

        return re;
    };

    /**
     * 表单异步取数据提交
     */
    NS.FORM_ASYNC_DATA = function(url, options) {
        var re = {
            status: 0,
            message: 'OKOKmsg',
            data: {
                params: {
                    // tree
                    'Pos': {
                        datasource: {
                            text: '我是节点',
                            value: Math.random(),
                            children: [
                                {
                                    text: '我是节点',
                                    value: Math.random()/*,
                                    children: [
                                        {
                                            text: 'tyerty',
                                            value: Math.random()
                                        },
                                        {
                                            text: 'tyerty',
                                            value: Math.random(),
                                            isLeaf: true
                                        }
                                    ]*/
                                },
                                {
                                    text: '北京分公司',
                                    value: Math.random(),
                                    isLeaf: true
                                },
                                {
                                    text: '同上',
                                    value: Math.random()
                                },
                                {
                                    text: '同上',
                                    value: Math.random()
                                },
                                {
                                    text: '我超长啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊',
                                    value: Math.random()
                                }        
                            ]
                        }
                    },
                    'Sugg': {
                        datasource: [
                            { text: 'as萨芬的撒的f', value: 1234 },
                            { text: 'as萨芬的撒的f', value: 124 },
                            { text: 'as萨芬的撒的f', value: 12334 },
                            { text: 'as萨芬的撒的f', value: 12344 },
                            { text: 'as萨芬的撒的f', value: 12345 },
                            { text: 'as萨芬的撒的f', value: 12364 },
                            { text: 'as萨芬的撒的f', value: 12374 },
                            { text: 'as萨芬的撒的f', value: 1234 },
                            { text: 'as萨芬的撒的f', value: 12384 },
                            { text: 'as萨芬的撒的f', value: 12394 },
                            { text: 'as萨芬的撒的f', value: 123904 },
                            { text: 'as萨芬的撒的f', value: 123434 },
                            { text: 'as萨芬的撒的f', value: 12334 }
                        ]
                    },
                    'ProdLine^_^product_line': {
                        datasource: [
                            { text: 'asdf' + Math.random(), value: 1},
                            { text: '范德萨的' + Math.random(), value: 2, selected: 0 },
                            { text: '旅游', value: 3 },
                            { text: '打三分的', value: 4 },
                            { text: '四谛法收到', value: 5 },
                            { text: 'tggtg', value: 16 },
                            { text: '建研集团有', value: 13 },
                            { text: '个人股 ', value: 54 },
                            { text: '个人股 ', value: 541 },
                            { text: '个人股 ', value: 542 },
                            { text: '个人股 ', value: 543 },
                            { text: '个人股 ', value: 545 },
                            { text: '个人股 ', value: 546 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5411 },
                            { text: '个人股 ', value: 5422 },
                            { text: '个人股 ', value: 5433 },
                            { text: '个人股 ', value: 5444 },
                            { text: '个人股 ', value: 5455 },
                            { text: '个人股 ', value: 5466 },
                            { text: '个人股 ', value: 5477 },
                            { text: '个人股 ', value: 5488 },
                            { text: '阿瓦分发谁干的发生的圣达菲', value: 23 }
                        ]
                    }
                }
            }
        };

        var d = re.data.params['ProdLine^_^product_line'].datasource;
        d = d.slice(0, Math.round(Math.random() * 5));

        return re;
    };

})();