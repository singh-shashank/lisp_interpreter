class LispIntException extends Exception
{
	private String cMessage;
	public LispIntException(String msg)
	{
		super(msg);
		cMessage = msg;
	}

	public LispIntException(String msg, Throwable cause)
	{
		super(cause);
		cMessage = msg;
	}

	String getCustomMessage()
	{
		return cMessage;
	}
}