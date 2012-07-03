<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript" src="./js/lib_admin.js"></script>
	</jsp:attribute>
	
	<jsp:attribute name="userMenu">
    	<p>Menù di amministrazione</p>
    	<p>Gestione utenti</p>
    	<p><a href="log" onclick="logClicked(event)">Consultazione log</a></p>
    </jsp:attribute>
    
    <jsp:attribute name="bodyTitle">Interfaccia di amministrazione</jsp:attribute>
    
    <jsp:body>
    	<p>Body Admin</p>
    </jsp:body>
</t:index>