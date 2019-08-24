<%-- This JSP loads the static file received by ViewResolverController --%>
<% 
  String staticHtmlToLoad = (String) request.getAttribute("staticHtmlToLoad");
%>

<jsp:include page="<%=staticHtmlToLoad%>" />
