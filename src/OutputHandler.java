class OutputHandler{
	private boolean error = false;
	private String errorMsg = "";
	private String prettyPrint = "";

	public OutputHandler()
	{

	}

	public void errorMessage(String msg)
	{
		error = true;
		errorMsg += msg;
	}

	public void prettyPrint(String msg)
	{
		prettyPrint += msg;
	}

	public void dump()
	{
		if(error)
		{
			System.out.println("ERROR: " + errorMsg);
		}
		else
		{
			System.out.println("All is well");
		}
	}
}