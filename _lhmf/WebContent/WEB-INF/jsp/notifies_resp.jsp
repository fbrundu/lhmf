<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>

	<jsp:attribute name="scripts">
		<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/dng.css"/>'/>
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_resp.js"/>'></script> 
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_resp_normal.js"/>'></script>
		<script type="text/javascript">
			function drawPageCallback(){
				var History = window.History;
				if (History.enabled == true)
				{
					var newState = {data: {action:'notifiche'}, title: null, url: './notifiche'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
					getMyNotifies();
				
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
  </jsp:body>
</t:index>