import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

/* TODO :

*/

class Lex { 

    private static String ErrorString1 = "\nA numeral atom can begin with '+' or '-' and should" +
                                         " consists of digits(0-9) following it."+
                                         "\nA literal atom has to begin with a letter and can be" +
                                         " followed by arbitrary numbers of letters or digits"; 

    private static String EndOfFile = "#EOF";

	private OutputHandler out = OutputHandler.getInstance();
	private String inputFile;
    BufferedReader br;
    Queue<Token> tokensQ = new LinkedList<Token>();
    
    private void readNextSetOfToken() throws LispIntException, IOException
    {
        if(null == br)
        {
            if(!inputFile.isEmpty())
            {
                br = new BufferedReader(new FileReader(inputFile));
            }
            else
            {
                br = new BufferedReader(new InputStreamReader(System.in));
            }
        }
        String line;
        if((line = br.readLine()) != null)
        {
            StringTokenizer scanLine = new StringTokenizer(line);
            while(scanLine.hasMoreTokens())
            {
                // Split at paranthesis
                String t1 = scanLine.nextToken().toUpperCase();
                StringTokenizer parts = new StringTokenizer(t1, "(|)", true);
                while(parts.hasMoreTokens())
                {
                    //tokensQ.add(scanLine.nextToken().toUpperCase());
                    String t = parts.nextToken();
                    convertToToken(t);
                }
            }
        }
        else
        {
            // push EOF here
            convertToToken(EndOfFile);
            br.close();
        }

    }

    public Lex(String file)
    {
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

    public void addBackUnprocessedToken(Token t)
    {
        Queue<Token> temp = new LinkedList<Token>();
        while(!tokensQ.isEmpty())
        {
            temp.add(tokensQ.remove());
        }

        tokensQ.add(t);

        while(!temp.isEmpty())
        {
            tokensQ.add(temp.remove());
        }

    }

    private boolean isDigit(char c)
    {
        if(c >= '0' && c <= '9')
            return true;
        else
            return false;
    }

    private boolean isLetter(char c)
    {
        if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
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
        else if(value.equals(EndOfFile))
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
                        handleErrorToken(value, "Token '" + value +
                            "' started with either '+' or '-' or a digit but is not a valid numeral atom." + 
                            ErrorString1);
                        return;
                    }
                }
                result = new NumeralAtom(value);
            }
            else if(isLetter(value.charAt(0)))
            {
                // can be a literal atom
                for(int i = 1; i<value.length(); ++i)
                {
                    if( !(isLetter(value.charAt(i)) || isDigit(value.charAt(i))) )
                    {
                        handleErrorToken(value, "Token '" + value +
                                        "' started with a letter but is not a valid literal atom." +
                                        ErrorString1);
                        return;
                    }
                }
                result = new LiteralAtom(value);
            }
            else
            {
                handleErrorToken(value, "Token '" + value +
                    "' is not a valid token." + 
                    ErrorString1);
                return;
            }
        }
        else
        {
            handleErrorToken(value, "Huh? An empty string token encountered! This shouldn't have happened!!");
            return;
        }

        tokensQ.add(result);
    }

    private void handleErrorToken(String value, String msg) throws LispIntException
    {
        if(out.isContOnError)
        {
            tokensQ.add(new ErrorAtom(value, msg));
        }
        else
        {
            throw new LispIntException(msg, new Exception());
        }
    }
}
