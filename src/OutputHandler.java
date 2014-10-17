class OutputHandler{

	private boolean error = false;
	private String errorMsg = "";
	private String prettyPrint = "";

	public static boolean isContOnError = false;
	public static boolean isDebug = false;
	public static boolean isPrintUsingDotNot = false;

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
		if(prettyPrint.isEmpty())
		{
			prettyPrint = msg;
		}
		else
		{
			prettyPrint += "\n" + msg;
		}
	}

	public void dump()
	{
		System.out.println(prettyPrint);
		if(error)
		{
			System.out.println("ERROR: " + errorMsg);
		}
		else
		{
			out.dump("No errors encountered");
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