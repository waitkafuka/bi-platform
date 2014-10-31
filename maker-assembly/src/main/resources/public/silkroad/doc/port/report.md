# 报表交互接口 #

## 新建/修改报表 ##

### 获取报表列表

请求路径

    GET:reports

后台模拟

    {
       "status": 0,
       "statusInfo": "",
       "data": [
           {
               "id": 1,
               "name": ""
           }
       ]
    }

### 获取某一报表

请求路径

    GET:reports/[id]

后台模拟

    {
       "status": 0,
       "statusInfo": "",
       "data": {
           "id": 1,
           "name": ""
           ...待续
       }
    }

### 发布报表

请求路径

    GET:reports/[id]/publish

后台模拟

    {
       "status": 0,
       "statusInfo": "",
	   "data": "http://baidu.com"
    }

### 保存报表

请求路径

    POST:reports/[id]

前台模拟

    vm: ''
    json: ''

后台模拟

    {
       "status": 0,
       "statusInfo": "",
	   "data": {}
    }
### 保存json与vm报表

请求路径

    POST:reports/[id]/json_vm

前台模拟

    vm: ''
    json: ''

后台模拟

    {
       "status": 0,
       "statusInfo": "",
	   "data": {}
    }

### 预览报表

请求路径

    GET:reports/[id]/preview_info

后台模拟

    {
       "status": 0,
       "statusInfo": "",
	   "data": "http://baidu.com"
    }
	
### 删除一报表

请求路径

    DELETE:reports/[id]

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

### 复制一报表(创建报表副本)

请求路径

    POST:reports/[id]/duplicate   // id:被复制报表的id；name:新报表的名称

前台模拟

    name: "报表名称"

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

### 新建报表（第一步：命名）

请求路径

    POST:/reports/

前端模拟

    name: "name"

后端模拟

    {
        "status": 0, 
        "statusInfo": "提示信息", 
        "data": {
            "id": 1
        }
    }

### 增加报表数据模型的cube （第二步：建cube）

请求路径

     POST:/reports/[id]/start_models

前端模拟

    "datasourcesId": 1,
    "selectedTables": [id1,id2], // 选中表
    "regexps":["regepx123","regepx2344", "rege456"] //正则表达式  

后端模拟

    {
        "status": 0, 
        "statusInfo": "提示信息"
    }

// TODO 战统定维度设置等接口

### 获取报表所用数据源的选中的表的列表 ###

请求路径

    GET:/reports/[id]/start_models

后台模拟数据

    {
        "status": 0,
        "statusInfo": "加载完成",
        "data": {
            "dsId": 1,
            "factTables": [
                {
                    "id": 1,
                    "name": "table_1"
                },
                {
                    "id": 1,
                    "name": "table_1"
                },
                {
                    "id": 1,
                    "name": "table_2_0"
                },
                {
                    "id": 1,
                    "name": "table_2_1"
                },
                {
                    "id": 1,
                    "name": "table_2_x"
                },
                {
                    "id": 1,
                    "name": "table_3_0"
                },
                {
                    "id": 1,
                    "name": "table_3_2"
                }
            ],
            "prefixs": [
                "table_2",
                "table_3"
            ]
        }
    }

### 更改衍生指标 ###

 请求路径

      PUT:reports/[id]/cubes/[id]/derive-inds

 前台模拟

     data = [
         {
             name: "ACP",
             expr: "点击（click）   /  消费（csm）"
         },
         {
             name: "R",
             expr: "现金（cash）/  消费（csm）"
         }
     ];

 后台模拟

     {
         "status": 0,
         "statusInfo": ""
     }

## 编辑报表的数据模型 ##

### 获取cube列表 ###

请求路径

    GET:/reports/[id]/cubes

后台模拟数据

    {
        "data": [
            {
                "id": 1,
                "name": "cube1"
            },
            {
                "id": 1,
                "name": "表2"
            }
        ],
        "status": 0,
        "statusInfo": ""
    }

### 获取特定cube的指标列表 ###

请求路径

    GET:reports/[id]/cubes/[id]/inds

后台模拟

    {
        "data": [
            {
                "id": 1,
                "caption": "原始指标", // 指标的汉字标识
                "name": "english_name", // 对应字段的英文名
                "type": "COMMON", // "COMMON":原始指标；"CAL":衍生指标; "DEFINE":用户自定义
                "aggregator": "SUM", // 汇总方式 SUM,COUNT,AVREAGE
                "visible": true, // 是否可见，true：不可见，false：可见
                "canToDim": 0 // 是否可作为维度，false：不可以，true：可以
            }
        ],
        "status": 0,
        "statusInfo": ""
    }

### 获取指定cube的维度列表 ###

请求路径

    GET:reports/[id]/cubes/[id]/dims

后台模拟

    {
        "data": {
            "dimList": [
                {
                    "id": 1,
                    "caption": "原始维度",   // 维度的汉字标识
                    "name": "english_name", // 对应字段的英文名
                    "type": "STANDARD_DIMENSION",
                    "canToInd": 0 // 是否可作为指标，false：不可以，true：可以
                },
                {
                    "id": 1,
                    "caption": "",
                    "type": "GROUP_DIMENSION",
                    "levels": [
                        {
                            "id": 1,
                            "name": "",
                            "caption": "english_name", // 对应字段的英文名
                            "type": 0                  // 0：原生，1：维度组
                        }
                    ]
                }
            }
        ],
        "status": 0,
        "statusInfo": ""
    }

