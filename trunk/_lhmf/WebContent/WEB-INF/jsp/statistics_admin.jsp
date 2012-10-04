<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_admin.js"/>'></script>
		<script type="text/javascript">
      function drawPageCallback()
      {
        if (History.enabled)
        {
          var newState = {
            data : {
              action : 'statAdmin'
            },
            title : null,
            url : './statAdmin'
          };
          History.replaceState({
            action : 'null'
          }, null, newState.url);
          History.replaceState(newState.data, newState.title, newState.url);
        }
        writeStatPageAdmin();
      }
    </script>
	</jsp:attribute>

	<jsp:attribute name="userMenu">
	<div class="round-border-topright"></div>
    	<h1 class="first">Menù di amministrazione</h1>
    	<dl class="menu-navigazione">
    <dt>
				<a id="userLink"
					href="<spring:url htmlEscape="true" value="/userMgmt"/>">Gestione Utenti</a>
			</dt>
    <dt>
				<a id="logLink" href="<spring:url htmlEscape="true" value="/log"/>">Consultazione log</a>
			</dt>
    <dt>
				<a id="productLink" 
					href="<spring:url htmlEscape="true" value="/productsMgmtAdmin"/>">Gestione Prodotti</a>
			</dt>
	<dt><a id="statLink" href="<spring:url htmlEscape="true" value="/statAdmin"/>">Statistiche</a></dt>
  </dl>
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
    <div class="gasDialog" id="dialog-error-insert" title="Errore">
      <p>
        <span class="ui-icon ui-icon-alert"
					style="float: left; margin: 0 7px 20px 0;"></span>
          Errore nell'inserimento
      </p>
    </div>
    <div class="gasDialog" id="dialog-error-update" title="Errore">
      <p>
        <span class="ui-icon ui-icon-alert"
					style="float: left; margin: 0 7px 20px 0;"></span>
          Errore nell'aggiornamento
      </p>
    </div>
    <div class="gasDialog" id="dialog-ok" title="Ok">
      <p>
        <span class="ui-icon ui-icon-check"
					style="float: left; margin: 0 7px 20px 0;"></span>
          Operazione andata a buon fine
      </p>
    </div>
	</jsp:attribute>


	<jsp:attribute name="bodyTitle">Gestione prodotti</jsp:attribute>

</t:index>