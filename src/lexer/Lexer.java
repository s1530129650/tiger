package lexer;

import static control.Control.ConLexer.dump;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import lexer.Token.Kind;
import util.Todo;


public class Lexer
{
  String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file
  public Integer line ;
  public Integer column ;
  
  Map<String, Token.Kind> map  ;
  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
    this.map = InitHashMap();

	this.line = 1;
	this.column = 1;
  }
  
  private Map<String, Token.Kind> InitHashMap()
  {
	  Map<String, Token.Kind> x = new HashMap<String, Token.Kind>();
	 
	  x.put("boolean", 	Kind.TOKEN_BOOLEAN);
	  x.put("class", 	Kind.TOKEN_CLASS) ;
	  x.put("else", 	Kind.TOKEN_ELSE) ;
	  x.put("extends", 	Kind.TOKEN_EXTENDS) ;
	  x.put("false",	Kind.TOKEN_FALSE) ;
	  x.put("if", 		Kind.TOKEN_IF) ;
	  x.put("int", 		Kind.TOKEN_INT) ;
	  x.put("length", 	Kind.TOKEN_LENGTH) ;
	  x.put("main", 	Kind.TOKEN_MAIN) ;
	  x.put("new", 		Kind.TOKEN_NEW) ;
	  x.put("out", 		Kind.TOKEN_OUT) ;
	  x.put("println", 	Kind.TOKEN_PRINTLN) ;
	  x.put("public", 	Kind.TOKEN_PUBLIC) ;
	  x.put("return", 	Kind.TOKEN_RETURN) ;
	  x.put("static", 	Kind.TOKEN_STATIC) ;
	  x.put("String", 	Kind.TOKEN_STRING) ;
	  x.put("System", 	Kind.TOKEN_SYSTEM) ;
	  x.put("this", 	Kind.TOKEN_THIS) ;
	  x.put("true", 	Kind.TOKEN_TRUE) ;
	  x.put("void", 	Kind.TOKEN_VOID) ;
	  x.put("while", 	Kind.TOKEN_WHILE) ;
	  
	  return x ;
  }
 
 public int getNextChar() throws Exception
 {
	 this.column ++;
	 return this.fstream.read();
	 
 }
 public void resetTomark() throws Exception
 {
	 this.fstream.reset();
	 this.column--;
 }

  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
	//System.out.println("here 26");
    //int c = this.fstream.read();
	int c = getNextChar();
    if (-1 == c)
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.
      return new Token(Kind.TOKEN_EOF, this.line,this.column);

    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || '\n' == c || '\r' == c) {
    	
    	if(c == '\t') {
    		this.column = this.column+3;
    		
    	}
    	if(c == '\n') {
    		this.line ++;
    		this.column = 1;
    		
    	}
    	c = getNextChar();
      
    }
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, this.line,this.column);
    //System.out.println("here 39");
    switch (c) {
    case '+':	
      return new Token(Kind.TOKEN_ADD, this.line,this.column);
    case '=':
    	return new Token(Kind.TOKEN_ASSIGN,this.line,this.column);
    case ',':
    	return new Token(Kind.TOKEN_COMMER,this.line,this.column);
    case '.':
    	return new Token(Kind.TOKEN_DOT,this.line,this.column);
    case '{':
    	return new Token(Kind.TOKEN_LBRACE,this.line,this.column);
    case '[':
    	return new Token(Kind.TOKEN_LBRACK,this.line,this.column);
    case '(':
    	return new Token(Kind.TOKEN_LPAREN,this.line,this.column);
    case '<':
    	return new Token(Kind.TOKEN_LT,this.line,this.column);
    case '!':
    	return new Token(Kind.TOKEN_NOT,this.line,this.column);
    case '}':
    	return new Token(Kind.TOKEN_RBRACE,this.line,this.column);
    case ']':
    	return new Token(Kind.TOKEN_RBRACK,this.line,this.column);
    case ')':
    	return new Token(Kind.TOKEN_RPAREN,this.line,this.column);
    case ';':
    	return new Token(Kind.TOKEN_SEMI,this.line,this.column);
    case '-':
    	return new Token(Kind.TOKEN_SUB,this.line,this.column);
    case '*':
    	return new Token(Kind.TOKEN_TIMES,this.line,this.column);
    case '&':
    	c = getNextChar();
    	if(c != '&') {
    		System.out.println("error: & is invalid character ");
    		return null;
    	}
    	return new Token(Kind.TOKEN_AND,this.line,this.column);
    default:
      // Lab 1, exercise 2: supply missing code to
      // lex other kinds of tokens.
      // Hint: think carefully about the basic
      // data structure and algorithms. The code
      // is not that much and may be less than 50 lines. If you
      // find you are writing a lot of code, you
      // are on the wrong way.
    	//check if it is number
      if (Character.isDigit(c)) {
    	  String Num = new String();
    	  Num = Num.concat(String.valueOf(c));
    	  this.fstream.mark(1);
    	  c = getNextChar();
    	  while (Character.isDigit(c)) {
    		  this.fstream.mark(1);
    		  Num = Num.concat(String.valueOf(c));
    		  c = getNextChar();
    	  }
    	  //resetTomark();
    	  this.fstream.reset();
    	  this.column--;
    	  
    	  return new Token(Kind.TOKEN_NUM,this.line,this.column,Num);
      }
      else if (Character.isLetter(c))
      {
    	  String IdName = new String();
    	  IdName = IdName.concat(String.valueOf((char)c));
    	  this.fstream.mark(1);
    	  c = getNextChar();
    	  while(Character.isDigit(c) || Character.isLetter(c) || c == '_') {
    		  this.fstream.mark(1);
    		  IdName = IdName.concat(String.valueOf((char)c));
    		  c = getNextChar();
    	  }
    	  //resetTomark();
    	  this.fstream.reset();
    	  this.column--;
    	  Kind tokenKind = map.get(IdName);
    	  if(tokenKind == null) {
    		  return new Token(Kind.TOKEN_ID,this.line,this.column,IdName);
    	  }
    	  else {
    		  return new Token(tokenKind,this.line,this.column);
    	  }
 
      }
      else {
    	  System.out.print("Invalid character:  ");
    	  System.out.println((char)c);
    	  return null;
      }
      
     
    }
  }

  public Token nextToken()
  {
    Token t = null;

    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (dump)
      System.out.println(t.toString());
    return t;
  }
}
