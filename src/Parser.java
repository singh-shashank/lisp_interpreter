import java.util.*;
import java.io.IOException;
import java.io.OutputStreamWriter;

// TODO :
// 1) take care of extra spaces while printing

class Parser
{
	Lex lex;
	public static OutputHandler out = OutputHandler.getInstance();
	private ArrayList<Production> prodList = new ArrayList<Production>();
	private int[][] parseTable = new int[Symbol.NT_END.ordinal()][Symbol.T_END.ordinal()-Symbol.NT_END.ordinal()];
	Stack<Symbol> symStack = new Stack<Symbol>();

	public Parser(Lex l)
	{
		lex = l;
		initGrammar();
	}

	enum Symbol
	{
		NT_S("Non Terminal : <S>"),
		NT_E("Non Terminal : <E>"),
		NT_X("Non Terminal : <X>"),
		NT_Y("Non Terminal : <Y>"),
		NT_R("Non Terminal : <R>"),
		NT_END("Non Terminal : End Marker"),
		T_ATOM("Terminal : ATOM"),
		T_OPEN_PARAN("Terminal : ("),
		T_CLOSE_PARAN("Terminal : )"),
		T_DOT("Terminal : ."),
		T_EOF("Terminal : #EOF"),
		T_END("Terminal : End Marker"),
		UNDEF("Undefined Symbol");

		private String value;

		Symbol(String value)
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

	private boolean isTerminalSymbol(Symbol s)
	{
		boolean res = false;
		if(s.ordinal() > Symbol.NT_END.ordinal() &&
			s.ordinal() < Symbol.T_END.ordinal())
		{
			res = true;
		}
		return res;
	}

	private class Production
	{
		public ArrayList<Symbol> symbList;

		public Production()
		{
			symbList = new ArrayList<Symbol>();
		}

		public String getString()
		{
			String res = "";
			for(int i=0;i<symbList.size()-1; ++i)
			{
				res += symbList.get(i);
			}
			return res;

		}
	}

	private void initGrammar()
	{
		// Initialize Productions

		// 0. <S> ::= <E>
		Production p1 = new Production();
		p1.symbList.add(Symbol.NT_E);
		prodList.add(p1);

		// 1. <E> ::= atom
		Production p2 = new Production();
		p2.symbList.add(Symbol.T_ATOM);
		prodList.add(p2);

		// 2. <E> ::= (<X>
		Production p3 = new Production();
		p3.symbList.add(Symbol.T_OPEN_PARAN);
		p3.symbList.add(Symbol.NT_X);
		prodList.add(p3);

		// 3. <X> ::= <E><Y>
		Production p4 = new Production();
		p4.symbList.add(Symbol.NT_E);
		p4.symbList.add(Symbol.NT_Y);
		prodList.add(p4);

		// 4. <X> ::= )
		Production p5 = new Production();
		p5.symbList.add(Symbol.T_CLOSE_PARAN);
		prodList.add(p5);

		// 5. <Y> ::= .<E>)
		Production p6 = new Production();
		p6.symbList.add(Symbol.T_DOT);
		p6.symbList.add(Symbol.NT_E);
		p6.symbList.add(Symbol.T_CLOSE_PARAN);
		prodList.add(p6);

		// 6. <Y> ::= <R>)
		Production p7 = new Production();
		p7.symbList.add(Symbol.NT_R);
		p7.symbList.add(Symbol.T_CLOSE_PARAN);
		prodList.add(p7);

		// 7. <R> ::= $
		Production p8 = new Production();
		prodList.add(p8);

		// 8. <R> ::=<E><R>
		Production p9 = new Production();
		p9.symbList.add(Symbol.NT_E);
		p9.symbList.add(Symbol.NT_R);
		prodList.add(p9);

		// Initialize LL(1) Parse Table
		for(int i = 0;i< Symbol.NT_END.ordinal();++i)
		{
			for (int j=0; j< (Symbol.T_END.ordinal()-Symbol.NT_END.ordinal()); ++j)
			{
				parseTable[i][j] = -1;
			}
		}

		// Update parse table with actual production number, indexed from 0
		parseTable[0][0] = 0;
		parseTable[0][1] = 0;

		parseTable[1][0] = 1;
		parseTable[1][1] = 2;

		parseTable[2][0] = 3;
		parseTable[2][1] = 3;
		parseTable[2][2] = 4;

		parseTable[3][0] = 6;
		parseTable[3][1] = 6;
		parseTable[3][2] = 6;
		parseTable[3][3] = 5;

		parseTable[4][0] = 8;
		parseTable[4][1] = 8;
		parseTable[4][2] = 7;
		parseTable[4][3] = 7;

		out.dump("Dumping parse table");
		for(int i = 0;i< Symbol.NT_END.ordinal();++i)
		{
			out.dump(" NT : " + Symbol.values()[i]);
			for (int j=0; j< (Symbol.T_END.ordinal()-Symbol.NT_END.ordinal()); ++j)
			{
				out.dump(Symbol.values()[Symbol.NT_END.ordinal() + j + 1] + " = " + (parseTable[i][j]));
			}
		}


	}

