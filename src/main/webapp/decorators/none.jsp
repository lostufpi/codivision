<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><fmt:message key="app.name"/></title>
	<link href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">
	<link href="<c:url value="/css/main.css" />" rel="stylesheet">
	<sitemesh:write property='head'/>
</head>

<body style="margin:0; padding:0;">
	<script src="<c:url value="/vendor/jquery/jquery.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap-filestyle.min.js" />"></script>
	
	<sitemesh:write property='body'/>
	
</body>
</html>