class SExp
{
	protected Token token;
	protected boolean isList;
	static OutputHandler out = OutputHandler.getInstance();

	public SExp()
	{
		token = new Token("UNDEFINED", Token.TokenType.UNDEF);
		isList = false;
	}
	public SExp(Token t)
	{
		token = t;
		isList = false;
	}

	public Token getToken()
	{
		return token;
	}

	public boolean getIsList()
	{
		return isList;
	}

	public void setIsList(boolean isList)
	{
		this.isList = isList;
	}

	public String print() throws LispIntException
	{
		//Parser.out.dump("Calling SExp print");
		return token.getTokenStringValue();
	}
}

class Atom extends SExp
{
	public Atom(NumeralAtom t)
	{
		super(t);
	}

	public Atom(LiteralAtom t)
	{
		super(t);
	}

	public boolean isNilAtom()
	{
		boolean ret = false;
		if(token instanceof LiteralAtom)
			{
				LiteralAtom l = (LiteralAtom)token;
				if(l.getType() == LiteralAtom.Type.NIL)
				{
					ret = true;
				}
			}
		return ret;
	}

	public boolean isTrueAtom()
	{
		boolean ret = false;
		if(token instanceof LiteralAtom)
			{
				LiteralAtom l = (LiteralAtom)token;
				if(l.getType() == LiteralAtom.Type.TRUE)
				{
					ret = true;
				}
			}
		return ret;
	}

	@Override
	public String print() throws LispIntException
	{
		//Parser.out.dump("Calling Atom print for " + token.getTokenStringValue());
		String ret = token.getTokenStringValue();
		if(!out.isPrintUsingDotNot)
		{
			if(token instanceof LiteralAtom)
			{
				LiteralAtom l = (LiteralAtom)token;
				if(l.getType() == LiteralAtom.Type.NIL)
				{
					isList = true;
				}
			}
		}
		return ret;
	}
}

class CSExp extends SExp
{
	public SExp left;
	public SExp right;
	private String terminalChar1;
	private String terminalChar2;

	public CSExp()
	{
		terminalChar1 = "";
		terminalChar2 = "";
	}
	public CSExp(SExp l, SExp r)
	{
		left = l;
		right = r;
		terminalChar1 = "";
		terminalChar2 = "";
	}


	public SExp getLeftSExp()
	{
		return left;
	}

	public SExp getRightSExp()
	{
		return right;
	}

	public void setTerminalChar1(String s)
	{
		terminalChar1 = s;
	}	

	public String getTerminalChar1()
	{
		return terminalChar1;
	}

	public void setTerminalChar2(String s)
	{
		terminalChar2 = s;
	}	

	public String getTerminalChar2()
	{
		return terminalChar2;
	}

	@Override
	public String print() throws LispIntException
	{
		//Parser.out.dump("Calling CExpPrint with terminal chars " + terminalChar1);
		//Parser.out.dump(" and " + terminalChar2);
		String res = "";
		String leftStr = "";
		String rightStr = "";
		
		if(left != null)
		{
			leftStr = left.print();
		}
		if(right != null)
		{
			rightStr = right.print();
			if(right instanceof Atom)
			{
				if(((Atom)right).isNilAtom() && !out.isPrintUsingDotNot)
				{
					rightStr = "";
				}
			}
		}

		if(!out.isPrintUsingDotNot && right != null && right.isList)
		{
			// So we are not forced to print using dot notation
			// and the right subtree is a list notation.

			// if we have NIL atom on right, then we need to empty
			// the string here.

			if(!terminalChar1.isEmpty())
			{
				res += terminalChar1;
			}
			res += leftStr;
			if(!rightStr.isEmpty())
			{
				// Strip of '(' & ')' if we have them on right side
				if(rightStr.charAt(0) == '(' 
					&& rightStr.charAt(rightStr.length()-1) == ')')
				{
					rightStr = rightStr.substring(1, rightStr.length()-1);
				}
				res += " ";
			}
			res += rightStr;
			if(!terminalChar2.isEmpty())
			{
				res += terminalChar2;
			}

			// Important, set the flag here so that this
			// could be propogated to upper subtrees
			this.isList = right.isList;
		}
		else
		{
			// Either debug flag set to print S expressions using
			// dot notation or this tree node is itself a 
			// dot s-expression
			if(!terminalChar1.isEmpty())
			{
				res += terminalChar1;
			}
			res += leftStr;
			if(!rightStr.isEmpty())
			{
				res += " . ";
				res += rightStr;
			}
			else
			{
				// print nothing in this case
			}
			if(!terminalChar2.isEmpty())
			{
				res += terminalChar2;
			}
		}
		return res;
	}

}