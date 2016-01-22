<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mobile Inspections</title>
</head>
<body>
	<%!	/* variable and method declarations */ 
	Date currentDate = new Date();

	Date getDate() {
		System.out.println("In getDate() method");
		return currentDate;
	}

	void setDate() {
		System.out.println("In setDate() method");
		currentDate = new Date();
	}%>

	<center>
		<h2>Mobile Inspections</h2>
	</center>

	<br> The time is now <%=new java.util.Date()%> <br> 
	
</body>
</html>
