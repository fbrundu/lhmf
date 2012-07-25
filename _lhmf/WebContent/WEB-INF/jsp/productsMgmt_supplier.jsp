<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_supplier.js"/>'></script>
		<script type="text/javascript">
      function drawPageCallback()
      {
        if (History.enabled)
        {
          var newState = {
            data : {
              action : 'productsMgmt'
            },
            title : null,
            url : './productsMgmt'
          };
          History.replaceState({
            action : 'null'
          }, null, newState.url);
          History.replaceState(newState.data, newState.title, newState.url);
        }
        writeSupplierPage();
      }
    </script>
	</jsp:attribute>

	<jsp:attribute name="userMenu">
    	<p>Menù fornitore</p>
    	<p>Gestione prodotti</p>
	</jsp:attribute>

	<jsp:attribute name="bodyTitle">Interfaccia fornitore</jsp:attribute>

	<jsp:body>
    </jsp:body>
</t:index>