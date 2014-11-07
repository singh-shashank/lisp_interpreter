import java.lang.Object;
import java.io.Reader;

class LispInt
{
	OutputHandler out = OutputHandler.getInstance();

	private void parseArgs(String[] args) throws Exception
	{
		for(int i=0;i<args.length;++i)
		{
			if(args[i].equals("-cont"))
			{
				out.isContOnError = true;
			}
			else if(args[i].equals("-dbg"))
			{
				out.isDebug = true;
			}
			else if(args[i].equals("-pdot"))
			{
				out.isPrintUsingDotNot = true;
			}
		}
	}

	public void start(String[] args)
	{
		// Read filenames
		try
		{
			parseArgs(args);
			Lex lex = new Lex();			
			Parser p = new Parser(lex);
			p.parseTokens();
		}
		catch(LispIntException lie)
		{
			out.errorMessage(lie.getCustomMessage());
			if(out.isDebug)
			{
				out.errorMessage("\n---------------------------------");
				out.errorMessage("\nDumping stack trace");
				lie.printStackTrace();
			}
		}
		catch(Exception e)
		{
			out.errorMessage("\nException Caught - " + e.getMessage());
			if(out.isDebug)
			{
				out.errorMessage("\n---------------------------------");
				out.errorMessage("\nDumping stack trace");
				e.printStackTrace();
			}
		}
		finally
		{
			out.dump();
		}
	}
}

class MyInt { 
	
    public static void main(String[] args) {	
		LispInt lInt = new LispInt();
		lInt.start(args);
    }
}