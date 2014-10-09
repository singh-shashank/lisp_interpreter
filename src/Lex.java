import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

/* TODO :
1) Implement a Token class and replace "String" for tokenQ
2) Process the raw word to a token class before putting in the queue.
3) Change "EOF" string to something else
*/

class Lex { 

    private static String ErrorString1 = "A numeral atom can begin with '+' or '-' and should" +
                                         " consists of digits(0-9) following it."+
                                         " A literal atom has to begin with a letter and can be" +
                                         " followed by arbitrary numbers of letters or digits"; 
	private OutputHandler out;
	private String inputFile;
    BufferedReader br;
    Queue<Token> tokensQ = new LinkedList<Token>();
    
    private void readNextSetOfToken() throws LispIntException, IOException
    {
        if(null == br)
        {
            br = new BufferedReader(new FileReader(inputFile));
        }
        String line;
        if((line = br.readLine()) != null)
        {
            StringTokenizer scanLine = new StringTokenizer(line);
            while(scanLine.hasMoreTokens())
            {
                // Split at paranthesis
                StringTokenizer parts = new StringTokenizer(scanLine.nextToken().toUpperCase(), "\\(|\\)", true);
                while(parts.hasMoreTokens())
                {
                    //tokensQ.add(scanLine.nextToken().toUpperCase());
                    convertToToken(parts.nextToken());
                }
            }
        }
        else
        {
            // push EOF here
            convertToToken("EOF");
            br.close();
        }

    }

    public Lex(String file, OutputHandler o)
    {
    	out = o;
    	inputFile = file;
    }

    public Token getNextToken() throws Exception
    {
        Token token = new Token();
        if(tokensQ.isEmpty())
        {
            // Read next set of tokens from the file
            while(tokensQ.isEmpty())
            {
                readNextSetOfToken();
            }
        }
        if(tokensQ.peek().getTokenType() == Token.TokenType.EOF)
        {
            token = tokensQ.peek();
        }
        else
        {
            token = tokensQ.remove();
        }
        return token;
    }

    private boolean isDigit(char c)
    {
        if(c >= '0' && c <= '9')
            return true;
        else
            return false;
    }

    void convertToToken(String value) throws LispIntException
    {
        Token result = new Token();

        if(value.equals("."))
        {
            result = new Token(value, Token.TokenType.DOT);
        }
        else if(value.equals("("))
        {
            result = new Token(value, Token.TokenType.OPEN_PARAN);
        }
        else if(value.equals(")"))
        {
            result = new Token(value, Token.TokenType.CLOSE_PARAN);
        }
        else if(value.equals("EOF"))
        {
            result = new Token(value, Token.TokenType.EOF);
        }
        else if(value.length() > 0)
        {
            if((isDigit(value.charAt(0))) || 
                (value.charAt(0) == '+') ||
                (value.charAt(0) == '-')
              )
            {
                // has to be a NUMERAL ATOM else error
                for(int i = 1; i<value.length(); ++i)
                {
                    if(!isDigit(value.charAt(i)))
                    {
                        throw new LispIntException("Token '" + value +
                            "' started with either '+' or '-' or a digit but is not a valid numeral atom." + 
                            ErrorString1
                            , new Exception());
                    }
                }
                result = new NumeralAtom(value);
            }
            else
            {
                // can be a literal atom
                result = new LiteralAtom(value);
            }
        }
        else
        {
            throw new LispIntException("Huh? An empty string token encountered! This shouldn't have happened!!",
                new Exception());
        }

        tokensQ.add(result);
    }
}
