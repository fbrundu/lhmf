<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib_admin.js"/>'></script>
		<script type="text/javascript">
			function drawPageCallback(){
				if(History.enabled){
					var newState = {data: {action:'userMgmt'}, title: null, url: './userMgmt'};
					History.replaceState({action: 'null'}, null, newState.url);
					History.replaceState(newState.data, newState.title, newState.url);
				}
				else
					prepareUserForm();
				//var History = window.History;
				//var state = History.getState();
				//var newState = {data:{action: 'log', min: ${minLog}, max: ${maxLog}}, title: null, url: "./log?min=" + ${minLog} + "&max=" + ${maxLog}};
				//if(newState.data != state.data || newState.title != state.title || newState.url != state.url)
					//History.replaceState({action: 'null'}, null, newState.url);
					//History.replaceState(newState.data, newState.title, newState.url);
				
			}
		</script>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
    	<p>Menù di amministrazione</p>
    	<p>Gestione utenti</p>
    	<p><a href='<spring:url htmlEscape="true" value="/log"/>' id="logLink">Consultazione log</a></p>
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    	<div id='tabs'>
    	  <ul>
    	    <li><a href='#tabs-1'>Aggiungi utente</a></li>
    	    <li><a href='#tabs-2'>Attiva utente</a></li>
			<li><a href='#tabs-3'>Modifica utente</a></li>
			<li><a href='#tabs-4'>Lista utenti</a></li>
		  </ul>
			<div id='tabs-1'>
			
			<!-- Tab per l'inserimento di un nuovo utente -->
			
			  <div class="registrazioneform" style="margin: 2em 0 0 65px;">
		    	<form  id="regform" action='newMember' method='get'>
		    	<fieldset><legend>&nbsp;Dati per la Registrazione&nbsp;</legend><br />
		        	<label for="username" class="left">Username:</label>
			          <input type="text" name="username" id="username" class="field" required="required"/>
		    <br><br><label for="firstname" class="left">Nome: </label>
		        	  <input type="text" name="firstname" id="firstname" class="field" required="required"/>
		        <br><label for="lastname" class="left">Cognome: </label>
		        	<input type="text" name="lastname" id="lastname" class="field" required="required"/>
		        <br><label for="email" class="left">Email: </label>
		        	<input type="text" name="email" id="email" class="field" required="required"/>
		        <br><label for="address" class="left">Indirizzo: </label>
		          	<input type="text" name="address" id="address" class="field" required="required"/>
			    <br><label for="city" class="left">Città: </label>
			        <input type="text" name="city" id="city" class="field" required="required"/>
			    <br><label for="state" class="left">Stato: </label>
			        <input type="text" name="state" id="state" class="field" required="required"/>
			     <br><label for="cap" class="left">Cap: </label>
			         <input type="text" name="cap" id="cap" class="field" required="required"/>
			     <br><label for="phone" class="left">Telefono: </label>
			         <input type="text" name="phone" id="phone" class="field"/>
		        </fieldset>
		        <div style="display:none;">
		        	<fieldset><legend id="legendError">&nbsp;Errore&nbsp;</legend><br />
			          <div id="errors" style="padding-left: 40px">
			          	
					  </div>
			        </fieldset>
		        </div>
		        <p><input type="submit" class="button" value="Registrati"/></p>
		        </form>
		      </div>
			
			</div>
			<div id='tabs-2'>
			
			<!-- Tab per l'attivazione di un utente -->
			  Attiva utente
			</div>
			<div id='tabs-3'>
			
			<!-- Tab per la modifica di un utente -->
			  Modifica utente
			</div>
			<div id='tabs-4'>
			
			<!-- Tab per la lista degli utenti -->
			  Lista Utenti
			</div>
		</div>
    </jsp:body>
</t:index>