package fr.ensimag.deca.codegen;



import fr.ensimag.deca.tree.*;

import java.util.HashMap;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CodeGenDval;
import fr.ensimag.deca.codegen.CodeGenMnemo;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

public class CodeGenCodeExp {
    
    public static HashMap<DVal, Integer> adresseTaille = new HashMap<>();

    /* ATTENTION PAS ENCORE PRIS EN COMPTE DANS LES FONCTIONS CI DESSSOUS LA LIMITE DE REGISTRE */

    public static void GenCodeExp(DecacCompiler compiler, AbstractExpr e1, AbstractExpr e2, int n)
    {
        boolean isString = e2.getType().isString();
        if (isString)
        {
            int taille = adresseTaille.get(CodeGenDval.wCodeGenDVal(e2));
            adresseTaille.put((DAddr)CodeGenDval.wCodeGenDVal(e1), taille);
            GenCodeExp(compiler, e2, n);
        }
        else
        {
            GenCodeExp(compiler, e2, n);
        }
    }

    public static void GenCodeExp(DecacCompiler compiler, AbstractExpr e, int n)
    {
        if (e instanceof AbstractBinaryExpr)
        {

            // CASE WHERE e IS AN OPERATION
            AbstractBinaryExpr operand = (AbstractBinaryExpr) e;
            AbstractExpr e1 = operand.getLeftOperand();
            AbstractExpr e2 = operand.getRightOperand();
            if (operand instanceof Assign && e1 instanceof AbstractIdentifier)
            {
                DAddr currentAddr = (DAddr)CodeGenDval.wCodeGenDVal(e1);
                int taille = 0;
                try
                {
                    //Case of assignment
                    if (e2 instanceof AbstractOpBool)
                    {
                        AbstractIdentifier e1id = (AbstractIdentifier) e1;
                        GenCodeBool(compiler, e2, false, new Label(e1.decompile()+"E"),n, e1id, true);
                    }
                    else if (e2 instanceof StringLiteral)
                    {
                        taille = GenCodeString(compiler, e2, n);
                        compiler.addInstruction(new STORE(Register.getR(n), currentAddr));
                    }
                    else if (e2 instanceof AbstractReadExpr)
                    {
                        GenCodeReader(compiler, e1,e2, n);
                    }
                    else
                    {
                        GenCodeExp(compiler, e1, e2, n);        
                        compiler.addInstruction(new STORE(Register.getR(n),(DAddr)CodeGenDval.wCodeGenDVal(e1)));
                    }
                }
                catch(Exception ie)
                {
                    System.err.println(ie.getMessage());
                }
                
                if (taille != 0)
                {
                    adresseTaille.put(currentAddr, taille);
                }
            }   
            else
            {
                try
                {
                    if (e instanceof AbstractOpBool)
                    {
                        GenCodeBool(compiler,e,true,new Label(e1.decompile()+"E"),n,null,false);
                    }

                    if(CodeGenDval.wCodeGenDVal(e2) instanceof NullOperand)
                    {
                        if (n < 15)
                        {
                            //Case where  <Dval(e2)> = Null && n < 15
                            GenCodeExp(compiler, e1, n);
                            GenCodeExp(compiler, e2, n+1);
                            CodeGenMnemo.Mnemo(compiler, operand,  Register.getR(n+1), Register.getR(n));
                        }
                        else
                        {
                            //Case where <Dval(e2)> = Null && n = 15    
                            GenCodeExp(compiler, e1, n);
                            compiler.addInstruction(new PUSH(Register.getR(n)));
                            GenCodeExp(compiler, e2, n);
                            compiler.addInstruction(new LOAD(Register.getR(n), Register.R0));
                            compiler.addInstruction(new POP(Register.getR(n)));
                            CodeGenMnemo.Mnemo(compiler, operand,  Register.R0, Register.getR(n));
                        }
                    }
                    else
                    {
                        // 1st Case where <Dval(e2)> != Null
                        GenCodeExp(compiler, e1, n);
                        CodeGenMnemo.Mnemo(compiler, operand, CodeGenDval.wCodeGenDVal(e2), Register.getR(n));
                    }
                    
                }
                catch(Exception ie2)
                {
                    System.err.println(ie2.getMessage());
                }
            }
        }
        else
        {
            // BASE CASE
            if (e instanceof ConvFloat)
            {
                System.out.println("ConvFloat : pas encore pris en compte");
                compiler.addInstruction(new FLOAT(CodeGenDval.wCodeGenDVal(e), Register.getR(n))); // Load en meme temps
            }
            else if (e instanceof AbstractReadExpr)
            {
                GenCodeReader(compiler, e, n);
            }
            else
            {
                compiler.addInstruction(new LOAD(CodeGenDval.wCodeGenDVal(e), Register.getR(n)));
            }
        }
    }

