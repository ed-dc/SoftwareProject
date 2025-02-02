package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl14
 * @date 01/01/2025
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public static final int WARN  = 4;

    private static int nombreRegistre = 16;
    private static boolean verification = false;
    private static boolean parse = false;

    public static boolean getParse(){
        return parse;
    }

    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public static int getNombreRegistre() {
        return nombreRegistre;
    }

    public static boolean getVerification() {
        
        return verification;
    }

    private static void setVerification() throws CLIException
    {
        if (parse)
        {
            throw new CLIException("Erreur : -v on ne peut  pas utiliser cet argument avec -p");
        }
        verification = true;
    }

    private static void setParser() throws CLIException
    {
        if (verification)
        {
            throw new CLIException("Erreur : -p on ne peut  pas utiliser cet argument avec -v");
        }
        parse = true;
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private List<File> sourceFiles = new ArrayList<File>();

    private boolean gestionDefault(boolean regardeSuivant, String s) throws CLIException
    {
        if (!regardeSuivant)
        {
            sourceFiles.add(new File(s));
        }
        else
        {
            try
            {
                int nombre = Integer.parseInt(s);
                if(nombre < 4 || nombre > 16)
                {
                    throw new CLIException("Erreur : -r doit être suivi d'un entier entre 4 et 16");
                }
                nombreRegistre = nombre;
            }
            catch (NumberFormatException e)
            {
                throw new CLIException("Erreur : -r doit être suivi d'un entier");
            }
            regardeSuivant = false;
        }
        return regardeSuivant;
    }

    public void parseArgs(String[] args) throws CLIException {
        Logger logger = Logger.getRootLogger();
        boolean regardeSuivant = false;
        boolean dispayUsageBool = false;
        for (String s : args)
        {
            if(printBanner)
            {
                return;
            }
            switch (s) {
                case "-b": 
                    printBanner = true; 
                    break; 
                case "-p": setParser(); break;
                case "-v": setVerification(); break;
                case "-n": 
                    logger.setLevel(Level.ALL); 
                    System.out.println("Veuillez gérer cette extention dans le fichier DecacCompiler.java");
                    break;
                case "-r": 
                    regardeSuivant = true;
                    break;
                case "-d": debug += INFO; break; 
                case "-P": parallel = true; break;
                case "-w": logger.setLevel(Level.WARN); break;
                case "-help": dispayUsageBool = true; break;
                default:
                    regardeSuivant = gestionDefault(regardeSuivant, s);
                    break;
            }
        }
        if (dispayUsageBool)
        {
            displayUsage();
            System.exit(0);
        }
        if (sourceFiles.isEmpty() && !printBanner)
        {
            throw new CLIException("Erreur : aucun fichier source à compiler");
        }
        // map command-line debug option to log4j's level.
        switch (getDebug()) 
        {
            case QUIET: break; // keep default
            case INFO:
                logger.setLevel(Level.INFO); break;
            case DEBUG:
                logger.setLevel(Level.DEBUG); break;
            case TRACE:
                logger.setLevel(Level.TRACE); break;
            case WARN:
                logger.setLevel(Level.WARN); break;
            default:
                logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        System.out.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println("  -b  Affiche une bannière indiquant le nom de l'équipe.");
        System.out.println("  -p  Arrête decac après l'étape de construction de l'arbre, et affiche la décompilation de ce dernier.");
        System.out.println("  -v  Arrête decac après l'étape de vérification.");
        System.out.println("  -n  N'autorise pas l'utilisation de System.out.println.");
        System.out.println("  -r X Fixe à X le nombre de registres disponibles (X doit être compris entre 4 et 16).");
        System.out.println("  -d  Augmente le niveau de debug (plusieurs -d augmentent de 1 à chaque fois).");
        System.out.println("  -P  Génère du code assembleur sans optimisations.");
        System.out.println("  -w  set le level de la journalisation a warning.");
    }
}
