<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>

	<jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_resp.js"/>'></script> 
		<script>
	  	function drawPageCallback(){
			writeIndexPage();
		}
	  	</script>
    </jsp:attribute>
    
  <jsp:attribute name="userMenu">
 	 <div class="round-border-topright"></div>
		<h1 class="first">Menu Utente</h1>
		<dl class="menu-navigazione">
		  <dt><a href="">Link 1</a></dt>
		  <dt><a href="">Link 2</a></dt>
		  <dt><a href="">Link 3</a></dt>	
		  <dt><a href="">Link 4</a></dt>			
		</dl> 
      <div class="round-border-topright"></div>
		<h1 class="first">Menu Responsabile</h1>
		<dl class="menu-navigazione">
		  <dt><a id="orderLink" href="">Ordini</a></dt>
		  <dt><a href="">Notifiche (NN)</a></dt>
	      <dt><a href="">Messaggi (NN)</a></dt>				
		</dl> 
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">Interfaccia Responsabile</jsp:attribute>
    
  <jsp:body>
  </jsp:body>
</t:index>