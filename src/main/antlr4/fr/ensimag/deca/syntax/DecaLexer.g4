lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
   // CommonTokenStream tokens = new CommonTokenStream(superClass);
   // AbstractDecaParser parser = new AbstractDecaParser(tokens);
}

ESPACE : ' ' {skip();};
EOL : ('\t'| '\r'| '\n') {skip();};
COMMENT : ('/*' .*? '*/') | '//' (~('\n'))* {skip();} ;
fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
INSTANCEOF : 'instanceof';
EQEQ : '==';
ASIN : 'asin';
ACOS : 'acos';
ATAN : 'atan';
TAN : 'tan';
SIN : 'sin';
COS : 'cos';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
GT : '>';
LT : '<';
THIS : 'this';
NULL : 'null';
NEW : 'new';
PLUS : '+';
EXCLAM : '!';
DOT : '.';
MINUS : '-';
TIMES : '*';
PERCENT : '%';
SLASH : '/';
ELSE : 'else';
OR : '||';
OBRACE : '{';
CBRACE : '}';
OPARENT : '(';
CPARENT : ')';
COMMA : ',';
EQUALS : '=';
SEMI : ';';
PRINT : 'print';
PRINTLN : 'println';
PRINTX : 'printx';
PRINTLNX : 'printlnx';
RETURN : 'return';
WHILE : 'while';
IF : 'if';
ASM : 'asm';
TRUE : 'true';
FALSE : 'false';
READINT : 'readInt';
READFLOAT : 'readFloat';
AND : '&&';
CLASS : 'class';
EXTENDS : 'extends';
PROTECTED : 'protected';
fragment DIGIT : '0' .. '9';
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;
fragment POSITIVE_DIGIT : '1' .. '9';
INT :  '0' |  POSITIVE_DIGIT DIGIT*;
fragment NUM : DIGIT+;
fragment SIGN : '+' | '-' | ;
EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f' | );
fragment DIGITHEX : '0' .. '9' | 'A' ..  'F' | 'a'  .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' | ESPACE);
FLOAT : FLOATDEC | FLOATHEX;
fragment STRING_CAR : ~('"' | '\\' | '\n');
STRING : '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"'{doInclude(getText());};

