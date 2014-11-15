import java.util.*;
import java.lang.Math;

class EvalSExp
{
	public static OutputHandler out = OutputHandler.getInstance();
	public static SExp eval(SExp exp,
		HashMap<String, Stack<SExp>> a, 
		HashMap<String, SExp> d) throws LispIntException // TODO : fix this
	{
		// exp is an atom
		SExp retVal = new SExp();
		if(exp.isAtom())
		{
			out.dump("SExp for eval is atom");
			if(exp.isTrueAtom())
			{
				retVal = new Atom(LiteralAtom.createTrueAtom());
			}
			else if(exp.isNilAtom())
			{
				retVal = new Atom(LiteralAtom.createNilAtom());
			}
			else if(exp.isNumeralAtom())
			{
				retVal = exp;
			}
			else if(isBound(exp, a))
			{
				retVal = getVal(exp.print(), a);
			}
			else
			{
				String msg = exp.print() + " is an unbounded variable.";
				throw new LispIntException(msg, new Exception());
			}
		}
		// its a list
		else if(exp instanceof CSExp)
		{
			out.dump("SExp for eval is CSExp");
			//out.dump("car of " + exp.print() + " is " + evalCar(exp).print());
			if(evalCar(exp).tryAndGetLiteralAtomType()
				== LiteralAtom.Type.QUOTE)
			{
				SExp temp = evalCdr(exp);
				if(temp instanceof CSExp && !((CSExp)temp).right.isNilAtom())
				{
					String msg = "QUOTE expression : " + exp.print();
					msg += " should contain a list with single element!";
					retVal = new SExp(ErrorAtom.createErrorAtom(msg));
					throw new LispIntException(msg, new Exception());
				}
				else
				{
					retVal = evalCar(temp);
				}
			}
			else if(evalCar(exp).tryAndGetLiteralAtomType()
				== LiteralAtom.Type.COND)
			{
				retVal = evcon(evalCdr(exp), a, d);
			}
			else if(evalCar(exp).tryAndGetLiteralAtomType()
				== LiteralAtom.Type.DEFUN)
			{
				if((evalCar(evalCdr(exp))).tryAndGetLiteralAtomType() 
					!= LiteralAtom.Type.NOT_PRIMIITVE)
				{
					String msg = "Cannot define a DEFUN using pre-defined keywords";
					throw new LispIntException(msg, new Exception());
				}
				String defunName = (evalCar(evalCdr(exp))).print();
				if(d.containsKey(defunName))
				{
					String msg = "DEFUN already defined for " + defunName;
					throw new LispIntException(msg, new Exception());
				}
				else
				{
					SExp formalBodyList = evalCdr(evalCdr(exp));
					SExp formalList = evalCar(formalBodyList);
					SExp bodyList = evalCar(evalCdr(formalBodyList));

					//if(formalList.isUndefined() || bodyList.isUndefined())
					//{
					//	String msg = "DEFUN expects a formals list and body list!";
					//	throw new LispIntException(msg, new Exception());
					//}

					if(!bodyList.isNilAtom())
					{
						//out.dump("formalList " + formalList.print());
						//out.dump("bodyList " + bodyList.print());
						SExp combinedSExp = evalCONS(formalList, bodyList);
						//out.dump("combinedSExp " + combinedSExp.print());
						d.put(defunName, combinedSExp);
					}
					retVal = evalCar(evalCdr(exp)); // DEFUN name
				}

			}
			else
			{
				retVal = apply(	evalCar(exp), 
								evlist(evalCdr(exp), a, d),
								a, d);
			}
			
		}
		else
		{
			String msg = "Shouldn't have reached here -- SExp passed to eval()"
				+ " is neither Atom nor CSExp";
			throw new LispIntException(msg, new Exception());
		}
		return retVal;
	}

	public static SExp evlist(SExp x,
		HashMap<String, Stack<SExp>> a, 
		HashMap<String, SExp> d) throws LispIntException
	{
		SExp retVal = new Atom(LiteralAtom.createNilAtom());
		if(!x.isNilAtom())
		{
			retVal = evalCONS(eval(evalCar(x), a, d),
				evlist(evalCdr(x), a, d));
		}

		return retVal;
	}

	public static SExp evcon(SExp x,
		HashMap<String, Stack<SExp>> a, 
		HashMap<String, SExp> d) throws LispIntException
	{
		if(evalNULL(x).isTrueAtom())
		{
			String msg = "COND has to be followed by a boolean condition and expression! ";
			throw new LispIntException(msg, new Exception());
		}
		SExp retVal = new SExp();
		if(eval(evalCar(evalCar(x)), a, d).isTrueAtom())
		{
			retVal = eval(evalCar(evalCdr(evalCar(x))), a, d);
		}
		else
		{
			retVal = evcon(evalCdr(x), a, d);
		}
		return retVal;
	}

