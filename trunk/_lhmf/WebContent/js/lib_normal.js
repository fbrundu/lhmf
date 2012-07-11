//CRUD on Order
function newPurchase(purchase)
{
  $.postJSON("ajax/newpurchase", purchase, function(
      idPurchase)
  {
    console.debug("Inserted: " + idPurchase);
  });
}
