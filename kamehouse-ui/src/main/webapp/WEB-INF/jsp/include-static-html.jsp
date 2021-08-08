<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- This JSP loads the static html received by ViewResolverController --%>
<%
  String hasError = (String) request.getAttribute("hasError");
  if ("true".equals(hasError)) {
      response.setStatus(404);
  }
%>
${staticHtmlContent}
