package fr.ensimag.deca.syntax;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author Ensimag
 * @date 01/01/2025
 */
public class ManualTestLex {
    // This is a test class, we do not try to give user-friendly error messages
    // but just throw exception to the user if something goes wrong (=> throws
    // IOException)
    public static void main(String[] args) throws IOException 
    {
        if(args == null || args.length == 0)
        {
            System.err.println("Usage: ManualTestLex <file1> <file2> ...");
            System.exit(1);
        }
        Logger.getRootLogger().setLevel(Level.DEBUG);
        DecaLexer lex = AbstractDecaLexer.createLexerFromArgs(args);
        System.exit(lex.debugTokenStream() ? 1 : 0);
    }
}
