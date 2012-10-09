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
		  <dt><a id="productsLink" href="<spring:url htmlEscape="true" value="/productsMgmt"/>">Gestione Prodotti</a></dt>
      <dt><a id="orderLink" href="<spring:url htmlEscape="true" value="/orderSup"/>">Gestione Ordini</a></dt>
		  <dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statSupplier"/>">Statistiche</a></dt>
		</dl> 
    </jsp:attribute>

	<jsp:attribute name="scripts">
	<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_supplier.js"/>'></script>
	    <script type="text/javascript">
      function drawPageCallback()
      {
    	  if(History.enabled){
				var newState = {data: {action:'orderSup', idOrd: 0, tab: 0}, title: null, url: './orderSup'};
				History.replaceState({action: 'null'}, null, newState.url);
				History.replaceState(newState.data, newState.title, newState.url);
			}
			else
        		writeOrderPage(0, 0);
      }
      </script>
    </jsp:attribute>

	<jsp:attribute name="bodyTitle">
      Gestione ordini
    </jsp:attribute>

  <jsp:attribute name="dialogs">
    <div class="gasDialog" id="dialog-confirm"
      title="Cancellare il prodotto?">
      <p>
        <span class="ui-icon ui-icon-alert"
          style="float: left; margin: 0 7px 20px 0;"></span>Sei sicuro?</p>
    </div>
        <div class="gasDialog" id="dialog-error-remove" title="Errore">
      <p>
        <span class="ui-icon ui-icon-alert"
          style="float: left; margin: 0 7px 20px 0;"></span>
          Errore nella cancellazione
      </p>
    </div>
    <div class="gasDialog" id="dialog-error-update" title="Errore">
      <p>
        <span class="ui-icon ui-icon-alert"
          style="float: left; margin: 0 7px 20px 0;"></span>
          Errore nell'aggiornamento
      </p>
    </div>
  </jsp:attribute>

	<jsp:body>
    </jsp:body>

</t:index>
