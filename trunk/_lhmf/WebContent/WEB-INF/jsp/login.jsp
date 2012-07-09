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
	<jsp:body>
        <h2>Accedi con username e password del sito</h2>
        <c:if test="${param.form_error != null}">
		   <span class="actionError">
		   Il tentativo di accesso al sito non è andato a buon fine.<br/><br/>
		   Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
		  </span>
		</c:if>
        <form method = "POST" action='<spring:url value="/j_spring_security_check"></spring:url>'>
        	<table>
				<tr>
					<td>
						<label for="j_username">Nome utente</label>
					</td>
					<td>
						<input type="text" id="j_username" name="j_username" value=""/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="j_password">Password</label>
					</td>
					<td >
						<input id="j_password" type="password" name="j_password" value=""/>
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<input type="submit" />
					</td>
				</tr>
			</table>
        	
        </form>
        <h2>Accedi con il tuo account preferito</h2>
        <c:if test="${param.openid_error != null}">
		   <span class="actionError">
		   Il tentativo di accesso al sito non è andato a buon fine.<br/><br/>
		   Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
		  </span>
		</c:if>
		<form id="openid_form" class="loginform" name="oidf" action='<spring:url htmlEscape="true" value="/j_spring_openid_security_check"/>' method="POST">
			<div id="openid_choice">
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
    </jsp:body>
</t:index>