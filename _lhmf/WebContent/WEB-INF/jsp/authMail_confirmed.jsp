<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="java.util.ArrayList" %>

<t:index>
	<jsp:attribute name="bodyTitle">Autenticazione Mail</jsp:attribute>
    
    <jsp:body>
    <div class="registrazioneform">
    	<form  id="regform">
    	<fieldset>
	    	<c:choose>
			    <c:when test='${authFailed == true}'>
			        <legend>&nbsp;Errore nell'Autenticazione della Mail&nbsp;</legend><br />
			        <div class="divresp">
			        	Si son verificati dei problemi durante l'autenticazione della mail.<br /><br />
			        	<div style="padding-left: 40px">
				          	<p><span class="actionError">
						          <c:forEach var="err" items="${errors}">
							        	<strong><c:out value="${err.id}" /></strong>: <c:out value="${err.error}" /><br />
							      </c:forEach>
							</span></p>
						</div>
					</div>
			    </c:when>
			    <c:otherwise>
			        <legend>&nbsp;Autenticazione della mail avvenuta con Successo&nbsp;</legend><br />
			        
			        <div class="divresp">
			        	Grazie <strong><c:out value="${firstname}" /></strong> per aver Autenticato la tua mail.<br /><br />
			        	<c:choose>
			        		<c:when test='${active == true }'>
			        			Il tuo account è stato abilitato per l'accesso al sistema.
			        		</c:when>
			        		<c:otherwise>
			        			Il tuo account è quasi pronto.<br /> Dopo l'attivazione da parte di un admin potrai avere accesso ai nostri servizi.
			        		</c:otherwise>
			        	</c:choose>
					</div>
			        
			    </c:otherwise>
			</c:choose>
       	</fieldset>
        </form>
        </div>
    </jsp:body>
</t:index>