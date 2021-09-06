package nl.jrdie.taal20.lexer;

import java_cup.runtime.*;
import nl.jrdie.taal20.lang.Taal20Symbol;
import nl.jrdie.taal20._parser.Taal20SymbolType;

%%

// C:\Users\jordi\ProgrammingProjects\taal20\src\main\kotlin\taal20.flex
// C:\Users\jordi\ProgrammingProjects\taal20\src\gen\java\nl\jrdie\taal20\lexer

%public
%class Taal20Lexer
//%implements sym

%unicode

%line
%column

%cupsym Taal20SymbolType
%cup
%cupdebug

%eofval{
  return symbol(Taal20SymbolType.EOF);
%eofval}
%eofclose

%{
  StringBuilder string = new StringBuilder();

    private Symbol symbol(int type) {
      return new Taal20Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value) {
      return new Taal20Symbol(type, yyline+1, yycolumn+1, value);
    }

    /**
     * assumes correct representation of a long value for
     * specified radix in scanner buffer from <code>start</code>
     * to <code>end</code>
     */
    private long parseLong(int start, int end, int radix) {
      long result = 0;
      long digit;

      for (int i = start; i < end; i++) {
        digit  = Character.digit(yycharat(i),radix);
        result*= radix;
        result+= digit;
      }

      return result;
    }
%}

/* nl.jrdie.taal20.main character classes */
//LineTerminator = \r|\n|\r\n
//InputCharacter = [^\r\n]
//
//WhiteSpace = {LineTerminator} | [ \t\f]

NewLine = \n
Space = " "
Tab = \t
Digit = [0-9]+
VarName = [a-z]

%state STRING, CHARLITERAL

%%

<YYINITIAL> {

  /* keywords */
  "zolang"                       { return symbol(Taal20SymbolType.ZOLANG); }
  "als"                          { return symbol(Taal20SymbolType.ALS); }
  "anders"                       { return symbol(Taal20SymbolType.ANDERS); }
  "gebruik"                      { return symbol(Taal20SymbolType.GEBRUIK); }
//  "variable"                     { return symbol(Taal20SymbolType.); }
  "kleurOog"                     { return symbol(Taal20SymbolType.KLEUROOG); }
  "zwOog"                        { return symbol(Taal20SymbolType.ZWOOG); }
  "kompas"                       { return symbol(Taal20SymbolType.KOMPAS); }
  "{"                            { return symbol(Taal20SymbolType.LBRACE); }
  "}"                            { return symbol(Taal20SymbolType.RBRACE); }
  "=="                           { return symbol(Taal20SymbolType.EQEQ); }
  "!="                           { return symbol(Taal20SymbolType.NOTEQ); }
  "="                            { return symbol(Taal20SymbolType.EQ); }
  ">"                            { return symbol(Taal20SymbolType.GT); }
  "<"                            { return symbol(Taal20SymbolType.LT); }
  "+"                            { return symbol(Taal20SymbolType.PLUS); }
  "-"                            { return symbol(Taal20SymbolType.MIN); }
  "*"                            { return symbol(Taal20SymbolType.TIMES); }
  "/"                            { return symbol(Taal20SymbolType.SLASH); }
  "%"                            { return symbol(Taal20SymbolType.PERCENT); }
  "stapVooruit"                  { return symbol(Taal20SymbolType.STAP_VOORUIT); }
  "stapAchteruit"                { return symbol(Taal20SymbolType.STAP_ACHTERUIT); }
  "draaiLinks"                   { return symbol(Taal20SymbolType.DRAAI_LINKS); }
  "draaiRechts"                  { return symbol(Taal20SymbolType.DRAAI_RECHTS); }
  {Tab}                          { return symbol(Taal20SymbolType.TAB); }
  {Space}                        { return symbol(Taal20SymbolType.SPACE); }
  {NewLine}                      { return symbol(Taal20SymbolType.NEWLINE); }
  {Digit}                        { return symbol(Taal20SymbolType.INT); }
  {VarName}                      { return symbol(Taal20SymbolType.VARNAME); }

//
//  /* boolean literals */
//  "true"                         { return symbol(BOOLEAN_LITERAL, true); }
//  "false"                        { return symbol(BOOLEAN_LITERAL, false); }
//  /* string literal */
//  \"                             { yybegin(STRING); string.setLength(0); }
//
//  /* character literal */
//  \'                             { yybegin(CHARLITERAL); }
//
//  /* numeric literals */
//
//  /* This is matched together with the minus, because the number is too big to
//     be represented by a positive integer. */
//  "-2147483648"                  { return symbol(INTEGER_LITERAL, Integer.valueOf(Integer.MIN_VALUE)); }
//
//  {DecIntegerLiteral}            { return symbol(INTEGER_LITERAL, Integer.valueOf(yytext())); }
//  {DecLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(yytext().substring(0,yylength()-1))); }
//
//  {HexIntegerLiteral}            { return symbol(INTEGER_LITERAL, Integer.valueOf((int) parseLong(2, yylength(), 16))); }
//  {HexLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(parseLong(2, yylength()-1, 16))); }
//
//  {OctIntegerLiteral}            { return symbol(INTEGER_LITERAL, Integer.valueOf((int) parseLong(0, yylength(), 8))); }
//  {OctLongLiteral}               { return symbol(INTEGER_LITERAL, new Long(parseLong(0, yylength()-1, 8))); }
//
//  {FloatLiteral}                 { return symbol(FLOATING_POINT_LITERAL, new Float(yytext().substring(0,yylength()-1))); }
//  {DoubleLiteral}                { return symbol(FLOATING_POINT_LITERAL, new Double(yytext())); }
//  {DoubleLiteral}[dD]            { return symbol(FLOATING_POINT_LITERAL, new Double(yytext().substring(0,yylength()-1))); }
//
//  /* comments */
//  {Comment}                      { /* ignore */ }
//
//  /* whitespace */
//  {WhiteSpace}                   { /* ignore */ }
//
//  /* identifiers */
//  {Identifier}                   { return symbol(IDENTIFIER, yytext()); }
}

