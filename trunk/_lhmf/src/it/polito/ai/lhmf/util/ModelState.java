package it.polito.ai.lhmf.util;

public class ModelState
{
	private boolean toReloadProducts;

	public boolean isToReloadProducts()
	{
		synchronized (this)
		{
			return toReloadProducts;
		}
	}

	public void setToReloadProducts(boolean toReloadProducts)
	{
		synchronized (this)
		{
			this.toReloadProducts = toReloadProducts;
		}
	}
}
