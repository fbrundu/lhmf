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
		<script>
	  	function drawPageCallback(){
			writeIndexPage();
		}
	  	</script>
    </jsp:attribute>
    
  <jsp:attribute name="userMenu">
 	 <div class="round-border-topright"></div>
		<h1 class="first">Menu Responsabile</h1>
		<dl class="menu-navigazione">
		  <dt><a id="orderLink" href="<spring:url htmlEscape="true" value="/orderResp"/>">Ordini</a></dt>
		  <dt><a id="purchaseLink" href="<spring:url htmlEscape="true" value="/purchaseResp"/>">Schede Di Acquisto</a></dt>
		  <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statResp"/>">Statistiche</a></dt>
		</dl> 
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">Interfaccia Responsabile</jsp:attribute>
    
  <jsp:body>
  </jsp:body>
</t:index>