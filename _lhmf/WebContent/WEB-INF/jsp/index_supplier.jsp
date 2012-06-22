<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>

	<jsp:attribute name="userMenu">
      <p>Menu Utente (Supplier) </p>
    </jsp:attribute>

	<jsp:attribute name="scripts">
    </jsp:attribute>

	<jsp:attribute name="bodyTitle">
      <h1>Title example (supplier)</h1>
    </jsp:attribute>

	<jsp:body>
    <p>Body example</p>
    <!-- ${ productList } -->
    <script type="text/javascript">
          $(function()
          {
            var productCategory = new Object();
            productCategory.idProductCategory = "1";
            productCategory.description = "Descrizione2";
            $.postJSON("ajax/newproductcategory", productCategory, function(
                idProductCategory)
            {
              console.debug(idProductCategory);
            });
            $.getJSON("ajax/getproductcategories", function(
                productCategoriesList)
            {
              console.debug(productCategoriesList);
            });
            $.postJSON("ajax/deleteproductcategory",
                productCategory.idProductCategory, function(idProductCategory)
                {
                  console.debug(idProductCategory);
                });
          });
        </script>
    </jsp:body>

</t:index>