### 获取特定cube的信息 ###

请求路径

    GET:reports/[id]/cubes/[id]

后台模拟

    // 原始指标
    "oriInd": [
        {
            "id": 1,
            "visible": 0 // 0:不选中，1：选中
        }
    ],
    // 原生维度（）
    "oriDim": [
        {
            "id": 1,
            "visible": 0 // 0:不选中，1：选中
        }
    ]

### 修改“筛选显示数据”的数据 ###

请求路径

    PUT:reports/[id]/cubes/[id]

前台模拟

    "oriInd": [
        {
            "id": 1,
            "visible": 0 // 0:不选中(不可见)，1：选中(可见)
        }
    ],
    // 原生维度（）
    "oriDim": [
        {
            "id": 1,
            "selected": 0 // 0:不选中，1：选中
        }
    ]

### 设置指标汇总方式

请求路径

    PUT:reports/[id]/cubes/[id]/inds/[id]

前台数据模拟

    aggregator: "1" 

后台数据模拟

    {
        "status": 0,
        "statwusInfo": ""
    }

### 修改指标名称
    
请求路径

    PUT:reports/[id]/cubes/[id]/inds/[id]

前台数据模拟

    name: ""

后台数据模拟

    {
        "status": 0,
        "statusInfo": ""
    }

### 修改维度,维度组 的名称

请求路径

    PUT:reports/[id]/cubes/[id]/dims/[id]

前台数据模拟

    name: ""

后台数据模拟

    {
        "status": 0,
        "statusInfo": ""
    }
     
### 指标转换到维度
 
请求路径

    PUT:reports/[id]/cubes/[id]/ind_to_dim/[id]

前台数据模拟

    // 可能将来要做的功能参数
    dimId: 1 // 如果拖到维度组有此参数
    prevDimId: 2  // 前一个维度的id，如果是第一个值为-1 

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }
     
### 维度转换到指标
 
请求路径

      PUT:reports/[id]/cubes/[id]/dim_to_ind/[id]

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

### 维度拖到维度组

请求路径

      POST:reports/[id]/cubes/[id]/dim-groups/[dim-group-id]/dim

前台模拟

    dimId: 1

后台模拟

    {
       "status": 0,
       "statusInfo": "",
       "data": {
           "canToInd": true
       }
    }

### 对维度组中的维度排序

请求路径
     
      POST:reports/[id]/cubes/[cubeId]/dim_groups/[dimGroupId]/dim_sorting

前台模拟

    groupId:5         // 维度组id
    dimId:1           // 维度id
    beforeDimId:-1    // 落点的前一个维度的id，如果被拖到第一个此值为-1

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

### 删除维度组中的维度

请求路径

     DELETE:reports/[id]/cubes/[id]/dim-groups/[dim-group-id]/dims/[dim-id]

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

### 删除维度组

请求路径
       
    DELETE:reports/[id]/cubes/[id]/dim-groups/[id]  

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    } 

### 创建维度组

请求路径

    POST:reports/[id]/cubes/[id]/dim-groups

前端模拟

    name: "维度组名"

后台模拟

    {
       "status": 0,
       "statusInfo": ""
    }

## 报表编辑时的数据交互（画布区组件的设置与修改）

### 获取一个报表的json

请求路径

    GET:/reports/{reportId}/json
    
后台模拟

    {
       "status": 0,
       "statusInfo": ""
       "data": ""
    }

### 获取一个报表的vm

请求路径

    GET:/reports/{reportId}/vm

后台模拟

    {
       "status": 0,
       "statusInfo": ""
       "data": ""
    }

### 添加一个组件

请求路径

    POST: reports/[report_id]/extend_area/
    
前台模拟
    
    type: "TABLE"
    
后台模拟

    {
        "status": 0,
        "statusInfo": "",
        "data": {
            "id": "oo-ss-34-9"
        }
    }

### 获取某一组件的数据配置

请求路径

    GET: reports/[report_id]/extend_area/[extend_area_id]

后端模拟

    {
        "status": 0,
        "statusInfo": "",
        "data":{
            "xAxis": [
                {
                    "id": "",
                    "caption": "",
                    "name": "",
                    "cubeId": "",
                    "olapElementId": "", // 维度或指标的id
                }
            ],
            "yAxis": [],
            "sAxis": []
        }
    }

### 添加某一组件的数据项

请求路径
    
    POST: reports/[report_id]/extend_area/[extend_area_id]/item

前台模拟

    cubeId: "",
    oLapElementId: "", // 维度或指标的id
    axisType: "x" // x,s

后台模拟

     {
         "status": 0,
         "statusInfo": "",
         "data":{
             "id": "",
             "caption": "",
             "name": "",
             "cubeId": "",
             "oLapElementId": "" // 维度或指标的id
         }
     }

### 删除某一组件的数据配置项

请求路径

    DELETE: reports/[report_id]/extend_area/[extend_area_id]/item/[id]

前台模拟

    axisType: "x" // x,s

后台模拟

    {
        "status": 0,
        "statusInfo": ""
    }
    
### 调整数据配置项顺序

请求路径

    POST: reports/[report_id]/extend_area/[areaId]/item_sorting  //areaId 组件ID

前台模拟

    type: 'x'          // 轴类型，只有x、y周
    source: 1          // 起始位，第一个为0
    target: 0          // 落点的位置，第一个为0

后台模拟

    {
        "status": 0,
        "statusInfo": ""
    }