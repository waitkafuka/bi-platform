<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>1：新建平面报表模版</title>
</head>
<body>
	<p>该功能仅提供给测试阶段使用，数据源默认为测试环境dataInsight数据源</p>
	<p>此测试页面不提供数据源选择</p>
	<p></p>
	<form action="${ctx}/reportTemplate/planeTable/test/create.action"
		method="post">
		<table border="1">
			<thead>
				<tr>
					<td><b>参数</b></td>
					<td width="200px"><b>值</b></td>
				</tr>
			</thead>

			<tbody>
				<tr>
					<td><b>模版id(填了为修改，不填为新建)</b></td>
					<td><input type="text" style="width: 600px" name="reportTemplateId" value="${reportTemplateId}"/></td>
				</tr>
				<tr>
					<td><b>模版名</b></td>
					<td><input type="text" style="width: 600px" name="templateName" value="${templateName}"/></td>
				</tr>
				<tr>
					<td><b>模版sql</b></td>
					<td><textarea name="sqlString"
							style="height: 200px; width: 600px">${sqlString}</textarea></td>
				</tr>
			</tbody>

		</table>
		<input type="submit" value="保存" />


	</form>

</body>
</html>