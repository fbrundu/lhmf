<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_admin.js"/>'></script>
		<script type="text/javascript">
			function drawPageCallback(){
				var History = window.History;
				
				if (History.enabled == true)
				{
					var newState = {data: {action:'messaggi'}, title: null, url: './messaggi'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
					getMyMessages();
				
			}
		</script>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
	<div class="round-border-topright"></div>
	<h1 class="first">Menu di Amministrazione</h1>
	<dl class="menu-navigazione">
	  <dt><a id="userLink" href="<spring:url htmlEscape="true" value="/userMgmt"/>">Gestione Utenti</a></dt>
	  <dt><a id="logLink" href="<spring:url htmlEscape="true" value="/log"/>">Consultazione log</a></dt>
	  <dt><a id="productLink" href="<spring:url htmlEscape="true" value="/productsMgmtAdmin"/>">Gestione Prodotti</a></dt>
	  <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statAdmin"/>">Statistiche</a></dt>
  </dl> 
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    </jsp:body>
</t:index>