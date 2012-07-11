//CRUD on Order
/////
function newOrder(order)
{
  $.postJSON("ajax/neworder", order, function(idOrder)
  {
    console.debug("Inserted: " + idOrder);
  });
}

function updateOrder(order)
{
  $.postJSON("ajax/updateorder", product, function(rowsAffected)
  {
    console.debug("Updated: " + rowsAffected);
  });
}

/*function getOders()
{
  $.getJSON("ajax/getproducts", function(productsList)
  {
    window.localStorage.setItem('productsList', JSON.stringify(productsList));
    console.debug("productsList saved in localstorage");
  });
}

function deleteOrder(idOrder)
{
  $.postJSON("ajax/deleteproduct", idProduct, function(rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
}
*/
/*function HelloOrder()
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
  setTimeout(function()
  {
    newProduct(product);
  }, 3000);
  setTimeout(function()
  {
    getProducts();
  }, 5000);
  setTimeout(function()
  {
    getCategories();
  }, 7000);
  setTimeout(function()
  {
    deleteCategory(productCategory.idProductCategory);
  }, 9000);
}
*/