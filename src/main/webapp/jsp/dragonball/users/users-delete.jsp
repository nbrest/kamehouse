<%@ page errorPage="users-error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="userId" value="${param.id}"></c:set>
<c:set var="deletedDragonBallUser" value="${dragonBallUserService.deleteDragonBallUser(userId)}"></c:set>

<c:set var="deletedUserId" value="${deletedDragonBallUser.getId()}"></c:set>
<c:choose>
  <c:when test="${deletedUserId > 0}">
    <%
      response.sendRedirect("users-list.jsp");
    %>
  </c:when>
  <c:otherwise>
    <%
      response.sendRedirect("users-error.jsp");
    %>
  </c:otherwise>
</c:choose>