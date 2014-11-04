class EvalSExp
{
	public static OutputHandler out = OutputHandler.getInstance();
	public static SExp eval(SExp exp) throws LispIntException
	{
		// exp is an atom
		SExp retVal = new SExp();
		if(exp instanceof Atom)
		{
			out.dump("SExp for eval is atom");
			Atom a = (Atom)exp;
			if(a.isNilAtom())
			{
				retVal = exp;
			}
			else if(a.isTrueAtom())
			{
				retVal = exp;
			}
			else if(a.getToken() instanceof NumeralAtom)
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
			retVal = evalCdr(exp);
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
}