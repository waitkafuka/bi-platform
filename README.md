业内领先的Holap敏捷BI分析平台，提供高性能、准实时、可扩展的、一站式的BI建模、分析平台。

	如何快速构建BI－Platform
		构建前提：需在指定环境中安装maven 3 以上、java8
	
	构建流程：
		1. clone项目到本地指定目录
		2. 分别在common、tesseract、maker-assembly目录下执行 mvn install构建项目
		3 找到common构建后的jar文件，执行java －jar 指定端口和有效文件路径，启动文件服务器， 如：
			java -jar common-0.0.1-SNAPSHOT.jar 9090 /tmp/ > log/commons.log &
		4.找到tesseract构建后的jar文件，执行java －jar 启动执行引擎， 如：
			java -jar tesseract-0.0.1-SNAPSHOT.jar > log/ter.log & 
		5.找到maker-assembly构建后的jar文件，执行java －jar 启动建模工具, 如：
			java -jar maker-assembly-0.0.1-SNAPSHOT.jar --server.port=8999 > log/maker-assembly.log &

