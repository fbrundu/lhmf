<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.ArrayList" %>

<t:index>
	<jsp:attribute name="bodyTitle">Registrazione</jsp:attribute>
    
    <jsp:body>
    <div class="registrazioneform">
    	<form  id="regform">
    	<fieldset><legend>&nbsp;Registrazione avvenuta con Successo&nbsp;</legend><br />
        	
        	<div class="divresp">
        	Grazie <c:out value="${firstname}" /> <c:out value="${lastname}" /> per aver effettuato la registrazione.<br /><br />
        	
        	<c:choose>
			  <c:when test="${checkMail == true}">
			    Una mail di verifica ti è stata inviata al tuo indirizzo di posta specificato durante la registrazione. 
        	Attivando la tua  mail potrai aver accesso ai nostri servizi
			  </c:when>
			  <c:otherwise>
			    Il tuo account è quasi pronto. <br />Dopo l'attivazione da parte di un admin potrai avere accesso ai nostri servizi.
			  </c:otherwise>
			</c:choose>
			</div>
       	</fieldset>
        </form>
        </div>
    </jsp:body>
</t:index>