    private static int compteur = 0;    
    /**
     * Code Generation for Boolean expression
     * @param compiler
     * @param e
     * @param branch
     * @param line
     */
    public static void GenCodeBool(DecacCompiler compiler , AbstractExpr e, boolean branch, Label lab, Integer n, AbstractIdentifier e1, boolean assign){
        
        //Base Cas
        if (e instanceof BooleanLiteral){
            BooleanLiteral ebl = (BooleanLiteral) e;
            if (assign){
                compiler.addInstruction(new LOAD(CodeGenDval.wCodeGenDVal(e),Register.R1));
                compiler.addInstruction(new CMP(0,Register.R1));
            }
            else{
                if (ebl.getValue()){
                    if (branch){
                        compiler.addInstruction(new BRA(lab));
                    }
                    else{

                        // Nothing happens
                    }
                }
                else{
                    GenCodeBool(compiler, new Not(e), branch, lab,n, e1,assign);
                }
            }
        }
        
        else if (e instanceof AbstractOpCmp){
            AbstractOpCmp expr_cmp = (AbstractOpCmp) e;
            GenCodeExp(compiler, expr_cmp, 2);
            compiler.addInstruction(new CMP(0, Register.getR(2)));
            compiler.addInstruction(new BEQ(lab));
            
            
        }

        //Not Case
        else if (e instanceof Not){
            Not enot = (Not) e;
            if (enot.getOperand() instanceof BooleanLiteral){   
                BooleanLiteral e_bool = (BooleanLiteral) enot.getOperand();
                BooleanLiteral new_bool = new BooleanLiteral(!e_bool.getValue());
                GenCodeBool(compiler,new_bool, !branch, lab, n ,e1,assign);
            }
            else{   
                if (enot.getOperand() instanceof And ){
                    
                    GenCodeBool(compiler, enot.getOperand(), !branch, lab,n,e1,assign);
                }
                
            }
            
        }

        //Operator Boolean Case
        else if (e instanceof AbstractOpBool){
            if (e instanceof And){
                And expr_and = (And) e; 
                Label lab_fin = new Label(lab.toString()+"Fin"+compteur);
                Label l1 = new Label(lab.toString()+"False"+compteur);
                compteur ++;
                if (assign){
                    GenCodeBool(compiler,expr_and.getLeftOperand(),false,lab,n, e1,assign);
                    compiler.addInstruction(new BEQ(l1));
                    GenCodeBool(compiler, expr_and.getRightOperand(), false, lab,n, e1,assign);
                    compiler.addInstruction(new BEQ(l1));
                    compiler.addInstruction(new STORE(Register.R1,(DAddr)CodeGenDval.wCodeGenDVal(e1)));
                    compiler.addInstruction(new BRA(lab_fin));
                    compiler.addLabel(l1);
                    compiler.addInstruction(new LOAD(0,Register.R1));
                    compiler.addInstruction(new STORE(Register.R1,(DAddr)CodeGenDval.wCodeGenDVal(e1)));
                    compiler.addLabel(lab_fin);
                }
                else{
                    if (branch){
                        GenCodeBool(compiler,expr_and.getLeftOperand(),false,lab_fin,n, e1,assign);
                        GenCodeBool(compiler, expr_and.getRightOperand(), true, lab,n, e1,assign);
                        compiler.add(new Line(lab_fin));
                    }
                    else{
                        GenCodeBool(compiler,expr_and.getLeftOperand(),false,lab,n, e1,assign);
                        GenCodeBool(compiler, expr_and.getRightOperand(), false, lab,n, e1,assign); 
                    }
                }
                
            }

            else if (e instanceof Or){
                Or expr_or = (Or) e; 
                Label lab_fin = new Label(lab.toString()+"Fin"+compteur);
                Label l1 = new Label(lab.toString()+"True"+compteur);
                compteur ++;
                if (assign){
                    GenCodeBool(compiler,expr_or.getLeftOperand(),false,lab,n, e1,assign);
                    compiler.addInstruction(new BNE(l1));
                    GenCodeBool(compiler, expr_or.getRightOperand(), false, lab,n, e1,assign);
                    compiler.addInstruction(new BNE(l1));
                    compiler.addInstruction(new LOAD(0,Register.R1));
                    compiler.addInstruction(new STORE(Register.R1,(DAddr)CodeGenDval.wCodeGenDVal(e1)));
                    compiler.addInstruction(new BRA(lab_fin));
                    compiler.addLabel(l1);
                    compiler.addInstruction(new LOAD(1,Register.R1));
                    compiler.addInstruction(new STORE(Register.R1,(DAddr)CodeGenDval.wCodeGenDVal(e1)));
                    compiler.addLabel(lab_fin);
                }
                else{
                    And expr = new And(new Not(expr_or.getLeftOperand()), new Not(expr_or.getRightOperand()));
                    GenCodeBool(compiler, new Not(expr), branch, lab,n,e1, assign);
                }
            }


        }
        //Identifier Case
        else if (e instanceof Identifier){

            compiler.addInstruction(new LOAD(CodeGenDval.wCodeGenDVal(e), Register.R0));
            compiler.addInstruction(new CMP(0,Register.R0));

            
        }
        
    }

