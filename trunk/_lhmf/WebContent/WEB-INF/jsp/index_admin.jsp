<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src="./js/lib_admin.js"></script>
		
			<c:choose>
				<c:when test="${firstPage == null}">
					<script type="text/javascript">
						function firstPageCallback(){
							writeIndexPage();
						}
					</script>
				</c:when>
				<c:when test="${firstPage == 'log'}">
					<c:choose>
						<c:when test="${minLog != null}">
							<script type="text/javascript">
								function firstPageCallback(){
									var state = History.getState();
									var newState = {data:{action: 'log', min: ${minLog}, max: ${maxLog}}, title: null, url: "./log?min=" + ${minLog} + "&max=" + ${maxLog}};
									if(newState.data != state.data || newState.title != state.title || newState.url != state.url)
										History.replaceState({action: 'null'}, null, newState.url);
										History.replaceState(newState.data, newState.title, newState.url);
								}
							</script>
						</c:when>
						<c:otherwise>
							<script type="text/javascript">
								function firstPageCallback(){
									var state = History.getState();
									var newState = {data: {action:'log'}, title: null, url: './log'};
									if(newState != state)
										History.replaceState({action: 'null'}, null, newState.url);
										History.replaceState(newState.data, newState.title, newState.url);
								}
							</script>	
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<script type="text/javascript">
						function firstPageCallback(){
							writeIndexPage();
						}
					</script>
				</c:otherwise>
			</c:choose>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
    	<p>Menù di amministrazione</p>
    	<p>Gestione utenti</p>
    	<p><a href="log" id="logLink">Consultazione log</a></p>
    </jsp:attribute>
    
    <jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    </jsp:body>
</t:index>