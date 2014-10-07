import java.lang.Object;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

class LispInt
{
	OutputHandler out = new OutputHandler();
	Logger log = Logger.getLogger(LispInt.class.getName());
	String inputFileName = "";
	String outputFileName = "";

	private void parseArgs(String[] args) throws Exception
	{
		if(args.length == 0)
		{
			out.errorMessage("No command line arguments passed - Not yet implemented");
			throw new Exception();
		}
		else if(args.length < 4)
		{
			out.errorMessage("Less than 4 arguments passed");
			throw new Exception();
		}
		else
		{
			/*System.out.println("Dumping arguments");	
			for(int i=0;i<args.length; ++i)
			{
				System.out.println(i + " " + args[i]);	
			}*/
			if(args[0].equals("<"))
			{
				if(args[2].equals(">"))
				{
					if(!args[1].isEmpty())
					{
						inputFileName = args[1];
						if(!args[3].isEmpty())
						{
							outputFileName = args[3];

						}
					}
				}
			}
		}
	}

	public void start(String[] args)
	{
		// Read filenames
		try
		{
			parseArgs(args);
			Lex lex = new Lex(inputFileName, out);			
			String token = lex.getNextToken();
			while(!token.equals("EOF"))
			{
				System.out.println(token);
				token = lex.getNextToken();
			}
		}
		catch(Exception e)
		{
			out.errorMessage("\nException Caught - " + e.getMessage());
			out.errorMessage("\nDumping stack trace");
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

    
    
