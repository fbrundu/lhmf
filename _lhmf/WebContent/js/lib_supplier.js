// CRUD on ProductCategory
function newCategory(pcDescription)
{
  if (pcDescription == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var idCategory = undefined;
  $.postSync("ajax/newproductcategory", {
    description : pcDescription
  }, function(idProductCategory)
  {
    idCategory = idProductCategory;
  });
  return idCategory;
}

function updateCategory(productCategory)
{
  if (productCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/updateproductcategory", productCategory, function(
      rowsAffected)
  {
    console.debug("Updated: " + rowsAffected);
  });
}

function getCategories()
{
  $.getJSONsync("ajax/getproductcategories", function(productCategoriesList)
  {
    window.localStorage.setItem('productCategoriesList', JSON
        .stringify(productCategoriesList));
    console.debug("productCategoriesList saved in localstorage");
  });
}

function getCategoriesNoLocal()
{
  var categoriesList;
  $.getJSONsync("ajax/getproductcategories", function(productCategoriesList)
  {
    categoriesList = productCategoriesList;
  });
  return categoriesList;
}

function loadCategoriesFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('productCategoriesList'));
}

function deleteCategory(idProductCategory)
{
  if (idProductCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/deleteproductcategory", idProductCategory, function(
      rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
}

function getCategoryDescription(productCategoriesList, idProductCategory)
{
  if (productCategoriesList == undefined || idProductCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var categoryIndex in productCategoriesList)
  {
    if (productCategoriesList[categoryIndex].idProductCategory == idProductCategory)
      return productCategoriesList[categoryIndex].description;
  }
  return "";
}

function getCategoriesAsTableRows(productCategoriesList, page, itemsPerPage)
{
  var returnedTableString = "";
  if (page < 1 || (page - 1) * itemsPerPage >= productCategoriesList.length
      || itemsPerPage < 1 || itemsPerPage > 100
      || productCategoriesList == undefined || page == undefined
      || itemsPerPage == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var productIndex = (page - 1) * itemsPerPage; productIndex < productCategoriesList.length
      && productIndex <= page * itemsPerPage; productIndex++)
  {
    returnedTableString += "<tr>";
    returnedTableString += "<td>"
        + productCategoriesList[prodIndex].description + "</td>";
    returnedTableString += "</tr>";
  }
  return returnedTableString;
}

// CRUD on Product
function newProduct(productParameters)
{
  if (productParameters == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postSync("ajax/newproduct", productParameters, function(idProduct)
  {
    if (idProduct > 0)
    {
      console.debug("Inserted product: " + idProduct);
      $("#productFieldset").children("input").val("");
    }
    else
      alert("Errore nella creazione di un nuovo prodotto");
  });
}

function updateProduct(product)
{
  if (product == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/updateproduct", product, function(rowsAffected)
  {
    console.debug("Updated: " + rowsAffected);
  });
}

function getAllProducts()
{
  $.getJSONsync("ajax/getproducts", function(productsList)
  {
    window.localStorage.setItem('productsList', JSON.stringify(productsList));
    console.debug("productsList saved in localstorage");
  });
}

function loadAllProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('productsList'));
}

function getMyProducts()
{
  $.getJSONsync("ajax/getmyproducts",
      function(productsList)
      {
        window.localStorage.setItem('myProductsList', JSON
            .stringify(productsList));
        console.debug("myProductsList saved in localstorage");
      });
}

function getMyProductsNoLocal()
{
  var myProductsList;
  $.getJSONsync("ajax/getmyproducts", function(productsList)
  {
    myProductsList = productsList;
  });
  return myProductsList;
}

function loadMyProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('myProductsList'));
}

function deleteProduct(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnRowsAffected = undefined;
  $.postSync("ajax/deleteproduct", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnRowsAffected = rowsAffected;
  });
  return returnRowsAffected;
}

function deleteAllMyProducts()
{
  myProductsList = loadAllMyProductsFromLocalStorage();

  for ( var prodIndex in myProductsList)
  {
    deleteProduct(myProductsList[prodIndex].id_product);
  }
}

