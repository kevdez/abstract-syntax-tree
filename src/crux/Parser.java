// The grammars on this file are written in Wirth notation
// http://en.wikipedia.org/wiki/Wirth_syntax_notation
// {} mean 0 or more copies
// [] means optional, either it's there or it isn't

package crux;

import java.util.ArrayList;
import java.util.Stack;
import ast.Command;

public class Parser {
    public static String studentName = "Kevin Hernandez";
    public static String studentID = "90872295";
    public static String uciNetID = "khernan3";
    
// Parser ==========================================
    
    private Scanner scanner;
    private Token currentToken;
    
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        this.currentToken = scanner.next();
    }
    
    public ast.Command parse()
    {
        initSymbolTable();
        try {
            return program();
        } catch (QuitParseException q) {
        	return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
        }
    }
    
// SymbolTable Management ==========================
    private SymbolTable symbolTable;

    private void initSymbolTable()
    {
    	this.symbolTable = new SymbolTable();
    	this.symbolTable.setDepth(0);
    	this.symbolTable.insert("readInt");
    	this.symbolTable.insert("readFloat");
    	this.symbolTable.insert("printBool");
    	this.symbolTable.insert("printInt");
    	this.symbolTable.insert("printFloat");
    	this.symbolTable.insert("println");
    }
    
    private void enterScope()
    {
    	SymbolTable temp = new SymbolTable();
        
        temp.setDepth(symbolTable.getDepth() + 1);
        symbolTable.setChildSymbolTable(temp);
        temp.setParentSymbolTable(symbolTable);
        symbolTable = temp;
    }
    
    private void exitScope()
    {
    	if(symbolTable.getParentSymbolTable() != null)
    		symbolTable = symbolTable.getParentSymbolTable();
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }    

// Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind);
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }    
    
    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
       
    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
    }
    
    @SuppressWarnings("unused")
	private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
    }
    
    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
    }         
    
    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
    }

    

 // Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();

    public void enterRule(NonTerminal nonTerminal) {
        String lineData = new String();
        for(int i = 0; i < parseTreeRecursionDepth; i++)
        {
            lineData += "  ";
        }
        lineData += nonTerminal.name();
        //System.out.println("descending " + lineData);
        parseTreeBuffer.append(lineData + "\n");
        parseTreeRecursionDepth++;
    }
    
    private void exitRule(NonTerminal nonTerminal)
    {
        parseTreeRecursionDepth--;
    }
    
    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }
    
    // Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
         
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    public String errorReport()
    {
    	return errorBuffer.toString();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private int lineNumber()
    {
        return currentToken.lineNumber();
    }
    
    private int charPosition()
    {
        return currentToken.charPosition();
    }
    
    
 
 // Grammar Rules =====================================================
    
 // literal := INTEGER | FLOAT | TRUE | FALSE .
    public ast.Expression literal()
    {
        ast.Expression expr;
        enterRule(NonTerminal.LITERAL);
        
        Token tok = expectRetrieve(NonTerminal.LITERAL);
        expr = Command.newLiteral(tok);
        
        exitRule(NonTerminal.LITERAL);
        return expr;
    }
 
// type := IDENTIFIER .
 public void type()
 { 	
 	expect(Token.Kind.IDENTIFIER);
 }
 
// op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
 public Token op0()
 {
     enterRule(NonTerminal.OP0);
     Token tok = expectRetrieve(NonTerminal.OP0);
     exitRule(NonTerminal.OP0);
     return tok;
 }
 
// expression0 := expression1 [ op0 expression1 ] .
 public ast.Expression expression0()
 {	
	 ast.Expression leftSide;
	 leftSide = expression1();
 	if (have(NonTerminal.OP0))
 	{
 		Token tok = op0();
 		ast.Expression rightSide = expression1();
 		return Command.newExpression(leftSide, tok, rightSide);
 	}
 	return leftSide;
}
 
//op1 := "+" | "-" | "or" .
 public Token op1()
{
	 enterRule(NonTerminal.OP1);
	 Token tok = expectRetrieve(NonTerminal.OP1);
	 exitRule(NonTerminal.OP1);
	 return tok;
}

// expression1 := expression2 { op1  expression2 } .
 public ast.Expression expression1()
 {	
	ArrayList<ast.Expression> expressions = new ArrayList<ast.Expression>();
	ArrayList<Token> tokens = new ArrayList<Token>();
	
	expressions.add(expression2());
	while(have(NonTerminal.OP1))
 	{
 		tokens.add(op1());
 		expressions.add(expression2());
 	}
 	
	ast.Expression temp = expressions.remove(0);
 	while(!expressions.isEmpty())
 	{
 		temp = Command.newExpression(temp, tokens.remove(0), expressions.remove(0));
 	}
 	return temp;
 }
 
//op2 := "*" | "/" | "and" .
public Token op2()
{
   enterRule(NonTerminal.OP2);
   Token tok = expectRetrieve(NonTerminal.OP2);
   exitRule(NonTerminal.OP2);
   return tok;
}

// expression2 := expression3 { op2 expression3 } .
 public ast.Expression expression2()
 {
	 ArrayList<ast.Expression> expressions = new ArrayList<ast.Expression>();
	 ArrayList<Token> tokens = new ArrayList<Token>();
		
	 expressions.add(expression3());
	 while (have(NonTerminal.OP2)) 
	 {
 		tokens.add(op2());
 		expressions.add(expression3());
	 }
	 
	 ast.Expression temp = expressions.remove(0);
	 while(!expressions.isEmpty())
	 {
	 	temp = Command.newExpression(temp, tokens.remove(0), expressions.remove(0));
	 }
	 return temp;
 }
 
// expression3 := "not" expression3
// 	       | "(" expression0 ")"
// 	       | designator
// 	       | call-expression
// 	       | literal .
 public ast.Expression expression3()
 { 	
	 ast.Expression result = null; 
	 int lin = lineNumber();
	 int cha = charPosition();
 	if(have(Token.Kind.NOT))
 	{
 		expect(Token.Kind.NOT);
 		result = new ast.LogicalNot(lin, cha, expression3());
 	}
 	else if(have(Token.Kind.OPEN_PAREN))
   	{
   		accept(Token.Kind.OPEN_PAREN);
   		result = expression0();
   		expect(Token.Kind.CLOSE_PAREN);
   	}
 	else if (have(NonTerminal.DESIGNATOR)) 
 	{
        result = designator();
    } 
 	else if (have(NonTerminal.CALL_EXPRESSION)) 
 	{
        result = call_expression();
    } 
 	else if (have(NonTerminal.LITERAL)) 
 	{
        result = literal();
    } 
 	else
 	{
//        String message = 
        		reportSyntaxError(NonTerminal.EXPRESSION3);
    }
 	return result;
 }
 
// call-expression := "::" IDENTIFIER "(" expression-list ")" .
 public ast.Call call_expression()
 {
	 int linNum = lineNumber();
	 int charPos = charPosition();
	expect(Token.Kind.CALL);
 	Symbol sym = tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));

 	
 	expect(Token.Kind.OPEN_PAREN);
 	ast.ExpressionList elist = expression_list();
 	expect(Token.Kind.CLOSE_PAREN);
 	
 	return new ast.Call(linNum, charPos, sym, elist);
 }
 
// expression-list := [ expression0 { "," expression0 } ] .
 public ast.ExpressionList expression_list()
 {
	 ast.ExpressionList result = new ast.ExpressionList(lineNumber(), charPosition());
	 
 	if(have(NonTerminal.EXPRESSION0))
 	{
 		result.add(expression0());
 		while(accept(Token.Kind.COMMA))
 		{
 			result.add(expression0());
 		}
 	}
 	
 	return result;
 }
 
// parameter := IDENTIFIER ":" type .
 public Symbol parameter()
 {
	Symbol sym = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
 	
	expect(Token.Kind.COLON);
 	type();
 	
 	return sym;
 }
 
