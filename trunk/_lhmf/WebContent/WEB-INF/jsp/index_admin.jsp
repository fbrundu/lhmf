<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_admin.js"/>'></script>
					
			<c:choose>
				<c:when test="${firstPage == null}">
					<script type="text/javascript">
						function drawPageCallback(){
							writeIndexPage();
						}
					</script>
				</c:when>
				<c:when test="${firstPage == 'log'}">
					<c:choose>
						<c:when test="${minLog != null}">
							<script type="text/javascript">
								function drawPageCallback(){
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
								function drawPageCallback(){
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
						function drawPageCallback(){
							writeIndexPage();
						}
					</script>
				</c:otherwise>
			</c:choose>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
	<div class="round-border-topright"></div>
	<h1 class="first">Menu di Amministrazione</h1>
	<dl class="menu-navigazione">
	  <dt><a id="userLink" href="<spring:url htmlEscape="true" value="/userMgmt"/>">Gestione Utenti</a></dt>
	  <dt><a id="logLink" href="<spring:url htmlEscape="true" value="/log"/>">Log</a></dt>
	  <dt><a id="productLink" href="<spring:url htmlEscape="true" value="/productsMgmtAdmin"/>">Gestione Prodotti</a></dt>
    <dt><a href="<spring:url htmlEscape="true" value="/notifiche"/>">Notifiche (NN)</a></dt>
	  <dt><a href="<spring:url htmlEscape="true" value="/messaggi"/>">Messaggi (NN)</a></dt>
	</dl> 
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    </jsp:body>
</t:index>