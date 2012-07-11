//CRUD on Order
function newOrder(productCategory)
{
  $.postJSON("ajax/newproductcategory", productCategory, function(
      idProductCategory)
  {
    console.debug("Inserted: " + idProductCategory);
  });
}

function HelloOrder()
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
