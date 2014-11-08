<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>4：显示可执行sql</title>
</head>
<script type="text/javascript">
	function showQueryData2() {
		showQueryData.action = '${ctx}/reportTemplate/planeTable/test/showQueryData2.action';
		showQueryData.submit();
	}
	function showQueryData3() {
		showQueryData.action = '${ctx}/reportTemplate/planeTable/test/showQueryData3.action';
		showQueryData.submit();
	}
</script>
<body>
	<p>真实sql</p>
	<p></p>
	<form id="showQueryData" name="showQueryData"
		action="${ctx}/reportTemplate/planeTable/test/showQueryData.action"
		method="post">
		<c:forEach items="${paramMap}" var="obj">
			<input type="hidden" name="${obj.key}" value="${obj.value}" />
		</c:forEach>

		<table border="1">
			<thead>
				<tr>
					<td><b>参数</b></td>
					<td><b>值</b></td>
				</tr>
			</thead>

			<tbody>
				<tr>
					<td><b>模版id</b></td>
					<td>${reportTemplateId}</td>
				</tr>
				<tr>
					<td><b>解析完sql</b></td>
					<td><textarea name="sqlString" readonly="readonly"
							style="height: 200px; width: 600px">${sqlString}</textarea></td>
				</tr>
			</tbody>

		</table>
		<input type="submit" value="执行sql1" /> 
		<input type="button" value="执行sql2" onclick="showQueryData2()">
		<input type="button" value="执行sql3(显示json格式数据)" onclick="showQueryData3()">
	</form>

</body>
</html>