package fr.ensimag.deca.codegen;

import static org.mockito.ArgumentMatchers.eq;

import fr.ensimag.deca.*;
import fr.ensimag.deca.tree.*;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.*;


public class CodeGenPrintGen {
    
    
    /**
     * Code Generation for printing expr !! FUNCTIONNAL ONLY FOR ARITHMETICAL EXPRESSION
     * @param compiler
     * @param e
     * @param type
     */
    public static void GenCodePrint(DecacCompiler compiler, AbstractExpr e, boolean printex){
        if (e instanceof AbstractBinaryExpr){
            CodeGenCodeExp.GenCodeExp(compiler,e,2);
            compiler.addInstruction(new LOAD(Register.getR(2), Register.getR(1)));
            AbstractExpr e1 = ((AbstractBinaryExpr)e).getLeftOperand();
            if (e1.getType().isInt())
            {
                compiler.addInstruction(new WINT());
            }
            else if(e1.getType().isFloat()) 
            {
                if (printex)
                {
                    compiler.addInstruction(new WFLOATX());
                }
                else{
                    compiler.addInstruction(new WFLOAT());
                }
            }
            else if (e1.getType().isBoolean())
            {
                BooleanLiteral ebl = (BooleanLiteral) e1;
                compiler.addInstruction(new WSTR(new ImmediateString(Boolean.toString(ebl.getValue()))));
            }
            else if (e1.getType().isString())
            {
                StringLiteral est = (StringLiteral) e1;
                compiler.addInstruction(new WSTR(new ImmediateString(est.getValue())));
            }

        }

        if (e instanceof IntLiteral){
            IntLiteral eint = (IntLiteral) e;
            compiler.addInstruction(new LOAD(eint.getValue(),Register.getR(1)));
            compiler.addInstruction(new WINT());
        }

        else if(e instanceof FloatLiteral){
            FloatLiteral efl = (FloatLiteral) e;
            compiler.addInstruction(new LOAD(new ImmediateFloat(efl.getValue()),Register.R1));
            if (printex){
                compiler.addInstruction(new WFLOATX());
            }
            else{
                compiler.addInstruction(new WFLOAT());
            }
        }
        else if (e instanceof BooleanLiteral){
            BooleanLiteral ebl = (BooleanLiteral) e;
            compiler.addInstruction(new WSTR(new ImmediateString(Boolean.toString(ebl.getValue()))));
        }
        else if (e instanceof StringLiteral){
            StringLiteral est = (StringLiteral) e;
            compiler.addInstruction(new WSTR(new ImmediateString(est.getValue())));
        }
        else if( e instanceof AbstractIdentifier)
        {
            AbstractIdentifier eid = (AbstractIdentifier)e;
            VariableDefinition varDef = eid.getVariableDefinition();
            compiler.addInstruction(new LOAD((DVal)varDef.getOperand(),Register.R1));
            if (e.getType().isInt()){
                compiler.addInstruction(new WINT());
            }
            else if(e.getType().isFloat()){
                if (printex){
                    compiler.addInstruction(new WFLOATX());
                }
                else{
                    compiler.addInstruction(new WFLOAT());
                }
            }
            else if (e.getType().isBoolean()){
                compiler.addInstruction(new CMP(0, Register.R1));
                Label ETrue = new Label(eid.decompile()+"True");
                Label EFalse = new Label(eid.decompile()+"False");
                Label EFin = new Label(eid.decompile()+"Fin");
                compiler.addInstruction(new BNE(ETrue));
                compiler.addLabel(EFalse);
                compiler.addInstruction(new WSTR(new ImmediateString("false")));
                compiler.addInstruction(new BRA(EFin));
                compiler.addLabel(ETrue);
                compiler.addInstruction(new WSTR("true"));
                compiler.addLabel(EFin);
            }
            else if (e.getType().isString())
            {
                DVal adresseDebutString = CodeGenDval.wCodeGenDVal(e);

                // On récupère la taille de la chaine de caractère : optimisation possible en faisait une boucle while en assembleur

                int taille = CodeGenCodeExp.adresseTaille.get(adresseDebutString);
                compiler.addInstruction(new LOAD(adresseDebutString, Register.getR(2)));
                for (int i = 1; i < taille + 1; i++)
                {
                    compiler.addInstruction(new LOAD(new RegisterOffset(i, Register.getR(2)), Register.getR(1)));
                    compiler.addInstruction(new WUTF8());
                    compiler.addLabel(null);
                }

            }
        }
        else if (e instanceof AbstractReadExpr)
        {
            AbstractReadExpr eread = (AbstractReadExpr)e;
            if (eread.getType().isInt())
            {
                compiler.addInstruction(new RINT());
                compiler.addInstruction(new WINT());
            }
            else if (eread.getType().isFloat())
            {
                compiler.addInstruction(new RFLOAT());
                if (printex){
                    compiler.addInstruction(new WFLOATX());
                }
                else{
                    compiler.addInstruction(new WFLOAT());
                }
            }
        }

    }

}
