<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>
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
		  <dt><a href="">Crea ordine</a></dt>
		  <dt><a href="">Visualizza ordini</a></dt>
		  <dt><a href="">Notifiche (NN)</a></dt>
	      <dt><a href="">Messaggi (NN)</a></dt>				
		</dl> 
    </jsp:attribute>
    
    <jsp:attribute name="scripts">
	<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_resp.js"/>'></script>
  	<script> 
  	$(function()
  	      {
  	        $("#addOrder").click(function(){
  	          var order = new Object();
			  newOrder(order);
  	          });
  	      });
  	</script>
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">
      <h1>Pagina responsabile</h1>
    </jsp:attribute>
    
  <jsp:body>
        <h3>Nuovo ordine</h3>
	    <br/>
	    <input id="date_open" type="text" placeholder="Data di apertura" maxlength="10" />
	    <input id="date_close" type="text" placeholder="Data di chiusura" maxlength="10"/>
	    <input id="date_delivery" type="text" placeholder="Data di consegna" maxlength="10" />
	  `idMember_resp` int(11) NOT NULL,
	  `idMember_supplier` int(11) NOT NULL COMMENT
	    
	    <br/>
	    <button id="addOrder">Aggiungi</button>
    </jsp:body>
</t:index>