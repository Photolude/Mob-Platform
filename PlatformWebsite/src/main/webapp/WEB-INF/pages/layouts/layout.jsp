<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title><tiles:insertAttribute name="title" ignore="true" /></title>
	
	<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0" />
	
	<script type="text/javascript" src="<c:url value="/scripts/jquery-1.4.1.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/scripts/ServiceCalls.js" />"></script>
	<script type="text/javascript" src="<c:url value="/scripts/PlatformInfo.js" />"></script>
	<script type="text/javascript" src="<c:url value="/scripts/CacheManagement.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/scripts/MobInfrastructure.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/scripts/Layout.js"/>"></script>
	
	<script type="text/javascript">
		var navigationData = eval("<%=session.getAttribute("menuData") %>");
		var attributions = eval("<%=request.getAttribute("attributions") %>");
	</script>
	
	<c:forEach items="${datacalls}" var="call">
		${call.script}
	</c:forEach>
	
	<c:if test="${plugins != null}">
		<c:forEach items="${plugins}" var="plugin">
			<c:if test="${plugin.name == null || plugin.name == \"\" || plugin.type == \"html\"}">
				<c:if test="${plugin.type == \"html\" }">
				</c:if>
				<c:if test="${plugin.type == \"text/css\" }">
					<style>
						${plugin.script}
					</style>
				</c:if>
				<c:if test="${plugin.type != \"html\" && plugin.type != \"text/css\" }">
					<script type="${plugin.type}">
						${plugin.script}
					</script>
				</c:if>
			</c:if>
			<c:if test="${plugin.name != null && plugin.name != \"\"}">
				<c:if test="${plugin.type == \"text/css\" }">
					<LINK href="script/${plugin.name }" rel="stylesheet" type="text/css"/>
				</c:if>
				<c:if test="${plugin.type != \"html\" && plugin.type != \"text/css\" }">
					<script type="${plugin.type}" src="script/${plugin.name}"></script>
				</c:if>
			</c:if>
		</c:forEach>
	</c:if>
	<!-- <LINK href="<c:url value="/styles/defaultstyles_1.1.css"/>" rel="stylesheet" type="text/css"/> -->
	<link rel="icon" type="image/ico" href="<c:url value="/images/favicon.ico"/>"/>
</head>
<body>
	<c:if test="${plugins != null}">
		<c:forEach items="${plugins}" var="plugin">
			<c:if test="${plugin.name == null || plugin.name == \"\" || plugin.type == \"html\"}">
				<c:if test="${plugin.type == \"html\" }">
					${plugin.script}
				</c:if>
			</c:if>
		</c:forEach>
	</c:if>
</body>
</html>