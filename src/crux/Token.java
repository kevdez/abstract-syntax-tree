package crux;


/**
 * @author XXXXXXXX XXXXXXXX
 *	Student ID: XXXXXXXX
 *	UCInetID: XXXXXXXX
 */

public class Token {
	
	public static enum Kind {
		AND("and"),
		OR("or"),
		NOT("not"),
		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return"),
		
		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::"),
		
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF();
		
		private String default_lexeme;
		
		Kind()
		{
			default_lexeme = "";
		}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return default_lexeme != null;
		}
		
		// OPTIONAL: if you wish to also make convenience functions, feel free
		//           for example, boolean matches(String lexeme)
		//           can report whether a Token.Kind has the given lexeme
		public boolean matches(String lexeme)
		{
			for (Kind k : Kind.values())
			{
				if (k.default_lexeme.equals(lexeme))
					return true;
			}
			return false;
		}	
		
		public static Kind getKind(String lexeme)
		{
			for(Kind k : Kind.values())
			{
				if(k.default_lexeme.equals(lexeme))
					return k;
			}
			return null;
		}
	}
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	
	// OPTIONAL: implement factory functions for some tokens, as you see fit
	public static Token EOF(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EOF;
		return tok;
	}

	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}
	
	// intended to be used on reserved words
	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		this.kind = Kind.getKind(lexeme);
		this.lexeme = lexeme;
		if (this.kind == null)
		{
			// if we don't match anything, signal error
			this.kind = Kind.ERROR;
			this.lexeme = "Unrecognized lexeme: " + lexeme;
		}	
	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}
	
	// Return the lexeme representing or held by this token
	public String lexeme()
	{
		return this.lexeme;
	}
	
	public String toString()
	{
		if(this.lexeme.equals("No Lexeme Given")
				|| this.lexeme.equals("and")
				|| this.lexeme.equals("or")
				|| this.lexeme.equals("not")
				|| this.lexeme.equals("let")
				|| this.lexeme.equals("var")
				|| this.lexeme.equals("array")
				|| this.lexeme.equals("func")
				|| this.lexeme.equals("if")
				|| this.lexeme.equals("else")
				|| this.lexeme.equals("while")
				|| this.lexeme.equals("true")
				|| this.lexeme.equals("false")
				|| this.lexeme.equals("return")
				)
			return this.kind + "(lineNum:" + this.lineNum + ", charPos:" + this.charPos + ")";
		else
			return this.kind +"(" + this.lexeme + ")" + "(lineNum:" + this.lineNum + ", charPos:" + this.charPos + ")";
			
	}
	
	// OPTIONAL: function to query a token about its kind
	//           boolean is(Token.Kind kind)
	public boolean is(Token.Kind kind)
	{
		if(this.kind.equals(kind))
			return true;
		else
			return false;
	}
	
	// OPTIONAL: add any additional helper or convenience methods
	//           that you find make for a clean design
	public static Token OPEN_PAREN(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_PAREN;
		return tok;
	}
	
	public static Token CLOSE_PAREN(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_PAREN;
		return tok;
	}
	
	public static Token OPEN_BRACE(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_BRACE;
		return tok;
	}
	
	public static Token CLOSE_BRACE(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_BRACE;
		return tok;
	}
	
	public static Token OPEN_BRACKET(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_BRACKET;
		return tok;
	}
	
	public static Token CLOSE_BRACKET(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_BRACKET;
		return tok;
	}
	
	public static Token ADD(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.ADD;
		return tok;
	}
	
	public static Token SUB(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.SUB;
		return tok;
	}
	
	public static Token MUL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.MUL;
		return tok;
	}
	
	public static Token DIV(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.DIV;
		return tok;
	}
	
	
	public static Token GREATER_EQUAL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.GREATER_EQUAL;
		return tok;
	}
	
	public static Token LESSER_EQUAL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.LESSER_EQUAL;
		return tok;
	}
	
	public static Token NOT_EQUAL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.NOT_EQUAL;
		return tok;
	}
	
	public static Token EQUAL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EQUAL;
		return tok;
	}
	
	
	public static Token GREATER_THAN(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.GREATER_THAN;
		return tok;
	}
	
	public static Token LESS_THAN(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.LESS_THAN;
		return tok;
	}
	
	public static Token ASSIGN(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.ASSIGN;
		return tok;
	}
	
	public static Token COMMA(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.COMMA;
		return tok;
	}
	
	public static Token SEMICOLON(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.SEMICOLON;
		return tok;
	}
	
	public static Token COLON(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.COLON;
		return tok;
	}
	
	public static Token CALL(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CALL;
		return tok;
	}
	
	public static Token INTEGER(String name, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.INTEGER;
		tok.lexeme = name;
		return tok;
	}
	
	public static Token FLOAT(String name, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.FLOAT;
		tok.lexeme = name;
		return tok;
	}
	
	public static Token IDENTIFIER(String name, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.IDENTIFIER;
		tok.lexeme = name;
		return tok;
	}
	
	public static Token ERROR(String lex, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.ERROR;
		tok.lexeme = "Unexpected character: " + lex;
		return tok;
	}

	public Kind kind() {
		return this.kind;
	}
}
