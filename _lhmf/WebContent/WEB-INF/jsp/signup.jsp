<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.ArrayList" %>

<t:index>
    <jsp:attribute name="scripts"><script type="text/javascript" src="<spring:url htmlEscape="true" value="/js/controllaform.js"/>"></script></jsp:attribute>
	<jsp:attribute name="bodyTitle">Registrazione</jsp:attribute>
    
    <jsp:body>
    <div class="registrazioneform">
    	<form  id="regform" action='<spring:url htmlEscape="true" value="${actionUrl}"/>' method='post'>
    	<fieldset><legend>&nbsp;Dati per la Registrazione&nbsp;</legend><br />
        	<label for="firstname" class="left">Nome: </label>
        	<c:choose>
        		<c:when test="${firstname_static != null}"><input type="text" name="firstname" id="firstname" class="field_readonly" readonly="readonly" value="${firstname_static}"/></c:when>
        		<c:otherwise><input type="text" name="firstname" id="firstname" class="field" required="required" value="${firstname}" onfocus="scrivi_help('firstname');"/></c:otherwise>
        	</c:choose>
        	<br><label for="lastname" class="left">Cognome: </label>
        	<c:choose>
        		<c:when test="${lastname_static != null}"><input type="text" name="lastname" id="lastname" class="field_readonly" readonly="readonly" value="${lastname_static}"/></c:when>
        		<c:otherwise><input type="text" name="lastname" id="lastname" class="field" required="required" value="${lastname}" onfocus="scrivi_help('lastname');"/></c:otherwise>
        	</c:choose>
        	<br><label for="email" class="left">Email: </label>
        	<c:choose>
        		<c:when test="${email_static != null}"><input type="text" name="email" id="email" class="field_readonly" readonly="readonly" value="${email_static}"/></c:when>
        		<c:otherwise><input type="text" name="email" id="email" class="field" required="required" value="${email}" onfocus="scrivi_help('email');"/></c:otherwise>
        	</c:choose>
        	<br><label for="address" class="left">Indirizzo: </label>
          		<input type="text" name="address" id="address" class="field" required="required" value="${address}" onfocus="scrivi_help('address');"/>
	        <br><label for="city" class="left">Città: </label>
	          <input type="text" name="city" id="city" class="field" required="required" value="${city}" onfocus="scrivi_help('city');"/>
	        <br><label for="state" class="left">Stato: </label>
	          	<%@ include file="state.html" %>
	        <br><label for="cap" class="left">Cap: </label>
	          <input type="text" name="cap" id="cap" class="field" required="required" value="${cap}" onfocus="scrivi_help('cap');"/>
	        <br><label for="phone" class="left">Telefono: </label>
	          <input type="text" name="phone" id="phone" class="field" value="${phone}" onfocus="scrivi_help('phone');"/>
        </fieldset>
        <c:if test="${errors != null}">
	          <fieldset><legend>&nbsp;Errore&nbsp;</legend><br />
	          <div style="padding-left: 40px">
	          	<p><span class="actionError">
			          <c:forEach var="err" items="${errors}">
				        	<strong><c:out value="${err.id}" /></strong>: <c:out value="${err.error}" /><br />
				      </c:forEach>
				</span></p>
				</div>
	          </fieldset>
   	    </c:if>
        <fieldset><legend>&nbsp;Aiuto alla Digitazione&nbsp;</legend><br />
                <textarea name="helptext" style="text-align:center" rows="3" cols="20"></textarea>
        </fieldset>
        <c:if test="${getUserCredentials == true}">
	        <fieldset><legend>&nbsp;Informazioni di Login&nbsp;</legend><br />
	                <br><label for="contact_username" class="left">Username:</label>
	                    <input type="text" name="username" id="contact_username" class="field" value="" tabindex="11" onfocus="scrivi_help('username');"/>
	                <br><label for="contact_password" class="left">Password:</label>
	                    <input type="password" name="password" id="contact_password" class="field" value="" tabindex="12" onfocus="scrivi_help('password');"/>
	                <br><label for="contact_repassword" class="left">Riscrivi Password:</label>
	                    <input type="password" name="repassword" id="contact_repassword" class="field" value="" tabindex="13" onfocus="scrivi_help('repassword');"/>
	        </fieldset>
        </c:if>
        <p><input type="submit" class="button" value="Registrati"/></p>
        </form>
        </div>
    </jsp:body>
</t:index>