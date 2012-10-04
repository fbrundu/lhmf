<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_resp.js"/>'></script>
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_resp_normal.js"/>'></script>
		<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/dng.css"/>'/>
		<script type="text/javascript">
			function drawPageCallback(){
				if(History.enabled){
					var newState = {data: {action:'purchaseResp'}, title: null, url: './purchaseResp'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
				    writePurchasePage();
			    	startRefreshOrder = 0;
			    	startRefresh = 1;
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
    <h1 class="first">Menu Responsabile</h1>
    <dl class="menu-navigazione">
      <dt><a id="orderLink" href="">Ordini</a></dt>
      <dt><a id="purchaseLink" href="">Schede Di Acquisto</a></dt>
      <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statSupplier"/>">Statistiche</a></dt>
    </dl> 
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Interfaccia Responsabile</jsp:attribute>
    
    <jsp:body>
    	<div id='tabsOrder'>
    	  <ul>
    	    <li><a href='tabsOrder-1'>Crea Ordine</a></li>
    	    <li><a href='tabsOrder-2'>Ordini Attivi</a></li>
			<li><a href='tabsOrder-3'>Ordini Scaduti</a></li>
			<li><a href='tabsOrder-3'>Ordini in Consegna</a></li>
		  </ul>
			<div id='tabsOrder-1'></div>
			<div id='tabsOrder-2'></div>
			<div id='tabsOrder-3'></div>
			<div id='tabsOrder-4'></div>
		</div>
    </jsp:body>
</t:index>