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
  Map<String, Token.Kind> map  ;
  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
    this.map = InitHashMap();
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
	 return this.fstream.read();
 }

  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
	//System.out.println("here 26");
    int c = this.fstream.read();
    if (-1 == c)
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.
      return new Token(Kind.TOKEN_EOF, null);

    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || '\n' == c || '\r' == c) {
      c = this.fstream.read();
    }
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, null);
    //System.out.println("here 39");
    switch (c) {
    case '+':	
      return new Token(Kind.TOKEN_ADD, null);
    case '=':
    	return new Token(Kind.TOKEN_ASSIGN,null);
    case ',':
    	return new Token(Kind.TOKEN_COMMER,null);
    case '.':
    	return new Token(Kind.TOKEN_DOT,null);
    case '{':
    	return new Token(Kind.TOKEN_LBRACE,null);
    case '[':
    	return new Token(Kind.TOKEN_LBRACK,null);
    case '(':
    	return new Token(Kind.TOKEN_LPAREN,null);
    case '<':
    	return new Token(Kind.TOKEN_LT,null);
    case '!':
    	return new Token(Kind.TOKEN_NOT,null);
    case '}':
    	return new Token(Kind.TOKEN_RBRACE,null);
    case ']':
    	return new Token(Kind.TOKEN_RBRACK,null);
    case ')':
    	return new Token(Kind.TOKEN_RPAREN,null);
    case ';':
    	return new Token(Kind.TOKEN_SEMI,null);
    case '-':
    	return new Token(Kind.TOKEN_SUB,null);
    case '*':
    	return new Token(Kind.TOKEN_TIMES,null);
    case '&':
    	c = getNextChar();
    	if(c != '&') {
    		System.out.println("error: & is invalid character ");
    		return null;
    	}
    	return new Token(Kind.TOKEN_AND,null);
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
    	  c = getNextChar();
    	  this.fstream.mark(1);
    	  while (Character.isDigit(c)) {
    		  this.fstream.mark(1);
    		  Num = Num.concat(String.valueOf(c));
    		  c = getNextChar();
    	  }
    	  this.fstream.reset();
    	  return new Token(Kind.TOKEN_NUM,null,Num);
      }
      else if (Character.isLetter(c))
      {
    	  String IdName = new String();
    	  IdName = IdName.concat(String.valueOf((char)c));
    	  c = getNextChar();
    	  this.fstream.mark(1);
    	  while(Character.isDigit(c) || Character.isLetter(c) || c == '_') {
    		  this.fstream.mark(1);
    		  IdName = IdName.concat(String.valueOf((char)c));
    		  c = getNextChar();
    	  }
    	  this.fstream.reset();
    	  Kind tokenKind = map.get(IdName);
    	  if(tokenKind == null) {
    		  return new Token(Kind.TOKEN_ID,null,IdName);
    	  }
    	  else {
    		  return new Token(tokenKind,null);
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
