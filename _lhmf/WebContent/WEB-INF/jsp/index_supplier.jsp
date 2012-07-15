<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>

	<jsp:attribute name="userMenu">
      <p>Menu Fornitore</p>
    </jsp:attribute>

	<jsp:attribute name="scripts">
	<script type="text/javascript"
			src='<spring:url htmlEscape="true" value="/js/lib_supplier.js"/>'></script>
  <script>
      $(function()
      {
        $("#products").html(
            returnFormattedProductsTable(JSON.parse(window.localStorage
                .getItem('my_products_list')), 1));
        $("#addProduct").click(function(){
          var product = new Object();
          product.name = $("#product_name").val();
          newProduct(product);
          });
      });
    </script>
    
    </jsp:attribute>

	<jsp:attribute name="bodyTitle">
      <h1>Pagina Fornitore</h1>
    </jsp:attribute>

	<jsp:body>
	  <h3>Prodotti</h3>
    <br/>
    <input id="product_name" type="text" placeholder="Nome" maxlength="45" />
    <input id="product_description" type="text" placeholder="Descrizione" maxlength="45" />
    <input id="product_dimension" type="number" placeholder="Dimensione" />
    <input id="product_measure_unit" type="text" placeholder="Unità di misura" maxlength="45" />
    <input id="product_block_unit" type="number" placeholder="Unità/blocco" />
    <label for="product_availability">Disponibilità</label><input id="product_availability" type="checkbox" />
    <input id="product_transport_cost" type="number" placeholder="Costo di trasporto" />
    <input id="product_unit_cost" type="number" placeholder="Costo per unità" />
    <input id="product_min_buy" type="number" placeholder="Unità minime acquistabili" />
    <input id="product_max_buy" type="number" placeholder="Unità massime acquistabili" />
  `idMember_supplier` int(11) NOT NULL,
  `idCategory` int(11) NOT NULL COMMENT
    
    <br/>
    <button id="addProduct">Aggiungi</button>
	  <table id="products">
	  </table>
    </jsp:body>

</t:index>