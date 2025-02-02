package fr.ensimag.deca.codegen;
import java.util.HashMap;

import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import net.bytebuddy.asm.MemberSubstitution.Substitution.Chain.Step.ForField.Read;


public class CodeGenDval {

    private static int nb_GB = 3; //Static Variable incremented to store @symb in the pile

    /* ATTENTION ICI LE DEPASSEMENT DE REGISTRE PAS PRIS EN COMPTE */

    /**
     * Function use to return either @symbol if expr is an Identifier or #n if expr is an IntLiteral and Null if expr is Null
     * @param expr
     */
    public static DVal wCodeGenDVal(AbstractExpr expr) 
    {
        if (expr instanceof IntLiteral){
            return new ImmediateInteger(((IntLiteral)expr).getValue());
        }
        else if (expr instanceof FloatLiteral){
            return new ImmediateFloat(((FloatLiteral) expr).getValue());
        }
        else if (expr instanceof BooleanLiteral){
            BooleanLiteral expr_bool = (BooleanLiteral) expr;
            if (expr_bool.getValue()){
                return new ImmediateInteger(1);
            }
            else{
                return new ImmediateInteger(0);
            }
        }
        else if (expr instanceof ConvFloat){
            ConvFloat conv = (ConvFloat) expr;
            return new NullOperand();
        }
        else if (expr instanceof Identifier){
            Identifier ident = (Identifier)expr;
            VariableDefinition varDef = ident.getVariableDefinition();
            if (varDef.getOperand() instanceof RegisterOffset)
            {
                return varDef.getOperand();
            }
            else
            {
                RegisterOffset reg = new RegisterOffset(nb_GB,Register.GB);
                varDef.setOperand(reg);
                nb_GB++;
                return reg;
            }
        }
        else if (expr instanceof ReadInt)
        {
            return new RegisterOffset(0,Register.getR(2));
        }
        else if (expr instanceof ReadFloat)
        {
            return new RegisterOffset(0,Register.getR(2));
        }
        else if (expr instanceof Not){
            Not expr_not = (Not) expr;  
            BooleanLiteral expr_bool = (BooleanLiteral) expr_not.getOperand();
            if (expr_bool.getValue()){
                return new ImmediateInteger(0);
            }
            else{
                return new ImmediateInteger(1);
            }
        }
        else{
            return new NullOperand();
        }
    }

}
