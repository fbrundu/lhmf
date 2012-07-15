// HelloCategory: example on how to do ajax operations
function HelloCategory()
{
  var productCategory = new Object();
  productCategory.idProductCategory = "1";
  productCategory.description = "Descrizione2";
  newCategory(productCategory);
  productCategory.description = "Descrizione1";
  setTimeout(function()
  {
    updateCategory(productCategory);
  }, 3000);
  setTimeout(function()
  {
    getCategories();
  }, 5000);
  setTimeout(function()
  {
    deleteCategory(productCategory.idProductCategory);
  }, 7000);
}

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
  $.getJSON("ajax/getproductcategories", function(productCategoriesList)
  {
    window.localStorage.setItem('productCategoriesList', JSON
        .stringify(productCategoriesList));
    console.debug("productCategoriesList saved in localstorage");
  });
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
  product.idMember = 1;
  newProduct(product);
  getMyProducts();
  getCategories();
  //deleteProduct(1);
  //deleteCategory(productCategory.idProductCategory);
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

function getProducts()
{
  $.getJSON("ajax/getproducts", function(productsList)
  {
    window.localStorage.setItem('productsList', JSON.stringify(productsList));
    console.debug("productsList saved in localstorage");
  });
}

function getMyProducts()
{
  $.getJSON("ajax/getmyproducts", function(productsList)
  {
    window.localStorage.setItem('productsList', JSON.stringify(productsList));
    console.debug("productsList saved in localstorage");
  });
}

function deleteProduct(idProduct)
{
  $.postJSONsync("ajax/deleteproduct", idProduct, function(rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
}
