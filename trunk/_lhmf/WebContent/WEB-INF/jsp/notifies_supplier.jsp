<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>

	<jsp:attribute name="userMenu">
      <div class="round-border-topright"></div>
		<h1 class="first">Menu Fornitore</h1>
		<dl class="menu-navigazione">
		  <dt>
				<a id="productsLink"
					href="<spring:url htmlEscape="true" value="/productsMgmt"/>">Gestione Prodotti</a>
			</dt>
      <dt><a id="orderLink" href="<spring:url htmlEscape="true" value="/order"/>">Gestione Ordini</a></dt>
  	  <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statSupplier"/>">Statistiche</a></dt>
		</dl> 
    </jsp:attribute>

	<jsp:attribute name="scripts">
	<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_supplier.js"/>'></script>
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

	<jsp:attribute name="bodyTitle">
      Interfaccia Fornitore
    </jsp:attribute>

	<jsp:body>
    </jsp:body>

</t:index>