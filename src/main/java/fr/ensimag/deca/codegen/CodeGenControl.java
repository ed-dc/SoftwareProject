package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CodeGenCodeExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.tree.*;

/** 
 * Class used to Generate the code for the control structures 
 * */
public class CodeGenControl {

    public static int n = 0;
    private static int nb_End = 0;    


    /**
     * Generate the code for the start of an if statement
     * @param compiler
     * @param condition
     * @param end
     */
    public static void wCodeGenIf(DecacCompiler compiler,AbstractExpr condition){
        
        Label lab = new Label("E_sinon" + n);
        
        CodeGenCodeExp.GenCodeBool(compiler, condition, false,lab, 2, null, false);

    }



    /**
     * Generate the code for the end of an if statement
     * @param compiler
     * @param end
     */
    public static Label wCodeGenEndIf(DecacCompiler compiler, Label lab_debut,  boolean end){
        if (end){
            Label lab = new Label("E_fin" + nb_End);
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(lab));
            compiler.addLabel(new Label("E_sinon" + (n)));
            n++;
            return lab;
        }
        else{
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(lab_debut));
            compiler.addLabel(new Label("E_sinon" + (n)));
            n++;
            return lab_debut;
        }
        
    }


    public static void wCodeGenFinalEndIf(DecacCompiler compiler){
        
        compiler.addLabel(new Label("E_fin" + nb_End));
        nb_End++;
        
    }

        // public static void wCodeGenDebWHile(DecacCompiler compiler){
        //     Label lab = new Label("E_debut " + n);
        //     compiler.addLabel();
        //     compiler.addLabel(new Label("E_debut" + n));
        // }


    
}
