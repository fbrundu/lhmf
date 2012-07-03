<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>

	<jsp:attribute name="userMenu">
      <p>Menu Utente (Supplier) </p>
    </jsp:attribute>

	<jsp:attribute name="scripts">
	<script type="text/javascript" src="./js/lib_supplier.js"></script>
    </jsp:attribute>

	<jsp:attribute name="bodyTitle">
      <h1>Title example (supplier)</h1>
    </jsp:attribute>

	<jsp:body>
    <p>Body example</p>
    <script type="text/javascript">
          HelloCategory();
          prList = window.localStorage.getItem('productCategoriesList');
        </script>
    </jsp:body>

</t:index>