<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="bodyTitle">Registrazione</jsp:attribute>
    
    <jsp:body>
    	<form action='<spring:url htmlEscape="true" value="${actionUrl}"/>' method='post'>
        	<label for="firstname">Nome: </label>
        	<c:choose>
        		<c:when test="${firstname != null}"><input type="text" name="firstname" id="firstname" readonly="readonly" value="${firstname}"/></c:when>
        		<c:otherwise><input type="text" name="firstname" id="lastname" required="required"/></c:otherwise>
        	</c:choose>
        	<br><label for="lastname">Cognome: </label>
        	<c:choose>
        		<c:when test="${lastname != null}"><input type="text" name="lastname" id="lastname" readonly="readonly" value="${lastname}"/></c:when>
        		<c:otherwise><input type="text" name="firstname" id="lastname" required="required"/></c:otherwise>
        	</c:choose>
        	<br><label for="email">Email: </label>
        	<c:choose>
        		<c:when test="${email != null}"><input type="text" name="email" id="email" readonly="readonly" value="${email}"/></c:when>
        		<c:otherwise><input type="text" name="email" id="email" required="required"/></c:otherwise>
        	</c:choose>
        	<br><input type="submit" value="Registrati"/>
        </form>
    </jsp:body>
</t:index>