// parameter-list := [ parameter { "," parameter } ] .
 public ArrayList<Symbol> parameter_list()
 {	
	 ArrayList<Symbol> list = new ArrayList<Symbol>();
 	if(have(NonTerminal.PARAMETER))
 	{
 		list.add(parameter());
 		while(accept(Token.Kind.COMMA))
 		{
 			list.add(parameter());
 		}
 	}
 	return list;
 }
 
// variable-declaration := "var" IDENTIFIER ":" type ";"
 public ast.VariableDeclaration variable_declaration()
 {
	 ast.VariableDeclaration vari;
	 int lin = lineNumber();
	 int cha = charPosition();
	 enterRule(NonTerminal.VARIABLE_DECLARATION);
	 expect(Token.Kind.VAR);
	 
	 vari = new ast.VariableDeclaration(lin, cha, tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER)));
			 
	 expect(Token.Kind.COLON);
	 type();
	 expect(Token.Kind.SEMICOLON);
	 
	 exitRule(NonTerminal.VARIABLE_DECLARATION);
	 
	 return vari;
	 
}
 
// designator := IDENTIFIER { "[" expression0 "]" } .
 public ast.Expression designator()
 {
	 enterRule(NonTerminal.DESIGNATOR);
	 
	 ast.Expression expr;
	 int lineNum = lineNumber();
     int charPos = charPosition();
     
	 Symbol sym = tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
     ast.AddressOf addr = new ast.AddressOf(lineNum, charPos, sym);
	 expr = new ast.Dereference(lineNum, charPos, addr);
	 
     while (accept(Token.Kind.OPEN_BRACKET)) {
         expression0();
         expect(Token.Kind.CLOSE_BRACKET);
     }
     
     exitRule(NonTerminal.DESIGNATOR);
     return expr;
 }
 
 
// array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
 public ast.ArrayDeclaration array_declaration()
 {
	 ast.ArrayDeclaration result;
	 int lin = lineNumber();
	 int cha = charPosition();
 	expect(Token.Kind.ARRAY);
 	
 	result = new ast.ArrayDeclaration(lin,cha, tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER)));
 	
 	expect(Token.Kind.COLON);
 	type();
 	expect(Token.Kind.OPEN_BRACKET);
 	expect(Token.Kind.INTEGER);
 	expect(Token.Kind.CLOSE_BRACKET);
 	while(accept(Token.Kind.OPEN_BRACKET))
 	{
 		expect(Token.Kind.INTEGER);
 		expect(Token.Kind.CLOSE_BRACKET);
 	}
 	expect(Token.Kind.SEMICOLON);
 	
 	return result;
 }
 
// function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
 public ast.FunctionDefinition function_definition()
 {
	 int lineNum =  lineNumber();
	 	int charPos = charPosition();
 	expect(Token.Kind.FUNC);

 	Symbol temp = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));

 	expect(Token.Kind.OPEN_PAREN);
 	enterScope();
 	ArrayList<Symbol> tempList = parameter_list();
 	expect(Token.Kind.CLOSE_PAREN);
 	expect(Token.Kind.COLON);
 	type();
 	ast.StatementList body = statement_block();
 	
 	return new ast.FunctionDefinition(lineNum, charPos, temp, tempList, body);
}
 
// declaration := variable-declaration | array-declaration | function-definition .
 public ast.Declaration declaration()
 {
	 ast.Declaration result = null;
 	if(have(NonTerminal.VARIABLE_DECLARATION))
 	{
 		result = variable_declaration();
 	}
 	else if(have(NonTerminal.ARRAY_DECLARATION))
 	{
 		result = array_declaration();
 	}
 	else if(have(NonTerminal.FUNCTION_DEFINITION))
 	{
 		result = function_definition();
 	}
 	return result;
 }
 
