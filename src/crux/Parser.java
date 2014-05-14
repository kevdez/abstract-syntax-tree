package crux;

import java.util.ArrayList;
import java.util.List;
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
    
    private boolean expect(nonterminal nt)
    {
        if (accept(nt))
            return true;
        string errormessage = reportsyntaxerror(nt);
        throw new QuitParseException(errorMessage);
        //return false;
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
        //return ErrorToken(errorMessage);
    }

    
 // Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
         
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind + ".]";
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
    
    
 // Grammar Rule Reporting ==========================================
    private StringBuffer parseTreeBuffer = new StringBuffer();


    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }
    
    
 
 // Grammar Rules =====================================================
    
 // literal := INTEGER | FLOAT | TRUE | FALSE .
    public ast.Expression literal()
    {
        ast.Expression expr;
        enterRule(NonTerminal.LITERAL);
        
        Token tok = expectRetrieve(NonTerminal.LITERAL);
        Expression expr = Command.newLiteral(tok);
        
        exitRule(NonTerminal.LITERAL);
        return expr;
    }
    
//  literal := INTEGER | FLOAT | TRUE | FALSE .
// public void literal()
// { 	
// 	if(have(Token.Kind.INTEGER))
// 		expect(Token.Kind.INTEGER);
// 	else if(have(Token.Kind.FLOAT))
// 		expect(Token.Kind.FLOAT);
// 	else if(have(Token.Kind.TRUE))
// 		expect(Token.Kind.TRUE);
// 	else if(have(Token.Kind.FALSE))
// 		expect(Token.Kind.FALSE);
// }
 
//  designator := IDENTIFIER { "[" expression0 "]" } .
 public ast.Expression designator()
 {
     tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
     while (accept(Token.Kind.OPEN_BRACKET)) {
         expression0();
         expect(Token.Kind.CLOSE_BRACKET);
     }
 }
 
// type := IDENTIFIER .
 public void type()
 { 	
 	expect(Token.Kind.IDENTIFIER);
 }
 
// op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
 public void op0()
 { 	
 	if(have(Token.Kind.GREATER_EQUAL))
 		expect(Token.Kind.GREATER_EQUAL);
 	else if(have(Token.Kind.LESSER_EQUAL))
 		expect(Token.Kind.LESSER_EQUAL);
 	else if(have(Token.Kind.NOT_EQUAL))
 		expect(Token.Kind.NOT_EQUAL);
 	else if(have(Token.Kind.EQUAL))
 		expect(Token.Kind.EQUAL);
 	else if(have(Token.Kind.GREATER_THAN))
 		expect(Token.Kind.GREATER_THAN);
 	else if(have(Token.Kind.LESS_THAN))
 		expect(Token.Kind.LESS_THAN);
 }
 
// op1 := "+" | "-" | "or" .
 public void op1()
 { 	
 	if(have(Token.Kind.ADD))
 		expect(Token.Kind.ADD);
 	else if(have(Token.Kind.SUB))
 		expect(Token.Kind.SUB);
 	else if(have(Token.Kind.OR))
 		expect(Token.Kind.OR);
 }
 
// op2 := "*" | "/" | "and" .
 public void op2()
 {
 	if(have(Token.Kind.MUL))
 		expect(Token.Kind.MUL);
 	else if(have(Token.Kind.DIV))
 		expect(Token.Kind.DIV);
 	else if(have(Token.Kind.AND))
 		expect(Token.Kind.AND);
	
 }

// expression0 := expression1 [ op0 expression1 ] .
 public void expression0()
 {	
 	expression1();
 	while (have(NonTerminal.OP0))
 	{
 		op0();
 		if(have(NonTerminal.EXPRESSION1))
 			expression1();
 	}
 	
}
 
// expression1 := expression2 { op1  expression2 } .
 public void expression1()
 {	
  	expression2();
 	while (have(NonTerminal.OP1))
 	{
 		op1();
 		if(have(NonTerminal.EXPRESSION2))
 			expression2();
 	}
 }
 
// expression2 := expression3 { op2 expression3 } .
 public void expression2()
 {	
 	expression3();
 	while (have(NonTerminal.OP2))
 	{
 		op2();
 		if(have(NonTerminal.EXPRESSION3))
 			expression3();
 	
 	}
 }
 
// expression3 := "not" expression3
// 	       | "(" expression0 ")"
// 	       | designator
// 	       | call-expression
// 	       | literal .
 public void expression3()
 { 	
 	if(have(Token.Kind.NOT))
 	{
 		accept(Token.Kind.NOT);
 		expression3();
 	}
 	else if(have(Token.Kind.OPEN_PAREN))
 	{
 		accept(Token.Kind.OPEN_PAREN);
 		expression0();
 		expect(Token.Kind.CLOSE_PAREN);
 	}
 	else if (have(NonTerminal.DESIGNATOR))
 	{
 		designator();
 	}
 	else if (have(NonTerminal.CALL_EXPRESSION))
 	{
 		call_expression();
 	}
 	else if (have(NonTerminal.LITERAL))
 	{
 		literal();
 	}
 }
 
