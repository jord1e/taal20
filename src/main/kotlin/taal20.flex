package nl.jrdie.taal20.lexer;

import java_cup.runtime.*;
import nl.jrdie.taal20.lang.Taal20Symbol;
import nl.jrdie.taal20._parser.Taal20SymbolType;

%%

// C:\Users\jordi\ProgrammingProjects\taal20\src\main\kotlin\taal20.flex
// C:\Users\jordi\ProgrammingProjects\taal20\src\gen\java\nl\jrdie\taal20\lexer

%public
%class Taal20Lexer

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

//LineTerminator = \r|\n|\r\n

NewLine = \n
Space = " "
Tab = \t
Digit = [0-9]+
VarName = [a-z]

%%

<YYINITIAL> {
  "zolang"             { return symbol(Taal20SymbolType.ZOLANG); }
  "als"                { return symbol(Taal20SymbolType.ALS); }
  "anders"             { return symbol(Taal20SymbolType.ANDERS); }
  "gebruik"            { return symbol(Taal20SymbolType.GEBRUIK); }
//  "variable          " { return symbol(Taal20SymbolType.); }
  "kleurOog"           { return symbol(Taal20SymbolType.KLEUROOG); }
  "zwOog"              { return symbol(Taal20SymbolType.ZWOOG); }
  "kompas"             { return symbol(Taal20SymbolType.KOMPAS); }
  "{"                  { return symbol(Taal20SymbolType.LBRACE); }
  "}"                  { return symbol(Taal20SymbolType.RBRACE); }
  "=="                 { return symbol(Taal20SymbolType.EQEQ); }
  "!="                 { return symbol(Taal20SymbolType.NOTEQ); }
  "="                  { return symbol(Taal20SymbolType.EQ); }
  ">"                  { return symbol(Taal20SymbolType.GT); }
  "<"                  { return symbol(Taal20SymbolType.LT); }
  "+"                  { return symbol(Taal20SymbolType.PLUS); }
  "-"                  { return symbol(Taal20SymbolType.MIN); }
  "*"                  { return symbol(Taal20SymbolType.TIMES); }
  "/"                  { return symbol(Taal20SymbolType.SLASH); }
  "%"                  { return symbol(Taal20SymbolType.PERCENT); }
  "stapVooruit"        { return symbol(Taal20SymbolType.STAP_VOORUIT); }
  "stapAchteruit"      { return symbol(Taal20SymbolType.STAP_ACHTERUIT); }
  "draaiLinks"         { return symbol(Taal20SymbolType.DRAAI_LINKS); }
  "draaiRechts"        { return symbol(Taal20SymbolType.DRAAI_RECHTS); }
  {Tab}                { return symbol(Taal20SymbolType.TAB); }
  {Space}              { return symbol(Taal20SymbolType.SPACE); }
  {NewLine}            { return symbol(Taal20SymbolType.NEWLINE); }
  {Digit}              { return symbol(Taal20SymbolType.INT, Integer.parseInt(yytext())); }
  {VarName}            { return symbol(Taal20SymbolType.VARNAME, yytext()); }
}

/* error fallback */
[^]                    { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
//<<EOF>>                          { return symbol(Taal20SymbolType.EOF); }
