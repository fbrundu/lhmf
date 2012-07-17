// CRUD on ProductCategory
function newCategory(productCategory)
{
  if (productCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/newproductcategory", productCategory, function(
      idProductCategory)
  {
    console.debug("Inserted: " + idProductCategory);
  });
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

// HelloProduct: example on how to do ajax operations
function HelloProduct()
{
  var productCategory = new Object();
  productCategory.idProductCategory = 1;
  productCategory.description = "Descrizione2";
  newCategory(productCategory);
  var product = new Object();
  product.idProduct = 1;
  product.name = "Lasagne";
  product.description = "Di nonna";
  product.dimension = 2;
  product.measureUnit = "Fette";
  product.unitBlock = 1;
  product.availability = true;
  product.transportCost = 0.1;
  product.unitCost = 0.2;
  product.minBuy = 1;
  product.maxBuy = 1;
  product.idProductCategory = productCategory.idProductCategory;
  product.idMemberSupplier = 1;
  newProduct(product);
  getMyProducts();
  myProductsList = loadAllMyProductsFromLocalStorage();
  for ( var prodIndex in myProductsList)
  {
    if (myProductsList[prodIndex].name == "Lasagne")
    {
      myProductsList[prodIndex].name = "Cambiato";
      updateProduct(myProductsList[prodIndex]);
      getMyProducts();
    }
  }
  deleteAllMyProducts();
  deleteCategory(productCategory.idProductCategory);
  getMyProducts();
}

// CRUD on Product
function newProduct(product)
{
  if (product == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/newproduct", product, function(idProduct)
  {
    console.debug("Inserted: " + idProduct);
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
  $.postJSONsync("ajax/deleteproduct", idProduct, function(rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
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

function getAllSuppliers()
{
  $.getJSONsync("ajax/getsuppliers",
      function(suppliersList)
      {
        window.localStorage.setItem('suppliersList', JSON
            .stringify(suppliersList));
        console.debug("suppliersList saved in localstorage");
      });
}

function loadAllSuppliersFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('suppliersList'));
}

function getSupplierAsTableRow(suppliersList, idSupplier)
{
  if (suppliersList == undefined || idSupplier == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var supplierIndex in suppliersList)
  {
    // TODO return member resp name
    if (suppliersList[supplierIndex].idMember == idSupplier)
      return "<td>" + suppliersList[supplierIndex].name
          + suppliersList[supplierIndex].surname + "</td>";
  }
  return "";
}