// call-expression := "::" IDENTIFIER "(" expression-list ")" .
 public void call_expression()
 {
 	expect(Token.Kind.CALL);
 	tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
 	expect(Token.Kind.OPEN_PAREN);
 	expression_list();
 	expect(Token.Kind.CLOSE_PAREN);
 }
 
// expression-list := [ expression0 { "," expression0 } ] .
 public void expression_list()
 {
 	if(have(NonTerminal.EXPRESSION0))
 	{
 		expression0();
 		while(accept(Token.Kind.COMMA))
 		{
 			expression0();
 		}
 	}
 }
 
// parameter := IDENTIFIER ":" type .
 public void parameter()
 {
	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
 	expect(Token.Kind.COLON);
 	type();
 }
 
// parameter-list := [ parameter { "," parameter } ] .
 public void parameter_list()
 {	
 	if(have(NonTerminal.PARAMETER))
 	{
 		parameter();
 		while(accept(Token.Kind.COMMA))
 		{
 			parameter();
 		}
 	}
 }
 
// variable-declaration := "var" IDENTIFIER ":" type ";"
 public ast.VariableDeclaration variable_declaration()
 {
 	expect(Token.Kind.VAR);
 	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
 	expect(Token.Kind.COLON);
 	type();
 	expect(Token.Kind.SEMICOLON);
}
 
// array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
 public void array_declaration()
 {
 	expect(Token.Kind.ARRAY);
 	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
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
 }
 
// function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
 public ast.FunctionDefinition function_definition()
 {
	
 	expect(Token.Kind.FUNC);
 	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
 	expect(Token.Kind.OPEN_PAREN);
 	enterScope();///////////////////////////// NEW SCOPE BEGINS HERE
 	parameter_list();
 	expect(Token.Kind.CLOSE_PAREN);
 	expect(Token.Kind.COLON);
 	type();
 	statement_block();
}
 
// declaration := variable-declaration | array-declaration | function-definition .
 public void declaration()
 {
 	if(have(NonTerminal.VARIABLE_DECLARATION))
 	{
 		variable_declaration();
 	}
 	else if(have(NonTerminal.ARRAY_DECLARATION))
 	{
 		array_declaration();
 	}
 	else if(have(NonTerminal.FUNCTION_DEFINITION))
 	{
 		function_definition();
 	}
 }
 
// declaration-list := { declaration } .
 public void declaration_list()
 {
 	while(have(NonTerminal.DECLARATION))
 	{
 		declaration();
 	}
 }
 
// assignment-statement := "let" designator "=" expression0 ";"
 public ast.Assignment assignment_statement()
 {	
 	expect(Token.Kind.LET);
 	designator();
 	expect(Token.Kind.ASSIGN);
 	expression0();
 	expect(Token.Kind.SEMICOLON);
  }
 
// call-statement := call-expression ";"
 public void call_statement()
 {
 	call_expression();
 	expect(Token.Kind.SEMICOLON);
 }
 
// if-statement := "if" expression0 statement-block [ "else" statement-block ] .
 public void if_statement()
 {
 	expect(Token.Kind.IF);
 	expression0();
 	enterScope();
 	statement_block();
 	if(accept(Token.Kind.ELSE))
 	{
 		statement_block();
 	}
 }
 
// while-statement := "while" expression0 statement-block .
 public void while_statement()
 {
 	expect(Token.Kind.WHILE);
 	expression0();
 	enterScope();
 	statement_block();
 }
 
// return-statement := "return" expression0 ";" .
 public void return_statement()
 {
 	expect(Token.Kind.RETURN);
 	expression0();
 	expect(Token.Kind.SEMICOLON);
 }
 
// statement := variable-declaration
//         | call-statement
//         | assignment-statement
//         | if-statement
//         | while-statement
//         | return-statement .
 public void statement()
 {
 	if(have(NonTerminal.VARIABLE_DECLARATION))
 	{
 		variable_declaration();
 	}
 	else if(have(NonTerminal.CALL_STATEMENT))
 	{
 		call_statement();
 	}
 	else if(have(NonTerminal.ASSIGNMENT_STATEMENT))
 	{
 		assignment_statement();
 	}
 	else if(have(NonTerminal.IF_STATEMENT))
 	{
 		if_statement();
 	}
 	else if(have(NonTerminal.WHILE_STATEMENT))
 	{
 		while_statement();
 	}
 	else if(have(NonTerminal.RETURN_STATEMENT))
 	{
 		return_statement();
 	}
 }
 
// statement-list := { statement } .
 public void statement_list()
 {
 	while(have(NonTerminal.STATEMENT))
 	{
 		statement();	
  	}
 }
 
// statement-block := "{" statement-list "}" .
 public void statement_block()
 {
 	expect(Token.Kind.OPEN_BRACE); 
 	statement_list();
 	expect(Token.Kind.CLOSE_BRACE);
 	exitScope();
 }
 
 // program := declaration-list EOF .
 public ast.DeclarationList program()
 {
// 	declaration_list();
//     expect(Token.Kind.EOF);
	 throw new RuntimeException("add code to each grammar rule, to build as ast.");
 }

}