	public void parseTokens() throws LispIntException, Exception
	{
		Token token = lex.getNextToken();
		while(token.getTokenType() !=  Token.TokenType.EOF)
		{
			SExp exp = new SExp();
			//symStack.push(Symbol.T_EOF);
			out.dump("----------------- Starting new expression-------------- ");
			symStack.push(Symbol.NT_S);
			exp = parseCompoundSExp(token);
			// if(token.getTokenType() == Token.TokenType.OPEN_PARAN)
			// {
				
			// 	exp = parseCompoundSExp(token);
			// }
			// else if(token.getTokenType() == Token.TokenType.NUMERAL_ATOM ||
			// 	token.getTokenType() == Token.TokenType.LITERAL_ATOM)
			// {
			// 	exp = parseTerminalSExp(token);
			// }			
			token = lex.getNextToken();

			if(!symStack.empty())
			{
				if(isTerminalSymbol(symStack.peek()))
				{
					throw new LispIntException("Parse Error - Missing a token ? " + symStack.pop() + "?..."
						, new Exception());
				}
				else
				{
					String msg = "One of these tokens could be missing : ";
					int nt_index = symStack.pop().ordinal();
					for (int j=0; j< (Symbol.T_END.ordinal()-Symbol.NT_END.ordinal()); ++j)
					{
						if(parseTable[nt_index][j] != -1)
						msg += Symbol.values()[Symbol.NT_END.ordinal() + j + 1] + " ";
					}
					throw new LispIntException("Parse Error - Missing a token ? " + msg
						, new Exception());
				}
			}

			out.dump("Printing expression ");
			out.prettyPrint(exp.print());
			out.prettyPrint("\n");
		}
	}

	private void dumpStack(Stack<Symbol> st)
	{
		Iterator<Symbol> iter = st.iterator();
		out.dump("----------Dumping stack----------- \n");
		while(iter.hasNext())
		{
			out.dump(iter.next().toString());
		}
		out.dump("-----------End of dump--------- \n");
	}

	public Atom parseTerminalSExp(Token token) throws LispIntException, Exception
	{
		Atom a = null;
		if(token instanceof NumeralAtom)
		{
			a = new Atom((NumeralAtom)token);
		}
		else if(token instanceof LiteralAtom)
		{
			a=  new Atom((LiteralAtom)token);
		}
		return a;
	}

	public SExp parseCompoundSExp(Token token) throws LispIntException, Exception
	{
		out.dump("Processing " + token.getTokenStringValue());
		if(token.getTokenType() == Token.TokenType.EOF
			|| symStack.empty())
		{
			out.dump("Didn't process : " + token.getTokenStringValue() + " ,so adding it back.");
			lex.addBackUnprocessedToken(token);
			return null;
		}

		dumpStack(symStack);
		applyProduction(token);
		dumpStack(symStack);

		CSExp compoundExp = new CSExp();
		SExp left;
		SExp right;		
		if(token.getTokenType() == Token.TokenType.OPEN_PARAN)
		{
			compoundExp.left = parseCompoundSExp(lex.getNextToken());
			compoundExp.right = parseCompoundSExp(lex.getNextToken());
			compoundExp.setTerminalChar("(");
		}
		else if(token.getTokenType() == Token.TokenType.NUMERAL_ATOM ||
			token.getTokenType() == Token.TokenType.LITERAL_ATOM)
		{
			//return parseTerminalSExp(token);
			compoundExp.left = parseTerminalSExp(token);
			compoundExp.right = parseCompoundSExp(lex.getNextToken());
		}
		else if(token.getTokenType() == Token.TokenType.CLOSE_PARAN)
		{
			compoundExp.left = null;
			compoundExp.right = null;
			compoundExp.setTerminalChar(")");
		}
		else if(token.getTokenType() == Token.TokenType.DOT)
		{
			compoundExp.left = parseCompoundSExp(lex.getNextToken());
			compoundExp.right = parseCompoundSExp(lex.getNextToken());
			compoundExp.setTerminalChar(".");
		}
		else
		{
			throw new LispIntException("Parse Error ", new Exception());
		}
		out.dump("Done Processing " + token.getTokenStringValue());

		return compoundExp;
	}	

