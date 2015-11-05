
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<link href="<c:url value="css/app.css" />" rel="stylesheet" type="text/css">

<title>Access Denied - ProgrammingFree</title>
</head>
<body>
<div class="details">
<h1>You do not have permission to access this page!
</h1>
<form action="/logout" method="post">
          <input type="submit" value="Sign in as different user" /> 
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>   
</div>
</body>
</html>