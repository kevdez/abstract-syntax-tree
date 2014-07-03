package crux;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class Scanner implements Iterable<Token> {
	public static String studentName = "XXXXXXXX XXXXXXXX";
	public static String studentID = "XXXXXXXX";
	public static String uciNetID = "XXXXXXXX";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next untokenized char (-1 == EOF, or values from 0 to 65535)
	private Reader input;
	
	Scanner(Reader reader)
	{
		// TODO: initialize the Scanner
		this.lineNum = 1;
		this.charPos = 1;
		this.input = reader;
		try {
			this.nextChar = reader.read();
			if(this.nextChar == 10)
				this.lineNum++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Scanner constructor: IOException...");
			e.printStackTrace();
		}
	}	
	
	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.
	private int readChar() {
		try {
			if(this.nextChar == 10)
				this.charPos = 0;
			int temp = this.input.read();
			if(temp=='\n')
			{
				this.lineNum++;
				this.charPos = 1;
			}
			else if (temp == -1)
			{
				return temp;
			}
			else
			{
				this.charPos++;
			}
			return temp;
		} catch (IOException e) {
			System.out.println("Scanner.readChar(): IOException...");
			e.printStackTrace();
			return -1;
		}
}
	
	
	// TODO: implement this
	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 *  NOTE: this reads tokens as one character, two characters, many characters, or error
	 */
	public Token next()
	{
		// read until a non-space char occurs
		if(this.nextChar == 10)
			this.charPos--;
		while(Character.isWhitespace(this.nextChar))
		{
			this.nextChar = readChar();
		}
		
		int tokenLinNum = this.lineNum;
		int tokenCharPos = this.charPos;
		
		// SINGLE CHAR TOKENS:
		// if nextChar is an EOF | "(" | ")" | ... | ";"
		switch(this.nextChar)
		{
			case '/':
			{
				this.nextChar = readChar();
				if(this.nextChar == '/')	// a comment
				{
					readUntilNewLine();
					tokenLinNum = this.lineNum;
					tokenCharPos = this.charPos;
					if(this.nextChar == -1)
						return crux.Token.EOF(tokenLinNum, tokenCharPos+1);
					return next();
				}
				else
					return crux.Token.DIV(tokenLinNum, tokenCharPos);
			}
			case -1:
				this.nextChar = readChar();
				if(tokenCharPos == 0)
					tokenCharPos++;
				return crux.Token.EOF(tokenLinNum,tokenCharPos);
			case '(':
				this.nextChar = readChar();
				return crux.Token.OPEN_PAREN(tokenLinNum, tokenCharPos);
			case ')':
				this.nextChar = readChar();
				return crux.Token.CLOSE_PAREN(tokenLinNum, tokenCharPos);
			case '{':
				this.nextChar = readChar();
				return crux.Token.OPEN_BRACE(tokenLinNum, tokenCharPos);
			case '}':
				this.nextChar = readChar();
				return crux.Token.CLOSE_BRACE(tokenLinNum, tokenCharPos);
			case '[':
				this.nextChar = readChar();
				return crux.Token.OPEN_BRACKET(tokenLinNum, tokenCharPos);
			case ']':
				this.nextChar = readChar();
				return crux.Token.CLOSE_BRACKET(tokenLinNum, tokenCharPos);
			case '+':
				this.nextChar = readChar();
				return crux.Token.ADD(tokenLinNum, tokenCharPos);
			case '-':
				this.nextChar = readChar();
				return crux.Token.SUB(tokenLinNum, tokenCharPos);
			case '*':
				this.nextChar = readChar();
				return crux.Token.MUL(tokenLinNum, tokenCharPos);
			case ',':
				this.nextChar = readChar();
				return crux.Token.COMMA(tokenLinNum, tokenCharPos);
			case ';':
				this.nextChar = readChar();
				return crux.Token.SEMICOLON(tokenLinNum, tokenCharPos);
		}
		
//		TWO CHAR TOKENS
		if(this.nextChar == '>')
		{
			this.nextChar = readChar();
			if(this.nextChar == '=')
			{
				this.nextChar = readChar();
				return crux.Token.GREATER_EQUAL(tokenLinNum, tokenCharPos);
			}
			else
				return crux.Token.GREATER_THAN(tokenLinNum, tokenCharPos);			
		}
		else if (this.nextChar == '<')
		{
			this.nextChar = readChar();
			if(this.nextChar == '=')
			{
				this.nextChar = readChar();
				return crux.Token.LESSER_EQUAL(tokenLinNum, tokenCharPos);
			}
			else
				return crux.Token.LESS_THAN(tokenLinNum, tokenCharPos);
		}
		else if (this.nextChar == '!')
		{
			this.nextChar = readChar();
			if(this.nextChar == '=')
			{
				this.nextChar = readChar();
				return crux.Token.NOT_EQUAL(tokenLinNum, tokenCharPos);
			}
			else
				return crux.Token.ERROR("!", tokenLinNum, tokenCharPos);
		}
		else if (this.nextChar == '=')
		{
			this.nextChar = readChar();
			if(this.nextChar == '=')
			{
				this.nextChar = readChar();
				return crux.Token.EQUAL(tokenLinNum, tokenCharPos);
			}
			else
			{
				return crux.Token.ASSIGN(tokenLinNum, tokenCharPos);
			}
		}
		else if (this.nextChar == ':')
		{
			this.nextChar = readChar();
			if(this.nextChar == ':')
			{
				this.nextChar = readChar();
				return crux.Token.CALL(tokenLinNum, tokenCharPos);
			}
			else
			{
				return crux.Token.COLON(tokenLinNum, tokenCharPos);
			}
		}

//		MANY-CHAR TOKENS
		String tokenLexeme = String.copyValueOf(Character.toChars(this.nextChar));
		if(isNextCharADigit())
		{
			this.nextChar = readChar();
			while (isNextCharADigit() || this.nextChar == '.')	// loops for INTEGER and FLOAT tokens
			{
				tokenLexeme += String.copyValueOf(Character.toChars(this.nextChar));
				
				// FLOATS
				if(this.nextChar == '.')
				{
					this.nextChar = readChar();
					while(isNextCharADigit())
					{
						tokenLexeme += String.copyValueOf(Character.toChars(this.nextChar));
						this.nextChar = readChar();
					}
					return crux.Token.FLOAT(tokenLexeme, tokenLinNum, tokenCharPos);
				}
				this.nextChar = readChar();
			}
			return crux.Token.INTEGER(tokenLexeme, tokenLinNum, tokenCharPos);
		}
		else if(isNextCharALetter())
		{
			this.nextChar = readChar();
			while(isNextCharALetter() || isNextCharADigit())
			{
				tokenLexeme += String.copyValueOf(Character.toChars(this.nextChar));
				if(		tokenLexeme.equals("and") || tokenLexeme.equals("or") || tokenLexeme.equals("not") || tokenLexeme.equals("let")
						|| tokenLexeme.equals("var") || tokenLexeme.equals("array") || tokenLexeme.equals("func") || tokenLexeme.equals("if")
						|| tokenLexeme.equals("else") || tokenLexeme.equals("while") || tokenLexeme.equals("true") || tokenLexeme.equals("false")
						|| tokenLexeme.equals("return") )
				{
					this.nextChar = readChar();
					if(isNextCharALetter() || isNextCharADigit())		// continuing token, unrecognized key word, probably identifier
					{
						tokenLexeme += String.copyValueOf(Character.toChars(this.nextChar));
						this.nextChar = readChar();
						while (isNextCharALetter() || isNextCharADigit())
						{
							tokenLexeme += String.copyValueOf(Character.toChars(this.nextChar));
							this.nextChar = readChar();
						}
						return Token.IDENTIFIER(tokenLexeme, tokenLinNum, tokenCharPos);
					}
					else												// recognized key word token
						return new Token(tokenLexeme, tokenLinNum, tokenCharPos);
				}
				this.nextChar = readChar();
			}
			return Token.IDENTIFIER(tokenLexeme, tokenLinNum, tokenCharPos);
		}
		else
		{
			this.nextChar = readChar();
			return crux.Token.ERROR(tokenLexeme, tokenLinNum, tokenCharPos);
		}
	
	}

	

	// OPTIONAL: any other methods that you find convenient for implementation or testing
	@Override
	public Iterator<Token> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isNextCharADigit()
	{
		return this.nextChar == '0' || this.nextChar == '1' || this.nextChar == '2'
				|| this.nextChar == '3' || this.nextChar == '4' || this.nextChar == '5'
				|| this.nextChar == '6' || this.nextChar == '7' || this.nextChar == '8'
				|| this.nextChar == '9';
	}
	
	private boolean isNextCharALetter()
	{
		return this.nextChar == '_' || this.nextChar == 'a' || this.nextChar == 'b' || this.nextChar == 'c'
				|| this.nextChar == 'd' || this.nextChar == 'e' || this.nextChar == 'f' || this.nextChar == 'g' || this.nextChar == 'h'
				|| this.nextChar == 'i' || this.nextChar == 'j' || this.nextChar == 'k' || this.nextChar == 'l' || this.nextChar == 'm'
				|| this.nextChar == 'n' || this.nextChar == 'o' || this.nextChar == 'p' || this.nextChar == 'q' || this.nextChar == 'r'
				|| this.nextChar == 's' || this.nextChar == 't' || this.nextChar == 'u' || this.nextChar == 'v' || this.nextChar == 'w'
				|| this.nextChar == 'x' || this.nextChar == 'y' || this.nextChar == 'z' || this.nextChar == 'A' || this.nextChar == 'B'
				|| this.nextChar == 'C' || this.nextChar == 'D' || this.nextChar == 'E' || this.nextChar == 'F' || this.nextChar == 'G'
				|| this.nextChar == 'H' || this.nextChar == 'I' || this.nextChar == 'J' || this.nextChar == 'K' || this.nextChar == 'L'
				|| this.nextChar == 'M' || this.nextChar == 'N' || this.nextChar == 'O' || this.nextChar == 'P' || this.nextChar == 'Q'
				|| this.nextChar == 'R' || this.nextChar == 'S' || this.nextChar == 'T' || this.nextChar == 'U' || this.nextChar == 'V'
				|| this.nextChar == 'W' || this.nextChar == 'X' || this.nextChar == 'Y' || this.nextChar == 'Z';
	}
	
	private void readUntilNewLine()
	{
		while (this.nextChar != 10 && this.nextChar != -1)
		{
			this.nextChar = readChar();
		}
	}
	
}
