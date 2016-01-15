<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Spring 4 MVC - HelloWorld Index Page</title>
</head>
<body>
	<%!	/* variable and method declarations */
 		/* Note: Now that you know how to do this -- it is in general not a good idea to use variables as
 		shown here.	The JSP usually will run as multiple threads of one single instance. Different threads
 		would interfere with variable access, because it will be the same variable for all of them. If you
 		do have to use variables in JSP, you should use synchronized access, but that hurts the performance.
 		In general, any data you need should go either in the session object or the request object 
 		(these are introduced a little later) if passing data between different JSP pages. Variables 
 		you declare inside scriptlets are fine, e.g. < % int i = 45; % >   because these are declared inside 
 		the local scope and are not shared. */ 
 		
	Date theDate = new Date();

	Date getDate() {
		System.out.println("In getDate() method");
		return theDate;
	}

	void setDate() {
		System.out.println("In setDate() method");
		theDate = new Date();
	}%>

	<center>
		<h2>mada mada dane</h2>

		<h3>
			<a href="hello?name=Goku">Click Here</a>
		</h3>
	</center>

	<br> The time is now
	<%=new java.util.Date()%>
	<br>
	<%@ include file="hello_included.jsp"%>
	<jsp:include page="hello_included.jsp"/>
	<!-- jsp:forward: me reenvia a otro jsp -->
	<!--jsp:forward page="hello_included.jsp"/-->
	<br>

	<TABLE BORDER=2>
		<%
			int n = 5;

			for (int i = 0; i < n; i++) {
		%>
		<TR>
			<TD>Number</TD>
			<TD><%=i + 1%></TD>
		</TR>
		<%
			}
		%>
	</TABLE>

	<br>getDate: <%= getDate() %>
	<br>setDate  <% setDate(); %>
	<br>getDate: <%= getDate() %>
	<br>
	
</body>
</html>
