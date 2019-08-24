<%-- This JSP loads the static html received by ViewResolverController --%>
<% 
  String staticHtmlToLoad = (String) request.getAttribute("staticHtmlToLoad");
%>

<jsp:include page="<%=staticHtmlToLoad%>" />
