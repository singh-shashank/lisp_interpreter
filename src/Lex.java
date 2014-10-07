import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

/* TODO :
1) Implement a Token class and replace "String" for tokenQ
2) Process the raw word to a token class before putting in the queue.
*/

class Lex { 
	private OutputHandler out;
	private String inputFile;
    BufferedReader br;
    Queue<String> tokensQ = new LinkedList<String>();
    
    private void readNextSetOfToken() throws IOException
    {
        if(null == br)
        {
            br = new BufferedReader(new FileReader(inputFile));
        }
        String line;
        if((line = br.readLine()) != null)
        {
            StringTokenizer scanLine = new StringTokenizer(line);
            //scanLine.useDelimiter("\\t|\\n| +");
            while(scanLine.hasMoreTokens())
            {
                tokensQ.add(scanLine.nextToken().toUpperCase());
            }
        }
        else
        {
            // push EOF here
            tokensQ.add("EOF");
            br.close();
        }

    }

    public Lex(String file, OutputHandler o)
    {
    	out = o;
    	inputFile = file;
    }

    public String getNextToken() throws Exception
    {
        String token;
        if(tokensQ.isEmpty())
        {
            // Read next set of tokens from the file
            while(tokensQ.isEmpty())
            {
                readNextSetOfToken();
            }
        }
        if(tokensQ.peek().equals("EOF"))
        {
            token = "EOF";
        }
        else
        {
            token = tokensQ.remove();
        }
        return token;
    }
}
