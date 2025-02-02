package launchers.test.deca.options.p;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class DecaP {

    private static final String TEST_DIR_OK = "src/test/deca/options/option-p/test/OK";
    private static final String TEST_DIR_KO = "src/test/deca/options/option-p/test/KO";

    private static final String OUT_DIR_OK = "src/test/deca/options/option-p/out/OK";
    private static final String OUT_DIR_KO = "src/test/deca/options/option-p/out/KO";

    private static final String DECA_FILE = "src/main/bin/decac";

    private static final String GREEN = "\033[92m";
    private static final String RED = "\033[91m";
    private static final String BLUE = "\033[94m";
    private static final String NEUTRAL = "\033[0m";

    private static void printColor(String color, String message, boolean mustWrite) 
    {
        if (mustWrite) 
        {
            System.out.println(color + message + NEUTRAL);
        }
    }

    private static void printColor(String color, String message) 
    {
        printColor(color, message, true);
    }

    private static List<String> filesToTest(String inputDir) 
    {
        try 
        {
            return Files.list(Paths.get(inputDir))
                    .filter(path -> path.toString().endsWith(".deca"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } 
        catch (IOException e) 
        {
            System.err.println("Erreur lors de la récupération des fichiers à tester");
            return new ArrayList<>();
        }
    }

    private static void writeToFile(String outputDir, String fileName, String content) 
    {
        try 
        {
            Files.writeString(Paths.get(outputDir, fileName), content);
        } 
        catch (IOException e) 
        {
            System.err.println("Erreur lors de l'écriture du fichier: " + fileName);
        }
    }

    private static String executeCommand(String[] command, boolean isOk, boolean doWrite) 
    {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));
            process.waitFor();

            if ((process.exitValue() == 0) == isOk) 
            {
                printColor(GREEN, "[ OK ]", doWrite);
                return output;
            } 
            else 
            {
                printColor(RED, "[ KO ]", doWrite);
                return output;
            }
        } 
        catch (IOException | InterruptedException e) 
        {
            System.err.println("Erreur lors de l'exécution de la commande: " + Arrays.toString(command));
            return "";
        }
    }

    private static void cleanOutputDirectories(boolean doWrite) 
    {
        System.out.print("Nettoyage: ");
        try 
        {
            for (String dir : Arrays.asList(OUT_DIR_OK, OUT_DIR_KO)) 
            {
                Files.walk(
                    Paths.get(dir)).filter(Files::isRegularFile).forEach(file -> 
                    {
                        try 
                        {
                            Files.delete(file);
                        } 
                        catch (IOException e) 
                        {
                            System.err.println("Erreur lors de la suppression du fichier: " + file);
                        }
                    });
            }

            if (Files.list(Paths.get(OUT_DIR_OK)).findAny().isEmpty()) 
            {
                printColor(GREEN, "[ OK ]", doWrite);
            } 
            else 
            {
                printColor(RED, "[ KO ]", doWrite);
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Erreur lors du nettoyage des répertoires de sortie");
        }
    }

    private static void processFiles(String inputDir, String outputDir, boolean isOk, boolean doWrite) 
    {
        List<String> files = filesToTest(inputDir);

        for (String file : files) {
            String outputFile = file.replace(".deca", "-out.deca");
            System.out.print(file + " -> " + outputFile + ": ");

            String[] command = {DECA_FILE, "-p", inputDir + "/" + file};
            String output = executeCommand(command, isOk, doWrite);

            writeToFile(outputDir, outputFile, output);
        }
    }

    private static void compareFiles(String file1, String file2, boolean doWrite) 
    {
        try 
        {
            List<String> content1 = Files.readAllLines(Paths.get(file1));
            List<String> content2 = Files.readAllLines(Paths.get(file2));

            if (content1.equals(content2)) 
            {
                printColor(GREEN, "[ OK ]", doWrite);
            } 
            else 
            {
                printColor(RED, "[ KO ]", doWrite);
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Erreur lors de la comparaison des fichiers: " + file1 + " et " + file2);
        }
    }

    private static void compareGeneratedFiles(String directory) {
        List<String> files = filesToTest(directory);

        for (String file : files) {
            String tempFile = file.replace(".deca", "-out.deca");
            String[] command = {DECA_FILE, "-p", directory + "/" + file};

            String output = executeCommand(command, true, false);
            writeToFile(directory, tempFile, output);

            System.out.print("Comparaison: " + file + " -> " + tempFile + ": ");
            compareFiles(directory + "/" + file, directory + "/" + tempFile, true);

            try 
            {
                Files.delete(Paths.get(directory, tempFile));
            } 
            catch (IOException e) 
            {
                System.err.println("Erreur lors de la suppression du fichier temporaire: " + tempFile);
            }
        }
    }

    public static void main(String[] args) {
        printColor(BLUE, "[Test deca-p: Java]");
        System.out.println("=============");
        cleanOutputDirectories(true);

        System.out.println("=============");
        printColor(BLUE, "[Test OK]");
        processFiles(TEST_DIR_OK, OUT_DIR_OK, true, true);

        System.out.println("=============");
        printColor(BLUE, "[Test KO]");
        processFiles(TEST_DIR_KO, OUT_DIR_KO, false, true);

        System.out.println("=============");
        printColor(BLUE, "[Comparaison des fichiers]");
        compareGeneratedFiles(OUT_DIR_OK);

        System.out.println("=============");
    }
}
