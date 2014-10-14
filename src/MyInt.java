import java.lang.Object;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

class LispInt
{
	OutputHandler out = OutputHandler.getInstance();
	Logger log = Logger.getLogger(LispInt.class.getName());
	String inputFileName = "";
	String outputFileName = "";

	private void parseArgs(String[] args) throws Exception
	{
		for(int i=0;i<args.length;++i)
		{
			if(args[i].equals("-cont"))
			{
				out.isContOnError = true;
			}
			else if(args[i].equals("-bdtg"))
			{
				out.isDebug = true;
			}
		}
	}

	public void start(String[] args)
	{
		// Read filenames
		try
		{
			parseArgs(args);
			Lex lex = new Lex(inputFileName);			
			Parser p = new Parser(lex);
			p.parseTokens();
		}
		catch(LispIntException lie)
		{
			out.errorMessage(lie.getCustomMessage());
			//out.errorMessage("\nDumping stack trace");
			//lie.printStackTrace();
			//log.info (e.getMessage());
		}
		catch(Exception e)
		{
			out.errorMessage("\nException Caught - " + e.getMessage());
			//out.errorMessage("\nDumping stack trace");
			e.printStackTrace();
			//log.info (e.getMessage());
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