    /**
     * Code Generation for Boolean expression
     * @param compiler
     * @param e
     * @param n
     */
    public static int GenCodeString(DecacCompiler compiler, AbstractExpr e2, int n)
    {
        if (e2 instanceof StringLiteral){
            StringLiteral currentStringLiteral = (StringLiteral)e2;
            String s = currentStringLiteral.getValue();
            int taille  = s.length();

            compiler.addInstruction(new NEW(taille + 1, Register.getR(n))); // On va stocker la taille de la chaine de caractère au début de la chaine

            compiler.addInstruction(new LOAD(new ImmediateInteger(taille), Register.R1));
            compiler.addInstruction(new STORE(Register.R1, (DAddr)new RegisterOffset(0, Register.getR(n))));

            int[] unicode = new int[taille];
            for (int i = 0; i < taille; i++){
                unicode[i] = (int)s.charAt(i);
                // On LOAD sous le format : LOAD #unicode[i], i(Rn)
                compiler.addInstruction(new LOAD(new ImmediateInteger(unicode[i]), Register.R1));
                compiler.addInstruction(new STORE(Register.R1, (DAddr)new RegisterOffset(i + 1, Register.getR(n))));
                // ajout d'une nouvelle ligne pour ameliorer la lisibilité
                compiler.addLabel(null);
            }
            return taille;          
        }
        else if (e2 instanceof Identifier)
        {
            compiler.addInstruction(new LOAD(CodeGenDval.wCodeGenDVal(e2), Register.getR(n)));
            DVal adresseDebutString = CodeGenDval.wCodeGenDVal(e2);
            int taille = adresseTaille.get(adresseDebutString);
            return taille;
        }
        return 0;
    }

    public static void GenCodeReader(DecacCompiler compiler, AbstractExpr e1 ,AbstractExpr e2, int n)
    {   
        

        if (e2 instanceof ReadInt)
        {
            compiler.addInstruction(new RINT());
            compiler.addInstruction(new STORE(Register.getR(1), (DAddr)CodeGenDval.wCodeGenDVal(e1)));
        }
        else if (e2 instanceof ReadFloat)
        {
            compiler.addInstruction(new RFLOAT());
            compiler.addInstruction(new STORE(Register.getR(1), (DAddr)CodeGenDval.wCodeGenDVal(e1)));
        }
        
           
        
    }

    public static void GenCodeReader(DecacCompiler compiler, AbstractExpr e2, int n){

        if (e2 instanceof ReadInt)
        {
            compiler.addInstruction(new RINT()); 
    
        }
        else if (e2 instanceof ReadFloat)
        {
            compiler.addInstruction(new RFLOAT());
        }
    }

}
