<!DOCTYPE html>
<html>
<head>
    <title>Customer Support</title>
</head>
<body>
<c:url var="logoutUrl" value="/logout"/>
<form action="${logoutUrl}" method="post">
    <input type="submit" value="Log out" />
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>

<h2>Comment #${commentId}: <c:out value="${comment.subject}"/></h2>

<%-- Only Admin or the user himself can shown the edit --%>
<security:authorize access="hasRole('ADMIN') or
 principal.username=='${comment.customerName}'">
    [<a href="<c:url value="/comment/edit/${comment.id}" />">Edit</a>]
</security:authorize>

<%-- Only Admin or the user himself can shown the delete --%>
<security:authorize access="hasRole('ADMIN')">
    [<a href="<c:url value="/comment/delete/${comment.id}" />">Delete</a>]
</security:authorize>

<br/><br/>
<i>Customer Name - <c:out value="${comment.customerName}"/></i><br/><br/>
<c:out value="${comment.body}"/><br/><br/>
<c:if test="${!empty comment.attachments}">
    Attachments:
    <c:forEach items="${comment.attachments}" var="attachment" varStatus="status">
        <c:if test="${!status.first}">, </c:if>
        <a href="<c:url value="/comment/${commentId}/attachment/${attachment.id}" />">
            <c:out value="${attachment.name}"/></a>

        [<a href="<c:url value="/comment/${commentId}/delete/${attachment.id}" />">Delete</a>]

    </c:forEach><br/><br/>
</c:if>
<a href="<c:url value="/comment" />">Return to list comments</a>
</body>
</html>