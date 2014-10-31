<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>2：选择列映射设置</title>
</head>
<body>
	<p>列映射</p>
	<p></p>
	<form action="${ctx}/reportTemplate/planeTable/test/saveColumns.action"
		method="post">
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
					<td><input type="text" style="width: 600px" name="reportTemplateId" value="${reportTemplateId}"/></td>
				</tr>
				<tr>
					<td><b>选择列映射参数（json格式）</b></td>
					<td><textarea name="columnsJson"
							style="height: 200px; width: 600px">${columnsJson}</textarea></td>
				</tr>
			</tbody>

		</table>
		<input type="submit" value="保存" />


	</form>

</body>
</html>