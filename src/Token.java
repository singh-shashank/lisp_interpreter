class Token
{
	public enum TokenType
	{
		NUMERAL_ATOM,
		LITERAL_ATOM,
		NIL_ATOM,
		DOT,
		OPEN_PARAN,
		CLOSE_PARAN,
		ERROR,
		EOF,
		UNDEF
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

	public static Token createNilAtom()
	{
		return new Token("NIL", TokenType.NIL_ATOM);
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

	@Override
	String getTokenStringValue() throws LispIntException
	{
		return value.toString();
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

	@Override
	String getTokenStringValue() throws LispIntException
	{
		return literalValue.toString();
	}
}

class ErrorAtom extends Token
{
	private String errorString;

	ErrorAtom(String s, String msg)
	{
		super(s, TokenType.ERROR);
		errorString = msg;
	}

	String getErrorString()
	{
		return errorString;
	}
}