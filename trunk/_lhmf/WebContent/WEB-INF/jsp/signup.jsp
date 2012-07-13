<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.ArrayList" %>

<t:index>
    <jsp:attribute name="scripts"><script type="text/javascript" src="/_lhmf/js/controllaform.js"></script></jsp:attribute>
	<jsp:attribute name="bodyTitle">Registrazione</jsp:attribute>
    
    <jsp:body>
    <div class="registrazioneform">
    	<form  id="regform" action='<spring:url htmlEscape="true" value="${actionUrl}"/>' method='post'>
    	<fieldset><legend>&nbsp;Dati per la Registrazione&nbsp;</legend><br />
        	<label for="firstname" class="left">Nome: </label>
        	<c:choose>
        		<c:when test="${fromOpenID == true && firstname != null}"><input type="text" name="firstname" id="firstname" class="field_readonly" value="${firstname}"/></c:when>
        		<c:otherwise><input type="text" name="firstname" id="firstname" class="field" required="required" value="${firstname}" onfocus="scrivi_help('firstname');"/></c:otherwise>
        	</c:choose>
        	<br><label for="lastname" class="left">Cognome: </label>
        	<c:choose>
        		<c:when test="${fromOpenID == true && lastname != null}"><input type="text" name="lastname" id="lastname" class="field_readonly" readonly="readonly" value="${lastname}"/></c:when>
        		<c:otherwise><input type="text" name="lastname" id="lastname" class="field" required="required" value="${lastname}" onfocus="scrivi_help('lastname');"/></c:otherwise>
        	</c:choose>
        	<br><label for="email" class="left">Email: </label>
        	<c:choose>
        		<c:when test="${fromOpenID == true && email != null}"><input type="text" name="email" id="email" class="field_readonly" readonly="readonly" value="${email}"/></c:when>
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
	          <input type="text" name="phone" id="phone" class="field" required="required" value="${phone}" onfocus="scrivi_help('phone');"/>
	        <br><input type="submit" class="button" value="Registrati"/>
	          <c:if test="${fromOpenID == false}">
	          
		          <div id="errors">
		          <ul>
			          <c:forEach var="err" items="${errors}">
				        <li><c:out value="${err.id}" />: <c:out value="${err.error}" /></li>
				      </c:forEach>
				  </ul>
		          </div>
          	  </c:if>
          	  
        </fieldset>
        <fieldset><legend>&nbsp;Aiuto alla Digitazione&nbsp;</legend><br />
                <textarea name="helptext" style="text-align:center" rows="3" cols="20"></textarea>
              </fieldset>
        </form>
        </div>
    </jsp:body>
</t:index>