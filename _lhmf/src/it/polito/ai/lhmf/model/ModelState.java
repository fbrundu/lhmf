package it.polito.ai.lhmf.model;

public class ModelState
{
	private boolean haveNewProducts;

	public boolean isHaveNewProducts()
	{
		synchronized (this)
		{
			return haveNewProducts;
		}
	}

	public void setHaveNewProducts(boolean haveNewProducts)
	{
		synchronized (this)
		{
			this.haveNewProducts = haveNewProducts;
		}
	}
}
