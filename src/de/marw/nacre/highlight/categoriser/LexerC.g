header {
package swing.text.highlight.categoriser;
}
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Lexer
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
{
//++++++++++++++++++++++++++++ Class preamble
import java.io.*;
import antlr.*;
//++++++++++++++++++++++++++++ end Class preamble
}

/**
 * Der Lexer fuer Standard/ISO C. Alle anderen Lexer fuer die
 * Tessy-Crosscompiler sind von dieser Basisgrammatik abgeleitet.
 */
class LexerC extends Lexer;

options
{
	k = 3;
//	exportVocab = LexerC;
	testLiterals = false;
}



{
//++++++++++++++++++++++++++++ user defined code (fields and methods)

//++++++++++++++++++++++++++++ end user defined code
}

//++++++++++++++++++++++++++++ rules

// a dummy rule to force vocabulary to be all characters (except special
//   ones that ANTLR uses internally (0 to 2)
protected
Vocabulary
	:       '\3'..'\377'
	;


/* Operators: */
OPERATOR: ( '=' | ':' | ',' | '?' | ';' | "->" 
	| '(' | ')' | '[' | ']' | '{' | '}' | "==" 
	| "!=" | "<=" | "<" | ">=" | ">" | '/' | "/=" 
	| '+' | "+=" | "++" | '-' | "-=" | "--" | '*' 
	| "*=" | '%' | "%=" | ">>" | ">>=" | "<<" | "<<=" 
	| "&&" | '!' | "||" | '&' | "&=" | '~' 
	| '|' | "|=" | '^' | "^=" 
	);


// DOT & VARARGS are commented out since they are generated as part of
// the Number rule below due to some bizarre lexical ambiguity shme.

// DOT  :       '.' ;
protected
DOT:;

// VARARGS      : "..." ;
protected
VARARGS:;



protected Space:
	( ' ' | '\t' | '\f')
	;

