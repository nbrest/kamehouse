<%@ page errorPage="users-error.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="ar.com.nicobrest.mobileinspections.service.DragonBallUserService"%>
<%@ page import="ar.com.nicobrest.mobileinspections.model.DragonBallUser"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="dragonBallUser" class="ar.com.nicobrest.mobileinspections.model.DragonBallUser">
  <jsp:setProperty name="dragonBallUser" property="id" value="${param.id}" />
  <jsp:setProperty name="dragonBallUser" property="username" value="${param.username}" />
  <jsp:setProperty name="dragonBallUser" property="email" value="${param.email}" />
  <jsp:setProperty name="dragonBallUser" property="age" value="${param.age}" />
  <jsp:setProperty name="dragonBallUser" property="powerLevel" value="${param.powerLevel}" />
  <jsp:setProperty name="dragonBallUser" property="stamina" value="${param.stamina}" />
</jsp:useBean>

${dragonBallUserService.updateDragonBallUser(dragonBallUser)}
<%
  response.sendRedirect("users-list.jsp");
%>