function getProductsAsTableRows(productsList, productCategoriesList, page,
    itemsPerPage, suppliersList)
{
  var returnedTableString = "";
  if (page < 1 || (page - 1) * itemsPerPage >= productsList.length
      || itemsPerPage < 1 || itemsPerPage > 100 || productsList == undefined
      || productCategoriesList == undefined || page == undefined
      || itemsPerPage == undefined || suppliersList == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var productIndex = (page - 1) * itemsPerPage; productIndex < productsList.length
      && productIndex <= page * itemsPerPage; productIndex++)
  {
    returnedTableString += "<tr>";
    returnedTableString += "<td>" + productsList[productIndex].name + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].description
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].dimension
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].measureUnit
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].unitBlock
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].availability
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].transportCost
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].unitCost
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].minBuy + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].maxBuy + "</td>";
    returnedTableString += "<td>"
        + getCategoryDescription(productCategoriesList,
            productsList[productIndex].idProductCategory) + "</td>";
    returnedTableString += getSupplierAsTableRow(suppliersList,
        productsList[productIndex].idMemberSupplier);
    returnedTableString += "</tr>";
  }
  return returnedTableString;
}

$(function()
{
  $.datepicker.setDefaults({
    dateFormat : 'dd/mm/yy'
  });
  drawPageCallback();
});

function writeIndexPage()
{
  $('.centrale').html("<p>Questa è l'interfaccia fornitore</p>");
}

