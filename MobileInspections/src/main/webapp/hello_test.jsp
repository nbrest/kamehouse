<html>
<body>
jsp mada mada dane <br>

<%= /* java expression here */ true %> 
<%  /* java block here      */ java.util.Date dateini = new java.util.Date(); System.out.println(dateini.toString()); %>

<br> The time is now <%= new java.util.Date() %>

<br>
<br> Java string: <%= new String("pegasus ryu sei ken") %>
<br>
 
<br> Get system properties: java.version: <%= System.getProperty("java.version") %>
<br>                        java.home: <%= System.getProperty("java.home") %>
<br>                        os.name: <%= System.getProperty("os.name") %>
<br>                        user.name: <%= System.getProperty("user.name") %>
<br>                        user.home: <%= System.getProperty("user.home") %>
<br>                        user.dir: <%= System.getProperty("user.dir") %>
<br> 

<br> 
<%
    // This is a scriptlet.  Notice that the "date"
    // variable we declare here is available in the
    // embedded expression later on.
    System.out.println( "Evaluating date now" );
    java.util.Date date1 = new java.util.Date();
%>
Hello!  The time is now <%= date1 %>
<br>  

<br>  
<%
    // This scriptlet declares and initializes "date"
    System.out.println( "Evaluating date now" );
    java.util.Date date2 = new java.util.Date();
%>
Hello!  The time is now
<%
    // This scriptlet generates HTML output
    out.println( String.valueOf( date2 ));
%>
<br>  
<%
	// request and response variables 
    out.println( "<BR>Your machine's address is " );
    out.println( request.getRemoteHost());
    //response.sendRedirect( "https://www.nicobrest.com.ar" );
%>
<br>
<TABLE BORDER=2>
<%
    int n = 5;
    
    for ( int i = 0; i < n; i++ ) {
        %>
        <TR>
        <TD>Number</TD>
        <TD><%= i+1 %></TD>
        </TR>
        <%
    }
%>
</TABLE>
<br>
<%
    Boolean hello = true;
    
    if ( hello ) {
        %>
        <P>Hello, world
        <%
    } else {
        %>
        <P>Goodbye, world
        <%
    }
%>
<br>
<%
	    java.util.Properties sysProperties = System.getProperties();
	    java.util.Enumeration enumSysProperties =  sysProperties.propertyNames();

	    while (enumSysProperties.hasMoreElements()) {
	      String key = (String) enumSysProperties.nextElement();
	      out.println(key + " : " + sysProperties.getProperty(key));
	      %><br><%
	    }
%>

<br><br>
HTML end
</body>
</html>
