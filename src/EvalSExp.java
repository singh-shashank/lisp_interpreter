import java.util.*;

class EvalSExp
{
	public static OutputHandler out = OutputHandler.getInstance();
	public static SExp eval(SExp exp,
		HashMap<String, Stack<Token>> a, 
		HashMap<String, String> d) throws LispIntException // TODO : fix this
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
			else
			{
				String msg = exp.print() + " is not an ATOM.";
				throw new LispIntException(msg, new Exception());
			}
		}
		// its a list
		else if(exp instanceof CSExp)
		{
			out.dump("SExp for eval is CSExp");
			out.dump("car of " + exp.print() + " is " + evalCar(exp).print());
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
			else
			{
				out.dump("NOT YET IMPLEMENTED");
			}
			
		}
		else
		{
			out.dumpError("Shouldn't have reached here -- SExp passed to eval()"
				+ " is neither Atom nor CSExp");
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
}