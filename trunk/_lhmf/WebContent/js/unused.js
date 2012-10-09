// FROM lib_supplier.js
function updateProductHandler(event)
{
  event.preventDefault();
  $("#dialog:ui-dialog").dialog("destroy");

  var idProduct = $(this).attr('name');

  if ($('#rowUpd' + idProduct).is(':visible'))
  {
    $('#rowUpd' + idProduct).hide('slow');
    return;
  }

  $('.rowUpdClass').hide('slow');

  var prod = getProduct(idProduct);
  if (prod == undefined)
  {
    $("#dialog-error-update").dialog({
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
  $('#divUpd' + idProduct)
      .html(
          "<div class='modificaProdottoForm' style='margin: 2em 0 0 65px;'>"
              + "<form id='updateProdForm"
              + idProduct
              + "' action='' method='post'>"
              + "<fieldset id='productFieldsetUpd"
              + idProduct
              + "'>"
              + "<legend>&nbsp;Dati per la modifica prodotto&nbsp;</legend>"
              + "<br /><label for='productNameUpd"
              + idProduct
              + "' class='left'>Nome: </label>"
              + "<input type='text' name='productNameUpd"
              + idProduct
              + "'"
              + " id='productNameUpd"
              + idProduct
              + "' class='field'"
              + "required='required' value='"
              + prod.name
              + "'/><br><label"
              + " for='productDescriptionUpd"
              + idProduct
              + "' class='left'>"
              + "Descrizione: </label><input type='text' "
              + "name='productDescriptionUpd"
              + idProduct
              + "' id='productDescriptionUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.description
              + "' />"
              + "<br><label for='productDimensionUpd"
              + idProduct
              + "' "
              + "class='left'>Dimensione: </label>"
              + "<input type='text' name='productDimensionUpd"
              + idProduct
              + "' "
              + "id='productDimensionUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.dimension
              + "' />"
              + "<br><label for='measure_unitUpd"
              + idProduct
              + "' "
              + "class='left'>Unità di misura: </label>"
              + "<input type='text' name='measure_unitUpd"
              + idProduct
              + "' "
              + "id='measure_unitUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.measureUnit
              + "' />"
              + "<br><label for='unit_blockUpd"
              + idProduct
              + "' "
              + "class='left'>Unità per blocco: </label>"
              + "<input type='text' name='unit_blockUpd"
              + idProduct
              + "' "
              + "id='unit_blockUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.unitBlock
              + "' />"
              + "<br><label for='transport_costUpd"
              + idProduct
              + "' "
              + "class='left'>Costo trasporto: </label>"
              + "<input type='text' name='transport_costUpd"
              + idProduct
              + "'"
              + " id='transport_costUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.transportCost
              + "' />"
              + "<br><label for='unit_costUpd"
              + idProduct
              + "' "
              + "class='left'>Costo unità: </label>"
              + "<input type='text' name='unit_costUpd"
              + idProduct
              + "' id='unit_costUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.unitCost
              + "' />"
              + "<br><label for='min_buyUpd"
              + idProduct
              + "' class='left'>Minimo unità acquistabili: </label>"
              + "<input type='text' name='min_buyUpd"
              + idProduct
              + "' id='min_buyUpd"
              + idProduct
              + "' class='field'  value='"
              + prod.minBuy
              + "'/>"
              + "<br><label for='max_buyUpd"
              + idProduct
              + "' class='left'>Massimo unità acquistabili: </label>"
              + "<input type='text' name='max_buyUpd"
              + idProduct
              + "' id='max_buyUpd"
              + idProduct
              + "'"
              + "class='field' value='"
              + prod.maxBuy
              + "' />"
              + "<br><br><label for='productCategoryUpd"
              + idProduct
              + "' class='left'>Categoria: </label>"
              + "<select name='productCategoryUpd"
              + idProduct
              + "' id='productCategoryUpd"
              + idProduct
              + "' class='field' onchange='checkCategorySelectUpd("
              + idProduct
              + ")'>"
              + "</select></fieldset>"
              + "<fieldset id='categoryFieldsetUpd"
              + idProduct
              + "' ><legend>&nbsp;Inserisci nuova categoria&nbsp;</legend><br />"
              + "<br><label for='categoryDescriptionUpd"
              + idProduct
              + "' class='left'>Descrizione: </label>"
              + "<input type='text' name='categoryDescriptionUpd"
              + idProduct
              + "' id='categoryDescriptionUpd"
              + idProduct
              + "' class='field' />"
              + "</fieldset>"
              + "<div id='errorDivUpd"
              + idProduct
              + "' style='display: none;'>"
              + "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errorsUpd"
              + idProduct
              + "' style='padding-left: 40px'>"
              + "</div></fieldset></div><p>"
              + "<input type='submit' class='button' value='Aggiorna prodotto' id='updateProductSubmit' name='"
              + idProduct + "' />" + "</p></form></div>");
  var categoriesList = getCategoriesNoLocal();
  var categoriesString = "";
  for ( var catIndex in categoriesList)
  {
    categoriesString += "<option";
    if (prod.category.idProductCategory == categoriesList[catIndex].idProductCategory)
      categoriesString += " selected='selected'";
    categoriesString += " value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }
  categoriesString += "<option value='nuova'>Nuova categoria...</option>";

  $('#productCategoryUpd' + idProduct).html(categoriesString);
  // disabilitare fieldset category
  $('#categoryFieldsetUpd' + idProduct).hide();
  $('#categoryFieldsetUpd' + idProduct).children().attr("disabled", "disabled");

  $('#updateProductSubmit').on("click", clickUpdateProductHandler);

  $('#rowUpd' + idProduct).show('slow');

  prepareProductsForm(1);

  return false; // don't post it automatically
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

function deleteAllMyProducts()
{
  myProductsList = loadAllMyProductsFromLocalStorage();

  for ( var prodIndex in myProductsList)
  {
    deleteProduct(myProductsList[prodIndex].id_product);
  }
}

function loadMyProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('myProductsList'));
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

// FROM lib_admin.js
function updateProductHandler(event)
{
event.preventDefault();
$("#dialog:ui-dialog").dialog("destroy");

var idProduct = $(this).attr('name');

if ($('#rowUpd' + idProduct).is(':visible'))
{
  $('#rowUpd' + idProduct).hide('slow');
  return;
}

$('.rowUpdClass').hide('slow');

var prod = getProduct(idProduct);
if (prod == undefined)
{
  $("#dialog-error-update").dialog({
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
$('#divUpd' + idProduct)
    .html(
        "<div class='modificaProdottoForm' style='margin: 2em 0 0 65px;'>"
            + "<form id='updateProdForm"
            + idProduct
            + "' action='' method='post'>"
            + "<fieldset id='productFieldsetUpd"
            + idProduct
            + "'>"
            + "<legend>&nbsp;Dati per la modifica prodotto&nbsp;</legend>"
            + "<br /><label for='productNameUpd"
            + idProduct
            + "' class='left'>Nome: </label>"
            + "<input type='text' name='productNameUpd"
            + idProduct
            + "'"
            + " id='productNameUpd"
            + idProduct
            + "' class='field'"
            + "required='required' value='"
            + prod.name
            + "'/><br><label"
            + " for='productDescriptionUpd"
            + idProduct
            + "' class='left'>"
            + "Descrizione: </label><input type='text' "
            + "name='productDescriptionUpd"
            + idProduct
            + "' id='productDescriptionUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.description
            + "' />"
            + "<br><label for='productDimensionUpd"
            + idProduct
            + "' "
            + "class='left'>Dimensione: </label>"
            + "<input type='text' name='productDimensionUpd"
            + idProduct
            + "' "
            + "id='productDimensionUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.dimension
            + "' />"
            + "<br><label for='measure_unitUpd"
            + idProduct
            + "' "
            + "class='left'>Unità di misura: </label>"
            + "<input type='text' name='measure_unitUpd"
            + idProduct
            + "' "
            + "id='measure_unitUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.measureUnit
            + "' />"
            + "<br><label for='unit_blockUpd"
            + idProduct
            + "' "
            + "class='left'>Unità per blocco: </label>"
            + "<input type='text' name='unit_blockUpd"
            + idProduct
            + "' "
            + "id='unit_blockUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.unitBlock
            + "' />"
            + "<br><label for='transport_costUpd"
            + idProduct
            + "' "
            + "class='left'>Costo trasporto: </label>"
            + "<input type='text' name='transport_costUpd"
            + idProduct
            + "'"
            + " id='transport_costUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.transportCost
            + "' />"
            + "<br><label for='unit_costUpd"
            + idProduct
            + "' "
            + "class='left'>Costo unità: </label>"
            + "<input type='text' name='unit_costUpd"
            + idProduct
            + "' id='unit_costUpd"
            + idProduct
            + "'"
            + "class='field' required='required' value='"
            + prod.unitCost
            + "' />"
            + "<br><label for='min_buyUpd"
            + idProduct
            + "' class='left'>Minimo unità acquistabili: </label>"
            + "<input type='text' name='min_buyUpd"
            + idProduct
            + "' id='min_buyUpd"
            + idProduct
            + "' class='field'  value='"
            + prod.minBuy
            + "'/>"
            + "<br><label for='max_buyUpd"
            + idProduct
            + "' class='left'>Massimo unità acquistabili: </label>"
            + "<input type='text' name='max_buyUpd"
            + idProduct
            + "' id='max_buyUpd"
            + idProduct
            + "'"
            + "class='field' value='"
            + prod.maxBuy
            + "' />"
            + "<br><br><label for='productCategoryUpd"
            + idProduct
            + "' class='left'>Categoria: </label>"
            + "<select name='productCategoryUpd"
            + idProduct
            + "' id='productCategoryUpd"
            + idProduct
            + "' class='field' onchange='checkCategorySelectUpd("
            + idProduct
            + ")'>"
            + "</select></fieldset>"
            + "<fieldset id='categoryFieldsetUpd"
            + idProduct
            + "' ><legend>&nbsp;Inserisci nuova categoria&nbsp;</legend><br />"
            + "<br><label for='categoryDescriptionUpd"
            + idProduct
            + "' class='left'>Descrizione: </label>"
            + "<input type='text' name='categoryDescriptionUpd"
            + idProduct
            + "' id='categoryDescriptionUpd"
            + idProduct
            + "' class='field' />"
            + "</fieldset>"
            + "<div id='errorDivUpd"
            + idProduct
            + "' style='display: none;'>"
            + "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />"
            + "<div id='errorsUpd"
            + idProduct
            + "' style='padding-left: 40px'>"
            + "</div></fieldset></div><p>"
            + "<input type='submit' class='button' value='Aggiorna prodotto' id='updateProductSubmit' name='"
            + idProduct + "' />" + "</p></form></div>");
var categoriesList = getCategoriesNoLocal();
var categoriesString = "";
for ( var catIndex in categoriesList)
{
  categoriesString += "<option";
  if (prod.category.idProductCategory == categoriesList[catIndex].idProductCategory)
    categoriesString += " selected='selected'";
  categoriesString += " value='"
      + categoriesList[catIndex].idProductCategory + "'>"
      + categoriesList[catIndex].description + "</option>";
}
categoriesString += "<option value='nuova'>Nuova categoria...</option>";

$('#productCategoryUpd' + idProduct).html(categoriesString);
// disabilitare fieldset category
$('#categoryFieldsetUpd' + idProduct).hide();
$('#categoryFieldsetUpd' + idProduct).children().attr("disabled", "disabled");

$('#updateProductSubmit').on("click", clickUpdateProductHandler);

$('#rowUpd' + idProduct).show('slow');
$('input:submit').button();

return false; // don't post it automatically
}