	public static SExp apply(SExp f, SExp x, 
		HashMap<String, Stack<SExp>> a, 
		HashMap<String, SExp> d) throws LispIntException
	{
		SExp retVal = new SExp();
		if(f.isAtom())
		{
			switch (f.tryAndGetLiteralAtomType())
			{
				case CAR :
					retVal = evalCar(evalCar(x));
					break;
				case CDR :
					retVal = evalCdr(evalCar(x));
					break;
				case CONS :
					retVal = evalCONS(evalCar(x), evalCar(evalCdr(x)));
					break;
				case ATOM :
					retVal = evalAtom(evalCar(x));
					break;
				case NULL :
					retVal = evalNULL(evalCar(x));
					break;
				case INT :
					retVal = evalINT(evalCar(x));
					break;
				case EQ :
					retVal = evalEQ(evalCar(x), evalCar(evalCdr(x)));
					break;
				case PLUS:
				case MINUS:
				case TIMES:
				case QUOTIENT:
				case REMAINDER:
					retVal = evalArithmeticFns(evalCar(x),
						evalCar(evalCdr(x)),
						f.tryAndGetLiteralAtomType());
					break;
				case LESS:
				case GREATER:
					retVal = evalRelFns(evalCar(x),
						evalCar(evalCdr(x)),
						f.tryAndGetLiteralAtomType());
					break;
				default :
					SExp combinedSExp = getValFromDList(f.print(), d);
					//out.dump("Apply called for " + f.print());
					//out.dump("formalList = " + evalCar(combinedSExp).print());
					//out.dump("actualList = " + x.print());
					// Update
					HashMap<String, Stack<SExp>> updatedA = 
									new HashMap<String, Stack<SExp>>(a);
					addPairs(evalCar(combinedSExp), x, updatedA);

					//out.dump("Dumping a list after addPairs");

					// for(String key : updatedA.keySet())
					// {
					// 	Stack<SExp> temp = updatedA.get(key);
					// 	out.dump("For key :" + key + " values are :" + Arrays.toString(temp.toArray()));
					// }
					// out.dump("-----end of list-----");
					retVal = eval(evalCdr(combinedSExp),
									updatedA,
									d);
					break;
			}
		}
		else
		{
			String msg = "IN apply() : " + f.print();
			msg += " should have been an ATOM!";
			throw new LispIntException(msg, new Exception()); 
		}
		return retVal;
	}

	public static SExp evalAtom(SExp exp) throws LispIntException
	{
		SExp retVal = new Atom(LiteralAtom.createNilAtom());
		if(exp.isAtom())
		{
			retVal = new Atom(LiteralAtom.createTrueAtom());
		}
		return retVal;
	}

	public static SExp evalCar(SExp exp) throws LispIntException
	{
		// call "null" on SExp also?
		if(!(exp instanceof CSExp))
		{
			String msg = "'car' cannot be evaluated for " + exp.print();
			msg += " because its an ATOM.";
			throw new LispIntException(msg, new Exception());
		}

		return (((CSExp)exp).left);
	}

	public static SExp evalCdr(SExp exp) throws LispIntException
	{
		// TODO : Undefined for () ? & call "null" on SExp also?
		if(!(exp instanceof CSExp))
		{
			String msg = "'cdr' cannot be evaluated for " + exp.print();
			msg += " because its an ATOM.";
			throw new LispIntException(msg, new Exception());
		}

		return ((CSExp)exp).right;
	}

	public static SExp evalNULL(SExp exp) throws LispIntException
	{
		SExp retVal;
		if(exp.isNilAtom())
		{
			retVal = new SExp(LiteralAtom.createTrueAtom());
		}
		else
		{
			retVal = new SExp(LiteralAtom.createNilAtom());
		}
		return retVal;
	}

	public static SExp evalINT(SExp exp) throws LispIntException
	{
		SExp retVal;
		if(exp.isNumeralAtom())
		{
			retVal = new SExp(LiteralAtom.createTrueAtom());
		}
		else
		{
			retVal = new SExp(LiteralAtom.createNilAtom());
		}
		return retVal;
	}

	public static SExp evalEQ(SExp exp1, SExp exp2) throws LispIntException
	{
		SExp retVal;
		if(!(exp1.isAtom() && exp2.isAtom()))
		{
			String msg = "EQ expects pair of atoms but was passed these: ";
			msg +=  exp1.print() + " , " + exp2.print() + "...";
			retVal = new SExp(ErrorAtom.createErrorAtom(msg));
			throw new LispIntException(msg, new Exception());
		}

		if(((Atom)exp1).eq((Atom)exp2))
		{
			retVal = new SExp(LiteralAtom.createTrueAtom());
		}
		else
		{
			retVal = new SExp(LiteralAtom.createNilAtom());
		}
		return retVal;
	}

	public static SExp evalCONS(SExp e1, SExp e2) throws LispIntException
	{
		CSExp retVal = new CSExp(e1, e2);
		retVal.setTerminalChar1("(");
		retVal.setTerminalChar2(")");
		return retVal;
	}

