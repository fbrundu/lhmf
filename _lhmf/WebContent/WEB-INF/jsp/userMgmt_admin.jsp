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
				if(History.enabled){
					var newState = {data: {action:'userMgmt'}, title: null, url: './userMgmt'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
					prepareUserForm();
				//var History = window.History;
				//var state = History.getState();
				//var newState = {data:{action: 'log', min: ${minLog}, max: ${maxLog}}, title: null, url: "./log?min=" + ${minLog} + "&max=" + ${maxLog}};
				//if(newState.data != state.data || newState.title != state.title || newState.url != state.url)
					//History.replaceState({action: 'null'}, null, newState.url);
					//History.replaceState(newState.data, newState.title, newState.url);
				
			}
		</script>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
    	<p>Menù di amministrazione</p>
    	<p>Gestione utenti</p>
    	<p><a href='<spring:url htmlEscape="true" value="/log"/>' id="logLink">Consultazione log</a></p>
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    	<div id='tabs'>
    	  <ul>
    	    <li><a href='#tabs-1'>Aggiungi utente</a></li>
    	    <li><a href='#tabs-2'>Attiva utente</a></li>
			<li><a href='#tabs-3'>Lista utenti</a></li>
		  </ul>
			<div id='tabs-1'>
			
			</div>
			<div id='tabs-2'>
			
			<!-- Tab per l'attivazione di un utente -->
			  Attiva utente
			</div>
			<div id='tabs-3'>
			
			<!-- Tab per la modifica di un utente -->
			  Lista Utenti
			</div>
		</div>
    </jsp:body>
</t:index>