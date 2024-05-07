package gr.hua.dit.compiler;

import java.io.*;
import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%line
%column
%cup

%eofval{
    return createSymbol(Symbols.EOF);
%eofval}

%{
    private StringBuffer sb = new StringBuffer();

    private Symbol createSymbol(int type) {
//        System.out.println("Matched string: \"" + yytext() + "\"" + " with type: " + Symbols.terminalNames[type]);
        return new Symbol(type, yyline+1, yycolumn+1);
    }

    private Symbol createSymbol(int type, Object value) {
//        System.out.println("Matched string: \"" + yytext() + "\"" + " with type: " + Symbols.terminalNames[type] + " and value: " + value);
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

delim       =       [ \t\n]
ws          =       {delim}+
variable    =       ([a-zA-Z_])[0-9A-Za-z_]*
digit       =       [0-9]
int         =       {digit}+
hex         =       [0-9a-fA-F]
string      =       \"[^\"]*\"
char        =       \'((\\[\\ntr0\'\"])|(\\x{hex}+)|([^\']))\'
single_line_comment   =       --[^\n]*
multi_line_comment  =       --\*([*]+[^\/]|[^*]|\n)*\*--
%%

// Keywords
"if"            { return createSymbol(Symbols.T_if);  }
"else"          { return createSymbol(Symbols.T_else); }
"while"         { return createSymbol(Symbols.T_while); }
"return"        { return createSymbol(Symbols.T_return); }

// Types
"byte"          { return createSymbol(Symbols.T_byte); }
"int"           { return createSymbol(Symbols.T_int); }
"false"         { return createSymbol(Symbols.T_false); }
"true"          { return createSymbol(Symbols.T_true); }
"reference"     { return createSymbol(Symbols.T_reference); }
"proc"           { return createSymbol(Symbols.T_proc); }

// Delimiters
"("             { return createSymbol(Symbols.T_lpar); }
")"             { return createSymbol(Symbols.T_rpar); }
"{"             { return createSymbol(Symbols.T_lbrace); }
"}"{delim}?     { return createSymbol(Symbols.T_rbrace); }
"["             { return createSymbol(Symbols.T_lbracket); }
"]"             { return createSymbol(Symbols.T_rbracket); }
","             { return createSymbol(Symbols.T_comma); }
":"             { return createSymbol(Symbols.T_colon); }
";"             { return createSymbol(Symbols.T_semicolon); }

// Operators
"=="            { return createSymbol(Symbols.T_eq_check); }
"!="            { return createSymbol(Symbols.T_neq); }
"<"             { return createSymbol(Symbols.T_lt); }
"<="            { return createSymbol(Symbols.T_le); }
">"             { return createSymbol(Symbols.T_gt); }
">="            { return createSymbol(Symbols.T_ge); }
"!"             { return createSymbol(Symbols.T_not); }
"="             { return createSymbol(Symbols.T_eq); }

// Logical operators
"&"             { return createSymbol(Symbols.T_and); }
"|"             { return createSymbol(Symbols.T_or); }

// Arithmetic operators
"+"             { return createSymbol(Symbols.T_plus); }
"-"             { return createSymbol(Symbols.T_minus); }
"/"             { return createSymbol(Symbols.T_div); }
"*"             { return createSymbol(Symbols.T_times); }
"%"             { return createSymbol(Symbols.T_mod); }

{variable}     { return createSymbol(Symbols.T_id, yytext()); }

{int}           { return createSymbol(Symbols.T_num, Integer.valueOf(yytext())); }
{string}        { return createSymbol(Symbols.T_string, yytext()); }
{char}          { return createSymbol(Symbols.T_char, yytext()); }

{ws}            { }
// { System.out.println("Matched whitespace of length: " + yytext().length()); }

.              { System.err.println("Illegal character: " + yytext()); }

{single_line_comment}    {}
// { System.out.println("Matched single line comment: " + yytext()); }
//{multi_line_comment}    {}