//<STRING> {
//  \"                             { yybegin(YYINITIAL); return symbol(STRING_LITERAL, string.toString()); }
//
//  {StringCharacter}+             { string.append( yytext() ); }
//
//  /* escape sequences */
//  "\\b"                          { string.append( '\b' ); }
//  "\\t"                          { string.append( '\t' ); }
//  "\\n"                          { string.append( '\n' ); }
//  "\\f"                          { string.append( '\f' ); }
//  "\\r"                          { string.append( '\r' ); }
//  "\\\""                         { string.append( '\"' ); }
//  "\\'"                          { string.append( '\'' ); }
//  "\\\\"                         { string.append( '\\' ); }
//  \\[0-3]?{OctDigit}?{OctDigit}  { char val = (char) Integer.parseInt(yytext().substring(1),8);
//                        				   string.append( val ); }
//
//  /* error cases */
//  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
//  {LineTerminator}               { throw new RuntimeException("Unterminated string at end of line"); }
//}
//
//<CHARLITERAL> {
//  {SingleCharacter}\'            { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, yytext().charAt(0)); }
//
//  /* escape sequences */
//  "\\b"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\b');}
//  "\\t"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\t');}
//  "\\n"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\n');}
//  "\\f"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\f');}
//  "\\r"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\r');}
//  "\\\""\'                       { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\"');}
//  "\\'"\'                        { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\'');}
//  "\\\\"\'                       { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, '\\'); }
//  \\[0-3]?{OctDigit}?{OctDigit}\' { yybegin(YYINITIAL);
//			                              int val = Integer.parseInt(yytext().substring(1,yylength()-1),8);
//			                            return symbol(CHARACTER_LITERAL, (char)val); }
//
//  /* error cases */
//  \\.                            { throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
//  {LineTerminator}               { throw new RuntimeException("Unterminated character literal at end of line"); }
//}

/* error fallback */
[^]                              { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
//<<EOF>>                          { return symbol(Taal20SymbolType.EOF); }
