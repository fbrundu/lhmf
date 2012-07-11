<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>
  <jsp:attribute name="userMenu">
      <p>Menu Utente (resp)</p>
    </jsp:attribute>
    
    <jsp:attribute name="scripts">
	<script type="text/javascript" src="./js/lib_resp.js"></script>
    </jsp:attribute>
    
  	<jsp:attribute name="bodyTitle">
      <h1>Title example (resp)</h1>
    </jsp:attribute>
    
  <jsp:body>
        <p>Body example</p>
        <script type="text/javascript">
      		$(function(){
        		HelloOrder();
      		});
        </script>
    </jsp:body>
</t:index>