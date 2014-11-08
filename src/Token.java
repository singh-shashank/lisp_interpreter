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
}

class NumeralAtom extends Token
{
	private Integer value;

	NumeralAtom(String s) throws LispIntException
	{
		super(s, TokenType.NUMERAL_ATOM);
		try
		{
			value = new Integer(s);
		}
		catch(NumberFormatException nfe)
		{
			throw new LispIntException("Not able to convert token '" + stringValue + "'' to numeric atom.", nfe);
		}
	}

	NumeralAtom(Integer v)
	{
		super(v.toString(), TokenType.NUMERAL_ATOM);
		value = v;
	}

	int getValue()
	{
		return value.intValue();
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
	private Type type;

	public enum Type
	{
		NIL("NIL"),
		TRUE("T"),
		QUOTE("QUOTE"),
		COND("COND"),
		DEFUN("DEFUN"),
		CAR("CAR"),
		CDR("CDR"),
		CONS("CONS"),
		ATOM("ATOM"),
		EQ("EQ"),
		NULL("NULL"),
		INT("INT"),
		PLUS("PLUS"),
		MINUS("MINUS"),
		TIMES("TIMES"),
		QUOTIENT("QUOTIENT"),
		REMAINDER("REMAINDER"),
		LESS("LESS"),
		GREATER("GREATER"),
		NOT_PRIMIITVE("NOT_PRIMITIVE_TYPE");

		private String value;

		Type(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		@Override
		public String toString()
		{
			return this.getValue();
		}
	}

	LiteralAtom(String s)
	{
		super(s, TokenType.LITERAL_ATOM);
		literalValue = s;
		setType();
	}

	LiteralAtom(Type t)
	{
		super(t.getValue(), TokenType.LITERAL_ATOM);
		literalValue = t.getValue();
		type = t;
	}

	private void setType()
	{
		if(literalValue.equals(Type.NIL.toString()))
		{
			type = Type.NIL;
		}
		else if(literalValue.equals(Type.TRUE.toString()))
		{
			type = Type.TRUE;
		}
		else if(literalValue.equals(Type.QUOTE.toString()))
		{
			type = Type.QUOTE;
		}
		else if(literalValue.equals(Type.COND.toString()))
		{
			type = Type.COND;
		}
		else if(literalValue.equals(Type.DEFUN.toString()))
		{
			type = Type.DEFUN;
		}
		else if(literalValue.equals(Type.CAR.toString()))
		{
			type = Type.CAR;
		}
		else if(literalValue.equals(Type.CDR.toString()))
		{
			type = Type.CDR;
		}
		else if(literalValue.equals(Type.CONS.toString()))
		{
			type = Type.CONS;
		}
		else if(literalValue.equals(Type.ATOM.toString()))
		{
			type = Type.ATOM;
		}
		else if(literalValue.equals(Type.EQ.toString()))
		{
			type = Type.EQ;
		}
		else if(literalValue.equals(Type.NULL.toString()))
		{
			type = Type.NULL;
		}
		else if(literalValue.equals(Type.INT.toString()))
		{
			type = Type.INT;
		}
		else if(literalValue.equals(Type.PLUS.toString()))
		{
			type = Type.PLUS;
		}
		else if(literalValue.equals(Type.MINUS.toString()))
		{
			type = Type.MINUS;
		}
		else if(literalValue.equals(Type.QUOTIENT.toString()))
		{
			type = Type.QUOTIENT;
		}
		else if(literalValue.equals(Type.REMAINDER.toString()))
		{
			type = Type.REMAINDER;
		}
		else if(literalValue.equals(Type.TIMES.toString()))
		{
			type = Type.TIMES;
		}
		else if(literalValue.equals(Type.LESS.toString()))
		{
			type = Type.LESS;
		}
		else if(literalValue.equals(Type.GREATER.toString()))
		{
			type = Type.GREATER;
		}
		else
		{
			type = Type.NOT_PRIMIITVE;
		}
	}

	public String getValue()
	{
		return literalValue;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	String getTokenStringValue() throws LispIntException
	{
		return literalValue.toString();
	}

	public static LiteralAtom createNilAtom()
	{
		return new LiteralAtom(Type.NIL);
	}

	public static LiteralAtom createTrueAtom()
	{
		return new LiteralAtom(Type.TRUE);
	}
}

class ErrorAtom extends Token
{
	private String errorString;

	ErrorAtom(String msg)
	{
		super("ERROR: ", TokenType.ERROR);
		errorString = msg;
	}

	String getErrorString()
	{
		return errorString;
	}

	public static ErrorAtom createErrorAtom(String msg)
	{
		return new ErrorAtom(msg);
	}
}