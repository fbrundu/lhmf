<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<link type="text/css" rel="stylesheet" href='<spring:url htmlEscape="true" value="/js/openid-selector/css/openid-shadow.css"/>'/>
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/openid-selector/js/openid-jquery.js"/>'></script>
		<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/openid-selector/js/openid-it.js"/>'></script>
		<script type="text/javascript">
		$(document).ready(function() {
			openid.init('openid_identifier');
		});

		function facebook_click() {
			$('#facebook_form').submit();
		}
		</script>
	</jsp:attribute>
	
	<jsp:attribute name="bodyTitle">Accesso al sito</jsp:attribute>
	
	<jsp:body>
	<div class="registrazioneform" align="center">
        <c:if test="${param.form_error != null}">
	        <fieldset><legend>&nbsp;Errore&nbsp;</legend><br />
			   <span class="actionError">
				   <strong>Il tentativo di accesso al sito non è andato a buon fine.</strong><br/><br/>
				   Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.<br/>
			  </span>
			</fieldset>
		</c:if>
		
        <form id="regform" method = "POST" action='<spring:url value="/j_spring_security_check"></spring:url>'>
        	
	        	<fieldset><legend>&nbsp;Effettua il Login&nbsp;</legend><br />
	        		<p>	<label for="j_username" class="top">Username:</label><br />
						<input type="text" name="j_username" id="j_username" tabindex="1" class="field" value="" /></p>
			  		<p>	<label for="j_password" class="top">Password:</label><br />
						<input type="password" name="j_password" id="j_password" tabindex="2" class="field" value="" /></p>
						<spring:url value="/signup" var="url" htmlEscape="true"/>
						<p>	<a href="${url}">Non sei registrato? Registrati ora!</a></p>
			  		<p>	<input type="submit" name="action" class="button" value="LOGIN"  /></p>
			 		
	        	</fieldset>
        	
        </form>
        
        
        
        <p><c:if test="${param.openid_error != null}">
	        <fieldset><legend>&nbsp;Errore&nbsp;</legend><br />
			   <span class="actionError">
				   <strong>Il tentativo di accesso al sito non è andato a buon fine.</strong><br/><br/>
				   Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.<br/>
			  </span>
			 </fieldset>
		</c:if></p>
		
		</div>
		
		
		<p class="center"></p>
		
		<div class="loginBox" align="center">
	        <h2 align="center">Accedi con il tuo account preferito</h2>
			<p class="center"></p>
			<form id="openid_form" class="loginform" name="oidf" action='<spring:url htmlEscape="true" value="/j_spring_openid_security_check"/>' method="POST">
				<div id="openid_choice" >
					<div id="openid_btns"></div>
				</div>
				<div id="openid_input_area">
					<input id="openid_identifier" name="openid_identifier" type="text" value="http://" required="required"/>
					<input id="openid_submit" type="submit" value="Sign-In"/>
				</div>
			</form>
			<form style="display:none" id="facebook_form" class="loginform" name="f" action='<spring:url htmlEscape="true" value="/j_spring_oauth_security_check"/>' method="POST">
				<input type="submit" />
			</form>
		</div>
    </jsp:body>
</t:index>