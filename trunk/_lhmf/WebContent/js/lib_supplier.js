// CRUD on ProductCategory
function newCategory(productCategory)
{
  $.postJSONsync("ajax/newproductcategory", productCategory, function(
      idProductCategory)
  {
    console.debug("Inserted: " + idProductCategory);
  });
}

function updateCategory(productCategory)
{
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
  $.postJSONsync("ajax/deleteproductcategory", idProductCategory, function(
      rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
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
  $.postJSONsync("ajax/newproduct", product, function(idProduct)
  {
    console.debug("Inserted: " + idProduct);
  });
}

function updateProduct(product)
{
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
  $.getJSONsync("ajax/getmyproducts", function(productsList)
  {
    window.localStorage.setItem('myProductsList', JSON
        .stringify(productsList));
    console.debug("myProductsList saved in localstorage");
  });
}

function loadAllMyProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('myProductsList'));
}

function deleteProduct(idProduct)
{
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

function getProductsAsTableRows(productList, page)
{
  var returnedTableString = "";
  if (page < 1 || (page - 1) * 20 >= productList.length)
    return "";
  for ( var prodIndex = (page - 1) * 20; prodIndex < productList.length
      && prodIndex <= page * 20; prodIndex++)
  {
    returnedTableString += "<tr>";
    returnedTableString += "<td>" + productList[prodIndex].name + "</td>";
    returnedTableString += "</tr>";
  }
  return returnedTableString;
}