	public static SExp evalArithmeticFns(SExp e1,
		SExp e2,
		LiteralAtom.Type op) throws LispIntException
	{
		SExp retVal = new SExp();
		if(!(e1.isNumeralAtom() && e2.isNumeralAtom()))
		{
			String msg = op.toString() + " expects both operands to be ";
			msg += " integers, but is passed " + e1.print() + ", " + e2.print();
			throw new LispIntException(msg, new Exception());
		}

		int x = ((NumeralAtom)(e1.getToken())).getValue();
		int y = ((NumeralAtom)(e2.getToken())).getValue();

		switch (op)
		{
			case PLUS:
				retVal = new Atom(new NumeralAtom(x+y));
				break;
			case MINUS:
				retVal = new Atom(new NumeralAtom(x-y));
				break;
			case TIMES:
				retVal = new Atom(new NumeralAtom(x*y));
				break;
			case QUOTIENT:
				retVal = new Atom(new NumeralAtom(x/y));
				break;
			case REMAINDER:
				// int rem;
				// if(Math.abs(x) > Math.abs(y))
				// {
				// 	rem = Math.abs(x) - Math.abs(y);
				// 	if(x < 0)
				// 		rem *= -1;
				// }
				// else
				// {
				// 	rem = Math.abs(y) - Math.abs(x);
				// 	if(y < 0)
				// 		rem *= -1;
				// }
				retVal = new Atom(new NumeralAtom(x % y));
				break;
			default:
				String msg = op.toString() + " is not a valid arithmetic function";
				msg += ". Shouldn't have reached here!";
				throw new LispIntException(msg, new Exception());
		}

		return retVal;

	}

	public static SExp evalRelFns(SExp e1,
		SExp e2,
		LiteralAtom.Type op) throws LispIntException
	{
		SExp retVal = new Atom(LiteralAtom.createNilAtom());
		if(!(e1.isNumeralAtom() && e2.isNumeralAtom()))
		{
			String msg = op.toString() + " expects both operands to be ";
			msg += " integers, but is passed " + e1.print() + ", " + e2.print();
			throw new LispIntException(msg, new Exception());
		}

		int x = ((NumeralAtom)(e1.getToken())).getValue();
		int y = ((NumeralAtom)(e2.getToken())).getValue();

		switch (op)
		{
			case LESS:
				if(x < y)
				{
					retVal = new Atom(LiteralAtom.createTrueAtom());
				}
				break;
			case GREATER:
				if(x > y)
				{
					retVal = new Atom(LiteralAtom.createTrueAtom());
				}
				break;
			default:
				String msg = op.toString() + " is not a valid relational function";
				msg += ". Shouldn't have reached here!";
				throw new LispIntException(msg, new Exception());
		}

		return retVal;
	}

	// Set of helper methods for the interpreter

	public static void addPairs(SExp varList, SExp valueList, 
		HashMap<String, Stack<SExp>> a) throws LispIntException
	{
		if(evalNULL(varList).isTrueAtom() && evalNULL(valueList).isTrueAtom())
		{
			return;
		}

		SExp x = evalCar(varList);
		SExp y = evalCar(valueList);

		//if(evalNULL(x).isTrueAtom() || evalNULL(y).isTrueAtom())
		if(x.isUndefined() || y.isUndefined())
		{
			String msg = "addPairs called with different sizes of formals and ";
			msg += "actual lists!";
			msg +="\n varList : " + varList.print() + " valueList : " + valueList.print();
			throw new LispIntException(msg, new Exception());
		}

		if(x.isAtom())
		{
			if(a.containsKey(x.getToken().getTokenStringValue()))
			{
				a.get(x.getToken().getTokenStringValue()).push(y);
			}
			else
			{
				Stack<SExp> s = new Stack<SExp>();
				s.push(y);
				a.put(x.getToken().getTokenStringValue(), s);
			}
		}
		else
		{
			String msg = "addPairs reads in a list of ATOM but either of these";
			msg += " or both are not ATOM : " + x.print() + ", " + y.print();
			throw new LispIntException(msg, new Exception());
		}

		addPairs(evalCdr(varList), evalCdr(valueList), a);
	}

	public static boolean isBound(SExp exp
		, HashMap<String, Stack<SExp>> a) throws LispIntException
	{
		boolean retVal = false;
		if(!exp.isAtom())
		{
			String msg = "Trying to check isBound() on: " + exp.print();
			msg += " , which is not an ATOM!";
		}
		
		if(a.containsKey(exp.print()))
		{
			retVal = true;
		}
		return retVal;

	}

	public static SExp getVal( String key, 
		HashMap<String, Stack<SExp>> a) throws LispIntException
	{
		SExp retVal;
		if(a.containsKey(key))
		{
			retVal = a.get(key).peek();
		}
		else
		{
			String msg = "In getVal()- shouldn't have reached here! Pre-condition getBound not checked.";
			msg += key + " is not defined in the 'a' list";
			retVal = new SExp(ErrorAtom.createErrorAtom(msg));
			throw new LispIntException(msg, new Exception());
		}
		return retVal;
	}

	public static SExp getValFromDList(String key, 
		HashMap<String, SExp> d) throws LispIntException
	{
		SExp retVal;
		if(d.containsKey(key))
		{
			retVal = d.get(key);
		}
		else
		{
			String msg = "Trying to call undefined user function : " + key;
			throw new LispIntException(msg, new Exception());
		}
		return retVal;
	}
}