package fr.ensimag.deca;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl14
 * @date 01/01/2025
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    private static void enleverErreursAll(){

    }
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions compileurOptions = new CompilerOptions();
        try 
        {
            compileurOptions.parseArgs(args);
            // Object o = (Object) Level.ALL;
            // if (o!=null && LOG.getLevel().equals(o)){
            //     enleverErreursAll(); // A FAIRE 
            // }
        } 
        catch (CLIException e) 
        {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            compileurOptions.displayUsage();
            System.exit(1);
        }
        
        if (compileurOptions.getPrintBanner()) {
            System.out.println("Equipe numéro 14");
            return;
        }
        if (compileurOptions.getSourceFiles().isEmpty()) {
            System.out.println("Aucun fichier source à compiler");
        }
        
        if (compileurOptions.getParallel()) 
        {
            error = DecacCompiler.gereParallele(compileurOptions, error);
        } 
        else 
        {
            for (File source : compileurOptions.getSourceFiles()) 
            {
                DecacCompiler compiler = new DecacCompiler(compileurOptions, source);
                if (compiler.compile())
                {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
