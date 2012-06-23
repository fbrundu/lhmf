// HelloCategory: example on how to do ajax operations
function HelloCategory()
{
  $(function()
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
          });
}

// CRUD on ProductCategory
function newCategory(productCategory)
{
  $.postJSON("ajax/newproductcategory", productCategory, function(
      idProductCategory)
  {
    console.debug("Inserted: " + idProductCategory);
  });
}

function updateCategory(productCategory)
{
  $.postJSON("ajax/updateproductcategory", productCategory, function(
      rowsAffected)
  {
    console.debug("Updated: " + rowsAffected);
  });
}

function getCategories()
{
  $.getJSON("ajax/getproductcategories", function(productCategoriesList)
  {
    window.localStorage.setItem('productCategoriesList', JSON.stringify(productCategoriesList));
    console.debug("productCategoriesList saved in localstorage");
  });
}

function deleteCategory(idProductCategory)
{
  $.postJSON("ajax/deleteproductcategory", idProductCategory, function(
      rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
  });
}