function writeSupplierPage(tab)
{
  $(".centrale").html(
      "<div id='tabs'><ul><li><a href='#tabs-1'>Aggiungi prodotto</a></li>"
          + "<li><a href='#tabs-2'>Gestione listino</a></li></ul>"
          + "<div id='tabs-1'></div><div id='tabs-2'></div>");

  $('#tabs-1')
      .html(
          "<div class='creazioneProdottoForm' style='margin: 2em 0 0 65px;'>"
              + "<form id='newProdForm' action='newProduct' method='post'>"
              + "<fieldset id='productFieldset'><legend>&nbsp;Dati per la creazione prodotto&nbsp;</legend>"
              + "<br /><label for='productName' class='left'>Nome: </label>"
              + "<input type='text' name='productName' id='productName' class='field'"
              + "required='required' /><br><label for='productDescription' class='left'>"
              + "Descrizione: </label><input type='text' name='productDescription' id='productDescription'"
              + "class='field' required='required' />"
              + "<br><label for='productDimension' class='left'>Dimensione: </label>"
              + "<input type='text' name='productDimension' id='productDimension'"
              + "class='field' required='required' />"
              + "<br><label for='measure_unit' class='left'>Unità di misura: </label>"
              + "<input type='text' name='measure_unit' id='measure_unit'"
              + "class='field' required='required' />"
              + "<br><label for='unit_block' class='left'>Unità per blocco: </label>"
              + "<input type='text' name='unit_block' id='unit_block'"
              + "class='field' required='required' />"
              + "<br><label for='transport_cost' class='left'>Costo trasporto: </label>"
              + "<input type='text' name='transport_cost' id='transport_cost'"
              + "class='field' required='required' />"
              + "<br><label for='unit_cost' class='left'>Costo unità: </label>"
              + "<input type='text' name='unit_cost' id='unit_cost'"
              + "class='field' required='required' />"
              + "<br><label for='min_buy' class='left'>Minimo unità acquistabili: </label>"
              + "<input type='text' name='min_buy' id='min_buy' class='field' />"
              + "<br><label for='max_buy' class='left'>Massimo unità acquistabili: </label>"
              + "<input type='text' name='max_buy' id='max_buy'"
              + "class='field' />"
              + "<br><br><label for='productCategory' class='left'>Categoria: </label>"
              + "<select name='productCategory' id='productCategory' class='field' onchange='checkCategorySelect()'>"
              + "</select></fieldset>"
              + "<fieldset id='categoryFieldset' ><legend>&nbsp;Inserisci nuova categoria&nbsp;</legend><br />"
              + "<br><label for='categoryDescription' class='left'>Descrizione: </label>"
              + "<input type='text' name='categoryDescription' id='categoryDescription' class='field' />"
              + "</fieldset>"
              + "<div id='errorDiv' style='display: none;'>"
              + "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errors' style='padding-left: 40px'>"
              + "</div></fieldset></div><p>"
              + "<input type='submit' class='button' value='Crea prodotto' id='newProductSubmit' />"
              + "</p></form></div>");
  var categoriesList = getCategoriesNoLocal();
  var categoriesString = "<option value='notSelected' selected='selected'>Seleziona...</option>";
  for ( var catIndex in categoriesList)
  {
    categoriesString += "<option value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }
  var categoriesForListino = categoriesString;
  categoriesString += "<option value='nuova'>Nuova categoria...</option>";

  $('#productCategory').html(categoriesString);

  var myProducts = getMyProductsNoLocal();
  var initialNumberOfPages = myProducts.length / 10;
  if (myProducts.length % 10 > 0)
    initialNumberOfPages += 1;

  var pagesForListino = "";
  for ( var page = 1; page <= initialNumberOfPages; page++)
    pagesForListino += "<option value='" + page + "'>" + page + "</option>";

  $('#tabs-2')
      .html(
          "<div class='listinoform'>"
              + "<form method='post' action=''>"
              + "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />"
              + "<label for='productCategorySearch' class='left'>Categoria: </label>"
              + "<select name='productCategorySearch' id='productCategorySearch' class='field'>"
              + categoriesForListino
              + "</select>"
              + "<label for='pageSearch' class='left'>&nbsp;&nbsp;&nbsp;Pagina: </label>"
              + "<select name='pageSearch' id='pageSearch' class='field'>"
              + pagesForListino
              + "</select>"
              + "<label for='itemsPerPageSearch' class='left'>&nbsp;&nbsp;&nbsp;Risultati Per Pagina: </label>"
              + "<select name='itemsPerPageSearch' id='itemsPerPageSearch' class='field'>"
              + "<option value='10'> 10 </option>"
              + "<option value='25'> 25 </option>"
              + "<option value='50'> 50 </option>"
              + "</select>"
              + "</fieldset><p><input type='submit' class='button' value='Visualizza' id='productListRequest' /></p>"
              + "</form>"
              + "<table id='productsListTable' class='list'></table>"
              + "<div id='errorDiv2' style='display:none;'>"
              + "<fieldset><legend id='legendError2'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errors2' style='padding-left: 40px'>"
              + "</div>"
              + "</fieldset>" + "</div><br />" + "</div>");

  prepareProductsForm(tab);
}

function checkCategorySelect()
{
  var selected = $('#productCategory').val();
  $("#errorDiv").hide('slow');

  if (selected == 'nuova')
  {
    $('#categoryFieldset').show('slow');
    $('#categoryFieldset').children().attr("disabled", false);
  }
  else
  {
    // Nuova categoria non selezionata
    $('#categoryFieldset').hide('slow');
    $('#categoryFieldset').children().attr("disabled", true);
  }
}

function prepareProductsForm(tab)
{
  $('#tabs').tabs({
    selected : tab
  });

  // disabilitare fieldset resp
  $('#categoryFieldset').hide();
  $('#categoryFieldset').children().attr("disabled", "disabled");

  $('#newProductSubmit').on("click", clickNewProductHandler);

  $('#productListRequest').on("click", clickNewProductSearchHandler);
}

function clickNewProductSearchHandler(event)
{
  event.preventDefault();

  var productCategory = $("#productCategorySearch").val();
  var page = $("#pageSearch").val();
  var itemsPerPage = $("#itemsPerPageSearch").val();
  var myProducts = getMyProductsNoLocal();
  var productsString = "";
  for ( var prodIndex = (page - 1) * itemsPerPage; prodIndex < myProducts.length
      && prodIndex < (page * itemsPerPage); prodIndex++)
  {
    if (productCategory != "notSelected"
        && productCategory != myProducts[prodIndex].idProductCategory)
      continue;
    productsString += "<tr id='listRow" + myProducts[prodIndex].idProduct
        + "'>" + "<td>" + myProducts[prodIndex].name + "</td>" + "<td>"
        + myProducts[prodIndex].description + "</td>";
    if (myProducts[prodIndex].availability == 0)
    {
      productsString += "<td id='listHead" + myProducts[prodIndex].idProduct
          + "' class='no'>Non in listino</td>";
      productsString += "<td id='listCont" + myProducts[prodIndex].idProduct
          + "'><form id='prodAval' name='" + myProducts[prodIndex].idProduct
          + "' action=''>";
      productsString += "<input type='submit' class='button' value='Inserisci in listino' />";
      productsString += "</form></td><td id='listDel"
          + myProducts[prodIndex].idProduct + "'><form id='prodDel' name='"
          + myProducts[prodIndex].idProduct + "' action=''>";
      productsString += "<input type='submit' class='button' value='Cancella' />";
      productsString += "</form>";
    }
    else
    {
      productsString += "<td id='listHead" + myProducts[prodIndex].idProduct
          + "' class='yes'>In listino</td>";
      productsString += "<td id='listCont" + myProducts[prodIndex].idProduct
          + "'><form id='prodNotAval' name='" + myProducts[prodIndex].idProduct
          + "' action=''>";
      productsString += "<input type='submit' class='button' value='Rimuovi da listino' />";
      productsString += "</form></td><td id='listDel"
          + myProducts[prodIndex].idProduct + "'><form id='prodDel' name='"
          + myProducts[prodIndex].idProduct + "' action=''>";
      productsString += "<input type='submit' class='button' value='Cancella' />";
      productsString += "</form>";
    }
    productsString += "</tr>";
  }
  $('#productsListTable').html(productsString);
  $('form').filter(function()
  {
    return this.id.match(/prodAval/);
  }).bind('submit', setProductAvailableHandler);
  $('form').filter(function()
  {
    return this.id.match(/prodNotAval/);
  }).bind('submit', setProductUnavailableHandler);
  $('form').filter(function()
  {
    return this.id.match(/prodDel/);
  }).bind('submit', deleteProductHandler);
}

function deleteProductHandler(event)
{
  event.preventDefault();
  $("#dialog:ui-dialog").dialog("destroy");

  var idProduct = $(this).attr('name');

  $("#dialog-confirm").dialog({
    resizable : false,
    height : 140,
    modal : true,
    buttons : {
      "Elimina" : function()
      {
        $(this).dialog("close");
        if (deleteProduct(idProduct) > 0)
        {
          $("#listRow" + idProduct).hide('slow');
        }
        else
        {
          $("#dialog-error-remove").dialog({
            resizable : false,
            height : 140,
            modal : true,
            buttons : {
              "Ok" : function()
              {
                $(this).dialog('close');
              }
            }
          });
        }

      },
      "Annulla" : function()
      {
        $(this).dialog("close");
      }
    }
  });
  return false; // don't post it automatically
}

function setProductAvailableHandler(event)
{
  event.preventDefault();
  var idProduct = $(this).attr('name');
  if (setProductAvailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('In listino');
    $('#listHead' + idProduct).attr('class', 'yes');
    $('#listCont' + idProduct).html(
        "<form id='prodNotAval' name='" + idProduct + "' action=''>"
            + "<input id='prodNotAval" + idProduct
            + "' type='submit' class='button'"
            + " value='Rimuovi da listino' />" + "</form>");
  }
  else
  {
    $("#dialog-error-insert").dialog({
      resizable : false,
      height : 140,
      modal : true,
      buttons : {
        "Ok" : function()
        {
          $(this).dialog('close');
        }
      }
    });
  }

  $('form').filter(function()
  {
    return this.id.match(/prodNotAval/);
  }).bind('submit', setProductUnavailableHandler);

  return false; // don't post it automatically
}

function setProductUnavailableHandler(event)
{
  event.preventDefault();
  var idProduct = $(this).attr('name');
  if (setProductUnavailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('Non listino');
    $('#listHead' + idProduct).attr('class', 'no');
    $('#listCont' + idProduct).html(
        "<form id='prodAval' name='" + idProduct + "' action=''>"
            + "<input type='submit' class='button'"
            + " value='Inserisci in listino' />" + "</form>");
  }
  else
  {
    $("#dialog-error-remove").dialog({
      resizable : false,
      height : 140,
      modal : true,
      buttons : {
        "Ok" : function()
        {
          $(this).dialog('close');
        }
      }
    });
  }
  $('form').filter(function()
  {
    return this.id.match(/prodAval/);
  }).bind('submit', setProductAvailableHandler);

  return false; // don't post it automatically
}

function setProductAvailable(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedRowsAffected = undefined;
  $.getSync("ajax/setproductavailable", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnedRowsAffected = rowsAffected;
    if (rowsAffected > 0)
      console.debug("Product inserted in list: " + idProduct);
  });
  return returnedRowsAffected;
}

