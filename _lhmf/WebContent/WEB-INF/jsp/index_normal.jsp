<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>
  <jsp:attribute name="userMenu">
      <div class="round-border-topright"></div>
		<h1 class="first">Menu Utente</h1>
		<dl class="menu-navigazione">
		  <dt><a href="">Crea Scheda</a></dt>
		  <dt><a href="">Visualizza Schede</a></dt>
		  <dt><a href="">Notifiche (NN)</a></dt>
	      <dt><a href="">Messaggi (NN)</a></dt>				
		</dl> 
    </jsp:attribute>
    
    <jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_normal.js"/>'></script> 
		<script>
	  	function drawPageCallback(){
			writeIndexPage();
		}
	  	</script>
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">Interfaccia Utente Normale</jsp:attribute>
    
  	<jsp:body>
		
    </jsp:body>
</t:index>