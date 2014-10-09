class Token
{
	public enum TokenType
	{
		NUMERAL_ATOM,
		LITERAL_ATOM,
		DOT,
		OPEN_PARAN,
		CLOSE_PARAN,
		ERROR,
		EOF
	}

	protected TokenType type;
	protected String stringValue;
	boolean unInitialized;

	Token()
	{
		unInitialized = true;
	}

	Token(String v, TokenType t)
	{
		stringValue = v;
		type = t;
		unInitialized = false;
	}

	void checkIfInitialized() throws LispIntException
	{
		if(unInitialized)
		{
			throw new LispIntException("Trying to access un-initialized token!", new Exception());
		}
	}

	String getTokenStringValue() throws LispIntException
	{
		checkIfInitialized();
		return stringValue;
	}

	TokenType getTokenType() throws LispIntException
	{
		checkIfInitialized();
		return type;
	}
}

class NumeralAtom extends Token
{
	private Integer value;

	NumeralAtom(String s) throws LispIntException
	{
		super(s, TokenType.NUMERAL_ATOM);
		stringValue = s;
		try
		{
			value = new Integer(s);
		}
		catch(NumberFormatException nfe)
		{
			throw new LispIntException("Not able to convert token '" + stringValue + "'' to numeric atom.", nfe);
		}
	}

	int getValue()
	{
		return value;
	}
}

class LiteralAtom extends Token
{
	private String literalValue;

	LiteralAtom(String s)
	{
		super(s, TokenType.LITERAL_ATOM);
		literalValue = s;
	}

	String getValue()
	{
		return literalValue;
	}
}