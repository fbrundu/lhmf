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
					var newState = {data: {action:'log'}, title: null, url: './log'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
					prepareLogForm();
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
    	<p>Seleziona range di date:</p>
    	<p>
    	<form method="get" action="log">
	    	<label for='min'>Data iniziale: </label><input type='text' id='min'/>
	    	<label for='max'> Data finale: </label><input type='text' id='max'/>&nbsp;<button type='submit' id='logsRequest'>Seleziona</button>
    	</form>
		<table id='logs'>
		</table>
    </jsp:body>
</t:index>