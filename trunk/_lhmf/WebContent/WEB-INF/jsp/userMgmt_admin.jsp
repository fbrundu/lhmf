<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true&language=it"></script>
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
  <div class="round-border-topright"></div>
      <h1 class="first">Menù di amministrazione</h1>
      <dl class="menu-navigazione">
    <dt>
        <a id="userLink" 
          href="<spring:url htmlEscape="true" value="/userMgmt"/>">Gestione Utenti</a>
      </dt>
    <dt>
        <a id="logLink" href="<spring:url htmlEscape="true" value="/log"/>">Consultazione log</a>
      </dt>
    <dt>
        <a id="productLink"
          href="<spring:url htmlEscape="true" value="/productsMgmtAdmin"/>">Gestione Prodotti</a>
      </dt>
      <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statAdmin"/>">Statistiche</a></dt>
  </dl>
  </jsp:attribute>

	<jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    	<div id='tabs'>
    	  <ul>
    	    <li><a href='#tabs-1'>Aggiungi utente</a></li>
    	    <li><a href='#tabs-2'>Attiva utente</a></li>
			<li><a href='#tabs-3'>Lista utenti</a></li>
			<li><a href='#tabs-4'>Mappa Utenti</a></li>
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
			<div id='tabs-4'>
			
			<!-- Tab per la mappa degli utenti -->
			  Mappa Utenti
			</div>
		</div>
    </jsp:body>
</t:index>