	// TODO : check for empty stack
	private void applyProduction(Token t) throws LispIntException, Exception
	{
		boolean nonTerminalOnTop = true;
		//out.dump("In apply production for token : " + t.getTokenStringValue());
		//out.dump("On top of stack : " + symStack.peek());
		while(nonTerminalOnTop)
		{
			if(isTerminalSymbol(symStack.peek()))
			{
				nonTerminalOnTop = false;
				out.dump("We have a terminal symbol");
				// Terminal symbol on top of the stack
				if(matchSymbolWithTokenType(symStack.peek(), t.getTokenType()))
				{
					symStack.pop();
				}
				else
				{
					new LispIntException("Parse error : (but shouldn't have come here)",
						new Exception());
				}
			}
			else
			{
				// We have a non terminal symbol
				//out.dump("We have a non terminal symbol");
				Symbol s = getSymbolTypeForTokenType(t.getTokenType());
				//out.dump("Accessing parse table for index : " + symStack.peek().ordinal()
				//	+ ", " + (s.ordinal() - Symbol.NT_END.ordinal() -1 ));
				int prodNumber = parseTable[symStack.peek().ordinal()][s.ordinal() - Symbol.NT_END.ordinal() -1];
				if(prodNumber != -1)
				{
					symStack.pop();
					Production p = prodList.get(prodNumber);
					//out.dump("Applying production : " + p.getString());
					for(int i = p.symbList.size()-1; i >= 0; --i)
					{
						symStack.push(p.symbList.get(i));
					}
				}
				else
				{
					String msg = "Parse Error - Misplaced token encountered : " + t.getTokenStringValue();
					throw new LispIntException(msg, new Exception());
				}

			}
			//dumpStack(symStack);
		}
	}

	private Symbol getSymbolTypeForTokenType(Token.TokenType type)
	{
		Symbol res = Symbol.UNDEF;
		if(type == Token.TokenType.NUMERAL_ATOM || type == Token.TokenType.LITERAL_ATOM)
		{
			res = Symbol.T_ATOM;
		}
		else if(type == Token.TokenType.DOT)
		{
			res = Symbol.T_DOT;
		}
		else if(type == Token.TokenType.OPEN_PARAN)
		{
			res = Symbol.T_OPEN_PARAN;
		}
		else if(type == Token.TokenType.CLOSE_PARAN)
		{
			res = Symbol.T_CLOSE_PARAN;
		}
		else if(type == Token.TokenType.EOF)
		{
			res = Symbol.T_EOF;
		}
		return res;
	}

	private boolean matchSymbolWithTokenType(Symbol s, Token.TokenType type) throws LispIntException
	{
		boolean res = false;
		if(s == getSymbolTypeForTokenType(type))
		{
			res = true;
		}
		else
		{
			String msg = "Parse Error - Expected : " + s.toString();
			msg += ", but got : " + type.toString();
			throw new LispIntException(msg, new Exception());
		}
		return res;
	}
}

class SExp
{
	Token token;
	public SExp()
	{

	}
	public SExp(Token t)
	{
		token = t;
	}

	public Token getToken()
	{
		return token;
	}

	public String print() throws LispIntException
	{
		Parser.out.dump("Calling SExp print");
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

	@Override
	public String print() throws LispIntException
	{
		Parser.out.dump("Calling Atom print for " + token.getTokenStringValue());
		return token.getTokenStringValue() + " ";
	}
}

class CSExp extends SExp
{
	public SExp left;
	public SExp right;
	private String terminalChar;

	public CSExp()
	{
		terminalChar = "";
	}
	public CSExp(SExp l, SExp r)
	{
		left = l;
		right = r;
		terminalChar = "";
	}

	public SExp getLeftSExp()
	{
		return left;
	}

	public SExp getRightSExp()
	{
		return right;
	}

	public void setTerminalChar(String s)
	{
		terminalChar = s;
	}	

	public String getTerminalChar()
	{
		return terminalChar;
	}

	@Override
	public String print() throws LispIntException
	{
		Parser.out.dump("Calling CExpPrint with terminal char " + terminalChar);
		String res = "";
		if(!terminalChar.isEmpty())
		{
			if(terminalChar.equals("."))
				res = terminalChar + " ";
			else
				res = terminalChar;
		}
		if(left != null)
		{
			res += left.print();
		}
		if(right != null)
		{
			res += right.print();
		}
		return res;
	}

}
	