function setProductUnavailable(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedRowsAffected = undefined;
  $.getSync("ajax/setproductunavailable", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnedRowsAffected = rowsAffected;
    if (rowsAffected > 0)
      console.debug("Product removed from list: " + idProduct);
  });
  return returnedRowsAffected;
}

function clickNewProductHandler(event)
{
  event.preventDefault();

  var errors = new Array();

  var productName = $('#productName').val();
  var productDescription = $('#productDescription').val();
  var productDimension = $('#productDimension').val();
  var measureUnit = $('#measure_unit').val();
  var unitBlock = $('#unit_block').val();
  var transportCost = $('#transport_cost').val();
  var unitCost = $('#unit_cost').val();
  var minBuy = $('#min_buy').val();
  var maxBuy = $('#max_buy').val();
  var productCategory = $('#productCategory').val();
  var categoryDescription = $('#categoryDescription').val();

  if (productName == "" || isNumber(productName))
  {
    errors.push("Nome prodotto: Formato non valido");
  }
  if (productDescription == "")
  {
    errors.push("Descrizione prodotto: Formato non valido");
  }
  if (productDimension == "" || !isPositiveNumber(productDimension))
  {
    errors.push("Dimensione: Formato non valido");
  }
  if (measureUnit == "" || isNumber(measureUnit))
  {
    errors.push("Unit&agrave; di misura: Formato non valido");
  }
  if (unitBlock == "" || !isPositiveNumber(unitBlock))
  {
    errors.push("Unit&agrave; per blocco: Formato non valido");
  }
  if (transportCost == "" || !isPositiveNumber(transportCost))
  {
    errors.push("Costo di trasporto: Formato non valido");
  }
  if (unitCost == "" || !isPositiveNumber(unitCost))
  {
    errors.push("Costo per unit&agrave;: Formato non valido");
  }
  if (minBuy == "" || !isPositiveNumber(minBuy))
  {
    errors.push("Minimo unit&agrave; acquistabili: Formato non valido");
  }
  if (maxBuy == "" || !isPositiveNumber(maxBuy))
  {
    errors.push("Massimo unit&agrave; acquistabili: Formato non valido");
  }
  if (!isPositiveNumber(productCategory))
  {
    if (categoryDescription == "" || isNumber(categoryDescription))
      errors.push("Categoria di prodotto: Formato non valido");
  }

  if (errors.length > 0)
  {
    $("#errors").html("");
    $("#errorDiv").hide();

    for ( var i = 0; i < errors.length; i++)
    {
      var error = errors[i].split(":");
      $("#errors").append(
          "<strong>" + error[0] + "</strong>: " + error[1] + "<br />");
    }

    $("#errorDiv").show("slow");
    // $("#errorDiv").fadeIn(1000);
  }
  else
  {
    // Creazione nuova categoria
    if (!isPositiveNumber(productCategory))
    {
      var idProductCategory = newCategory(categoryDescription);
      if (idProductCategory > 0)
      {
        productCategory = idProductCategory;
        $("#categoryDescription").val("");
      }
      else
      {
        $("#dialog-error-insert").dialog({
          resizable : false,
          height : 140,
          modal : true,
          buttons : {
            "Ok" : function()
            {
              $(this).dialog('close');
            }
          }
        });
        return;
      }

    }

    // Creazione nuovo prodotto
    newProduct({
      productName : productName,
      productDescription : productDescription,
      productDimension : productDimension,
      measureUnit : measureUnit,
      unitBlock : unitBlock,
      transportCost : transportCost,
      unitCost : unitCost,
      minBuy : minBuy,
      maxBuy : maxBuy,
      productCategory : productCategory,
    });

    writeSupplierPage(0);
  }
}

function isNumber(n)
{
  return !isNaN(parseFloat(n)) && isFinite(n);
}

function isPositiveNumber(n)
{
  return !isNaN(parseFloat(n)) && isFinite(n) && n >= 0;
}