// declaration-list := { declaration } .
 public ast.DeclarationList declaration_list()
 {
	 ast.DeclarationList result = new ast.DeclarationList(lineNumber(), charPosition());
 	while(have(NonTerminal.DECLARATION))
 	{
 		result.add(declaration());
 	}
 	return result;
 }
 
// assignment-statement := "let" designator "=" expression0 ";"
 public ast.Assignment assignment_statement()
 {	int lin = lineNumber();
 	int cha = charPosition();
 	expect(Token.Kind.LET);
 	ast.Expression destination = designator();
 	expect(Token.Kind.ASSIGN);
 	ast.Expression source = expression0();
 	expect(Token.Kind.SEMICOLON);
 	
 	return new ast.Assignment(lin, cha, destination, source);
  }
 
// call-statement := call-expression ";"
 public ast.Call call_statement()
 {
	 ast.Call result;
 	result = call_expression();
 	expect(Token.Kind.SEMICOLON);
 	return result;
 }
 
// if-statement := "if" expression0 statement-block [ "else" statement-block ] .
 public ast.IfElseBranch if_statement()
 {
	 int lin = lineNumber();
	 int cha = charPosition();
 	expect(Token.Kind.IF);
 	ast.Expression cond = expression0();
 	enterScope();
 	ast.StatementList thenBlock = statement_block();
 	ast.StatementList elseBlock = null;
 	if(accept(Token.Kind.ELSE))
 	{
 		elseBlock = statement_block();
 	}
 	return new ast.IfElseBranch(lin, cha, cond, thenBlock, elseBlock);
 }
 
// while-statement := "while" expression0 statement-block .
 public ast.WhileLoop while_statement()
 {
	 int lin = lineNumber();
	 int cha = charPosition();
 	expect(Token.Kind.WHILE);
 	ast.Expression cond = expression0();
 	enterScope();
 	ast.StatementList body = statement_block();
 	return new ast.WhileLoop(lin, cha, cond, body);
 }
 
// return-statement := "return" expression0 ";" .
 public ast.Return return_statement()
 {
	 ast.Return result;
 	expect(Token.Kind.RETURN);
 	result = new ast.Return(lineNumber(), charPosition(), expression0());
 	expect(Token.Kind.SEMICOLON);
 	return result;
 }
 
// statement := variable-declaration
//         | call-statement
//         | assignment-statement
//         | if-statement
//         | while-statement
//         | return-statement .
 public ast.Statement statement()
 {
	ast.Statement result = null;
 	if(have(NonTerminal.VARIABLE_DECLARATION))
 	{
 		result = variable_declaration();
 	}
 	else if(have(NonTerminal.CALL_STATEMENT))
 	{
 		result = call_statement();
 	}
 	else if(have(NonTerminal.ASSIGNMENT_STATEMENT))
 	{
 		result = assignment_statement();
 	}
 	else if(have(NonTerminal.IF_STATEMENT))
 	{
 		result = if_statement();
 	}
 	else if(have(NonTerminal.WHILE_STATEMENT))
 	{
 		result = while_statement();
 	}
 	else if(have(NonTerminal.RETURN_STATEMENT))
 	{
 		result = return_statement();
 	}
 	
	return result;
 }
 
// statement-list := { statement } .
 public ast.StatementList statement_list()
 {
	 ast.StatementList list = new ast.StatementList(lineNumber(), charPosition());
 	while(have(NonTerminal.STATEMENT))
 	{
 		list.add(statement());	
  	}
 	
 	return list;
 }
 
// statement-block := "{" statement-list "}" .
 public ast.StatementList statement_block()
 {
	 ast.StatementList result;
 	expect(Token.Kind.OPEN_BRACE); 
 	result = statement_list();
 	expect(Token.Kind.CLOSE_BRACE);
 	exitScope();
 	return result;
 }
 
 // program := declaration-list EOF .
 public ast.DeclarationList program()
 {
	 ast.DeclarationList result = declaration_list();
     expect(Token.Kind.EOF);
     return result;
 }

}
