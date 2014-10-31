<%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-store"); 
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control", "must-revalidate");
response.setDateHeader("Expires",0);
request.setAttribute("ctx", request.getContextPath());
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
