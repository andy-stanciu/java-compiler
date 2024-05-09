/**
 * JFlex specification for lexical analysis of a simple demo language.
 * Change this into the scanner for your implementation of MiniJava.
 *
 * CSE 401/M501/P501 19au, 20sp, 20au
 */


package scanner;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.sym;

%%
%public
%final
%class scanner
%unicode
%cup
%line
%column

/* The following code block is copied literally into the generated scanner
 * class. You can use this to define methods and/or declare fields of the
 * scanner class, which the lexical actions may also reference. Most likely,
 * you will only need to tweak what's already provided below.
 *
 * We use CUP's ComplexSymbolFactory and its associated ComplexSymbol class
 * that tracks the source location of each scanned symbol.
 */
%{
  /** The CUP symbol factory, typically shared with parser. */
  private ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();

  /** Initialize scanner with input stream and a shared symbol factory. */
  public scanner(java.io.Reader in, ComplexSymbolFactory sf) {
    this(in);
    this.symbolFactory = sf;
  }

  /**
   * Construct a symbol with a given lexical token, a given
   * user-controlled datum, and the matched source location.
   *
   * @param code     identifier of the lexical token (i.e., sym.<TOKEN>)
   * @param value    user-controlled datum to associate with this symbol
   * @effects        constructs new ComplexSymbol via this.symbolFactory
   * @return         a fresh symbol storing the above-described information
   */
  private Symbol symbol(int code, Object value) {
    // Calculate symbol location
    int yylen = yylength();
    Location left = new Location(yyline + 1, yycolumn + 1);
    Location right = new Location(yyline + 1, yycolumn + yylen);
    // Calculate symbol name
    int max_code = sym.terminalNames.length;
    String name = code < max_code ? sym.terminalNames[code] : "<UNKNOWN(" + yytext() + ")>";
    return this.symbolFactory.newSymbol(name, code, left, right, value);
  }

  /**
   * Construct a symbol with a given lexical token and matched source
   * location, leaving the user-controlled value field uninitialized.
   *
   * @param code     identifier of the lexical token (i.e., sym.<TOKEN>)
   * @effects        constructs new ComplexSymbol via this.symbolFactory
   * @return         a fresh symbol storing the above-described information
   */
  private Symbol symbol(int code) {
    // Calculate symbol location
    int yylen = yylength();
    Location left = new Location(yyline + 1, yycolumn + 1);
    Location right = new Location(yyline + 1, yycolumn + yylen);
    // Calculate symbol name
    int max_code = sym.terminalNames.length;
    String name = code < max_code ? sym.terminalNames[code] : "<UNKNOWN(" + yytext() + ")>";
    return this.symbolFactory.newSymbol(name, code, left, right);
  }

  /**
   * Convert the symbol generated by this scanner into a string.
   *
   * This method is useful to include information in the string representation
   * in addition to the plain CUP name for a lexical token.
   *
   * @param symbol   symbol instance generated by this scanner
   * @return         string representation of the symbol
   */
   public String symbolToString(Symbol s) {
     // All symbols generated by this class are ComplexSymbol instances
     ComplexSymbol cs = (ComplexSymbol)s; 
     if (cs.sym == sym.IDENTIFIER) {
       return "ID(" + (String)cs.value + ")";
     } else if (cs.sym == sym.INT_LITERAL) {
         return "INT(" + (String)cs.value + ")";
     } else if (cs.sym == sym.error) {
       return "<UNEXPECTED(" + (String)cs.value + ")>";
     } else {
       return cs.getName();
     }
   }
%}

/* Helper definitions */
letter = [a-zA-Z]
digit = [0-9]
eol = [\r\n]
white = {eol}|[ \t]

%%

/* Token definitions */

/* reserved words (first so that they take precedence over identifiers) */
"boolean" { return symbol(sym.BOOLEAN); }
"class" { return symbol(sym.CLASS); }
"else" { return symbol(sym.ELSE); }
"extends" { return symbol(sym.EXTENDS); }
"false" { return symbol(sym.FALSE); }
"if" { return symbol(sym.IF); }
"int" { return symbol(sym.INT); }
"length" { return symbol(sym.LENGTH); }
"main" { return symbol(sym.MAIN); }
"new" { return symbol(sym.NEW); }
"public" { return symbol(sym.PUBLIC); }
"return" { return symbol(sym.RETURN); }
"static" { return symbol(sym.STATIC); }
"String" { return symbol(sym.STRING); }
"System.out.println" { return symbol(sym.PRINT); }
"this" { return symbol(sym.THIS); }
"true" { return symbol(sym.TRUE); }
"void" { return symbol(sym.VOID); }
"while" { return symbol(sym.WHILE); }

/* operators */
"+" { return symbol(sym.PLUS); }
"-" { return symbol(sym.MINUS); }
"*" { return symbol(sym.MULT); }
"/" { return symbol(sym.DIV); }
"%" { return symbol(sym.MOD); }
"<" { return symbol(sym.LESS_THAN); }
"<=" { return symbol(sym.LESS_THAN_OR_EQUAL); }
">" { return symbol(sym.GREATER_THAN); }
">=" { return symbol(sym.GREATER_THAN_OR_EQUAL); }
"=" { return symbol(sym.EQUALS); }
"!" { return symbol(sym.NOT); }
"&&" { return symbol(sym.AND); }
"||" { return symbol(sym.OR); }
"==" { return symbol(sym.IS_EQUAL); }
"!=" { return symbol(sym.IS_NOT_EQUAL); }
"&" { return symbol(sym.BITWISE_AND); }
"|" { return symbol(sym.BITWISE_OR); }
"^" { return symbol(sym.BITWISE_XOR); }
"~" { return symbol(sym.BITWISE_NOT); }

/* delimiters */
"(" { return symbol(sym.LPAREN); }
")" { return symbol(sym.RPAREN); }
"[" { return symbol(sym.LSQUARE); }
"]" { return symbol(sym.RSQUARE); }
"{" { return symbol(sym.LCURL); }
"}" { return symbol(sym.RCURL); }
";" { return symbol(sym.SEMICOLON); }
"," { return symbol(sym.COMMA); }
"." { return symbol(sym.DOT); }
"?" { return symbol(sym.QUESTION); }
":" { return symbol(sym.COLON); }

/* identifiers */
{letter}({letter}|{digit}|_)* {
  return symbol(sym.IDENTIFIER, yytext());
}

/* integer literals */
0|([1-9]{digit}*) {
  return symbol(sym.INT_LITERAL, yytext());
}

/* comments */
((\/\*)([^*/]|(\**[^*/])|\/)*(\**)(\*\/))|((\/\/)[^{eol}]*) { /* ignore comments */ }

/* whitespace */
{white}+ { /* ignore whitespace */ }

/* lexical errors (last so other matches take precedence) */
. {
    System.err.printf(
      "Unexpected character '%s' on line %d at column %d of input.%n",
      yytext(), yyline + 1, yycolumn + 1
    );
    return symbol(sym.error, yytext());
  }

<<EOF>> { return symbol(sym.EOF); }
