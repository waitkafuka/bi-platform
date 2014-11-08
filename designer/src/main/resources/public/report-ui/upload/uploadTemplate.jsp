<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%
String[] param = (String[])request.getParameterMap().get("_s_idcard");
String sidcard;
if (param == null || param[0] == null) {
    sidcard = "NoBizKey";
}else{
	sidcard = param[0];
}
%>
<body>
	<form action="uploadTemplate.action?_s_idcard=<%=sidcard %>" method="post" enctype ="multipart/form-data" runat="server"> 
		<input id="templateFile" runat="server" name="templateFile" type="file" />
		<p><input type="radio" name="fileType" id="fileType" value="ZIP">Zip File</p>
		<p><input type="radio" name="fileType" id="fileType" value="FILE" checked>Template File</p>
		<input type="submit" name="Upload" value="上传模板" id="Upload" />
	</form>
</body>