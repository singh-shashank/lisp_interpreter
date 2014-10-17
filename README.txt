CSE 6341 - LISP INTERPRETER PROJECT (JAVA version)
SUBMITTED BY :- SHASHANK SINGH
--------------------------------------------------------------------------
--------------------------------------------------------------------------

Contents:
----------
1. Building the project
2. Executing the interpreter
3. Design
4. References

--------------------------------------------------------------------------

1. Building the project
-------------------------

make -> builds the project generating all the class files in the bin directory.

make clean -> clears out the "bin" directory

2. Executing the interpreter
-----------------------------

java -cp bin MyInt < "input_filename" > "output_filename"

The above command is the only suggested way of running the interpreter.

Following are few flags that supported by the interpreter:

-dbg -> flag for enabling debug statements

-pdot -> can be used to print the S-Expressions in "dot" notations

-cont -> is a flag for continuing on errors (not fully supported in this version yet)

NOTE: This interpreter doesn't yet support an interactive mode where an expression is entered at press of the return key. I am planning to do that for the final submission, but for this submission, you can enter the input at the console but for marking the end of the input, please type in "#EOF" after which the complete input program will be evaluated


3. Design
-----------
This project comprises of the following components:

a) Lexical Analyzer
_____________________

- The input lines are tokenized at two levels.

- First all the space seperated tokens are extracted.
	- this includes tabs, newline characters, standard white spaces

- Second tokenization happens to extract the actual tokens of the grammar.
	- i.e. remove '(' and ')' when they are not space seperated with atoms.

- Then the set of tokens are converted to actual grammar tokens, which 
  essentially have the following categories:

  NUMERAL_ATOM,
  LITERAL_ATOM,
  DOT,
  OPEN_PARAN,
  CLOSE_PARAN,
  ERROR,
  EOF,
  UNDEF

- The "Token" class is a base class for Numeral and Literal Atom subclasses.

- "#EOF" is categorized as a special token string to mark the end of the file.

- Numeral Atom class stores values in integer form but have utlity methods for 	 returning actual token string values for printing.

- All the tokens are not read at once and are read "on-demand" by the parser.

- The logic is to put the tokens in a queue for Parser to read from and if the queue is empty, read the next set of tokens.

- So in case of errors we stop reading the tokens.

b) Parser
__________

 - Parser fetches grammer tokens from the Lexical Analyzer.

 - I make use of the grammar specified in the project description.

 - I define a LL(1) parser table at the initialization of the Parser.

 - This table is used for applying productions while parsing S-Expressions.

 - Following is the parse table for the grammar provided


 		atom 	( 	) 	. 	#EOF
 --------------------------------------
<S>		 0 		0			
<E>		 1		2	
<X>		 3		3	4
<Y>		 6 		6 	6 	5
<R>		 8 		8	7		8

where the productions are:
0. <S> ::= <E>
1. <E> ::= atom
2. <E> ::= (<X>
3. <X> ::= <E><Y>
4. <X> ::= )
5. <Y> ::= .<E>)
6. <Y> ::= <R>)
7. <R> ::= $
8. <R> ::=<E><R>

 - The input tokens are parsed and the S-Expression trees are generated incrementally.

 - All the expressions are stored in their DOT notation formats but as directed by the requirements they are printed in the list notation when necessary.

 - Two stacks are used - one for actual parsing and other for creating a S-Exp tree after a successful parsing

c) Error Handling
__________________

- A new 'LispIntException' is implemented extending the 'Exception' class of Java.

- As much as possible, meaningful error message are thrown at the user with a possible fix.

- Output handler class interfaces with the system's output console and is responsible for managing and printing debug statements and other outputs of the interpreter.


4. Testing
-----------
Comprehensive testing was done for almost all the possible cases that I could think.


4. References
--------------

http://web.cse.ohio-state.edu/~rountev/5343/pdf/SyntaxAnalysis.pdf
http://en.wikipedia.org/wiki/LL_parser
