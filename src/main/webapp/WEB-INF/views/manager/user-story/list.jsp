<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<jstl:if test="${projectId!=null}">
		<h2><acme:message code="master.user-story.list.of-project"/></h2>
</jstl:if>

<acme:list>
	<acme:list-column code="manager.user-story.list.label.title" path="title" width="10%"/>
	<acme:list-column code="manager.user-story.list.label.description" path="description" width="10%"/>
	<acme:list-column code="manager.user-story.list.label.estimatedCost" path="estimatedCost" width="10%"/>
	<acme:list-column code="manager.user-story.list.label.priority" path="priority" width="10%"/>
	<acme:list-column code="manager.user-story.list.label.acceptanceCriteria" path="acceptanceCriteria" width="10%"/>
	<acme:list-column code="manager.user-story.list.label.link" path="link" width="10%"/>
</acme:list>

<jstl:choose>
	<jstl:when test="${projectId!=null && canCreate==true}">
		<acme:button code="manager.project.form.button.create-of-project" action="/manager/user-story/create?projectId=${projectId}"/>
	</jstl:when>
	<jstl:when test="${projectId==null}">
		<acme:button code="manager.project.form.button.create" action="/manager/user-story/create"/>
	</jstl:when>
</jstl:choose>