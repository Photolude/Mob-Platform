<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${plugins != null}">
	<c:forEach items="${plugins}" var="plugin">
		<c:if test="${plugin.name == null || plugin.name == \"\" || plugin.type == \"html\"}">
			<c:if test="${plugin.type == \"html\" }">
				${plugin.script}
			</c:if>
			<c:if test="${plugin.type != \"html\" }">
				<script type="${plugin.type}">
					${plugin.script}
				</script>
			</c:if>
		</c:if>
		<c:if test="${plugin.name != null && plugin.name != \"\"}">
			<script type="${plugin.type}" src="script/${plugin.name}"></script>
		</c:if>
	</c:forEach>
</c:if>