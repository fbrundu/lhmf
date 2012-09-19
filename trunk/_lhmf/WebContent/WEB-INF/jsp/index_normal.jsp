<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>

	<jsp:attribute name="scripts">
		<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/dng.css"/>'/>
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_normal.js"/>'></script> 
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
		  <dt><a id="purchaseLink" href="">Schede Di Acquisto</a></dt>
		</dl> 
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">Interfaccia Utente Normale</jsp:attribute>
    
  	<jsp:body>
		
    </jsp:body>
</t:index>