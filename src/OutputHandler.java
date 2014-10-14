class OutputHandler{
	private boolean error = false;
	private String errorMsg = "";
	private String prettyPrint = "";

	public static boolean isContOnError = false;
	public static boolean isDebug = false;

	private static OutputHandler out = new OutputHandler();

	private OutputHandler()
	{

	}

	public static OutputHandler getInstance()
	{
		return out;
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
			System.out.println(prettyPrint);
		}
	}

	public void dump(String message)
	{
		if(isDebug)
		{
			System.out.println(message);
		}
	}

	public void dumpError(String message)
	{
		if(isDebug)
		{
			System.out.println("ERROR: " + message);
		}
	}
}