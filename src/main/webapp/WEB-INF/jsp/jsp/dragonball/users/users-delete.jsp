<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<c:set var="userId" value="${param.id}"></c:set>
<c:set var="deletedDragonBallUser" value="${dragonBallUserService.deleteDragonBallUser(userId)}"></c:set>

<c:set var="deletedUserId" value="${deletedDragonBallUser.getId()}"></c:set>
<c:choose>
  <c:when test="${deletedUserId > 0}">
    <%
      response.sendRedirect("users-list");
    %>
  </c:when>
</c:choose>