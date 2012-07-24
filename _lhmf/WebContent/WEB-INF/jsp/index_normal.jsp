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
	<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_normal.js"/>'></script>
  	<script> 
  	$(function()
  	      {
  	        $("#addPurchase").click(function(){
  	          var purchase = new Object();
  	          purchase.name = $("#product_name").val();
			  newPurchase(purchase);
  	          });
  	      	$("#purchase_table").html(
  	      			);
  	      });
  	</script>
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">
      <h1>Pagina utente normale</h1>
    </jsp:attribute>
    
  	<jsp:body>
		<h3>Schede di acquisto</h3>
	    <button id="addPurchase">Aggiungi</button>
	    
	     <table id="purchase_table">
	  	 </table>
    </jsp:body>
</t:index>