//Whitespace
Whitespace
	:       ( ( '\003'..'\010' | Space  | '\016'.. '\037' | '\177'..'\377' )
		// handle newlines
		|	(	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
		)
	;

protected
Newline
	: (	/*	'\r' '\n' can be matched in one alternative or by matching
			'\r' in one iteration and '\n' in another.  I am trying to
			handle any flavor of newline that comes in, but the language
			that allows both "\r\n" and "\r" and "\n" to all be valid
			newline is ambiguous.  Consequently, the resulting grammar
			must be ambiguous.  I'm shutting this warning off.
		 */
		options { generateAmbigWarnings=false; }
		:	"\r\n"
		|	'\r'
		|	'\n'
	  ) {}
	;


// multiple-line comments
Comment
	:	"/*"
		(	{ LA(2)!='/' }? '*'
		|	Newline
		|	~('*'|'\n'|'\r')
		)*
		"*/"
	;

CPPComment
	:
		"//" ( ~('\n') )*
	;

// testen mit: gcc -E -ansi -pedantic -o - proper-split.C |grep "^#"
PREPROC_DIRECTIVE
	:
	'#' (Space)+ ( "include" | "ifndef" | "ifdef"
		| "if" | "elif" | "else" | "define" | "undef"
		| "endif" | "error" | "line" | "pragma")
	;


/* Literals: */

/*
 * Note that we do NOT handle tri-graphs nor multi-byte sequences.
 */


/*
 * Note that we can't have empty character constants (even though we
 * can have empty strings :-).
 */
CharLiteral
	:       '\'' ( Escape | ~( '\'' | '\r' | '\n' | '\\' ))+ '\''
	;


/*
 * Can't have raw imbedded newlines in string constants.  Strict reading of
 * the standard gives odd dichotomy between newlines & carriage returns.
 * Go figure.
 */
StringLiteral
	:       '"'
		( Escape
		  | Newline 	{ _ttype = BadStringLiteral; }
		  // String mit Zeilenumbruch
		  | '\\' Newline
		  | ~( '"' | '\r' | '\n' | '\\' )
		)*
		'"'
	;

// StringLinteral in einer Zeile
protected
StringLiteralSingleLine
	:       '"' ( Escape | ~( '"' | '\r' | '\n' | '\\' ))* '"'
	;


protected BadStringLiteral
	:       // Imaginary token.
	;


/*
 * Handle the various escape sequences.
 *
 * Note carefully that these numeric escape *sequences* are *not* of the
 * same form as the C language numeric *constants*.
 *
 * There is no such thing as a binary numeric escape sequence.
 *
 * Octal escape sequences are either 1, 2, or 3 octal digits exactly.
 *
 * There is no such thing as a decimal escape sequence.
 *
 * Hexadecimal escape sequences are begun with a leading \x and continue
 * until a non-hexadecimal character is found.
 *
 * No real handling of tri-graph sequences, yet.
 */

protected
Escape
	:       '\\'
		( options{warnWhenFollowAmbig=false;}:
		'a'
		| 'b'
		| 'f'
		| 'n'
		| 'r'
		| 't'
		| 'v'
		| '"'
		| '\''
		| '\\'
		| '?'
		| ('0'..'3') ( options{warnWhenFollowAmbig=false;}
			: Digit ( options{warnWhenFollowAmbig=false;}: Digit )? )?
		| ('4'..'7') ( options{warnWhenFollowAmbig=false;}
			: Digit )?
		| 'x' ( options{warnWhenFollowAmbig=false;}: Digit | 'a'..'f' | 'A'..'F' )+
		)
	;


/* Numeric Constants: */

protected
Digit
	:       '0'..'9'
	;

protected
LongSuffix
	:       'l' |  'L'
	;

protected
UnsignedSuffix
	:       'u' | 'U'
	;

protected
FloatSuffix
	:       'f' | 'F'
	;

protected
Exponent
	:       ( 'e' | 'E' ) ( '+' | '-' )? ( Digit )+
	;

Number
	:       ( ( Digit )+ ( '.' | 'e' | 'E' ) )=> ( Digit )+
		( '.' ( Digit )* ( Exponent )? | Exponent)
		( FloatSuffix | LongSuffix )?

	|       ( "..." )=> "..."       { _ttype = VARARGS;     }

	|       '.'                     { _ttype = DOT; }
		( ( Digit )+ ( Exponent )? ( FloatSuffix | LongSuffix)?
		)?

	|       '0' ( '0'..'7' )* ( LongSuffix | UnsignedSuffix )?

	|       '1'..'9' ( Digit )* ( LongSuffix | UnsignedSuffix )?

	|       '0' ( 'x' | 'X' ) ( 'a'..'f' | 'A'..'F' | Digit )+
		( LongSuffix | UnsignedSuffix )?
	;



/** an identifier.  Note that testLiterals is set to true!  This means
 * that after we match the rule, we look in the literals table to see
 * if it's a literal or really an identifer
 */
ID
options {
		testLiterals=true;
		paraphrase = "an identifier";
	}
	: (IDletter Digit) =>  IDletter	(IDletter | Digit)*
	;

TypeKeyword
	:  "char"
 |"double"
 |"enum"
 |"float"
 |"int"
 |"long"
 |"short"
 |"signed"
 |"struct"
 |"typedef"
 |"union"
 |"unsigned"
 |"void"
 |"auto"
 |"const"
 |"extern"
 |"register"
 |"static"
 |"volatile"
 |"far" |"huge" |"inline" |"near" |"pascal"
	;

StmtKeyword
	: "asm" |"break" |"case" |"continue" |"default"
	 |"do" |"else" |"for" |"goto" |"if" |"return"
	 |"sizeof" |"switch" |"while"
	;
/** Liefert erstes Zeichen eines Identifiers, zum Ueberschreiben
*/
protected
IDletter
	: ('a'..'z'|'A'..'Z'|'_')
	;