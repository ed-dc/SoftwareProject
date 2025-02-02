package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.InstructionException;
import fr.ensimag.deca.tree.AbstractBinaryExpr;
import fr.ensimag.deca.tree.AbstractOpExactCmp;
import fr.ensimag.deca.tree.AbstractOpIneq;
import fr.ensimag.deca.tree.Minus;
import fr.ensimag.deca.tree.Multiply;
import fr.ensimag.deca.tree.NotEquals;
import fr.ensimag.deca.tree.Plus;
import fr.ensimag.deca.tree.Equals;
import fr.ensimag.deca.tree.Greater;
import fr.ensimag.deca.tree.GreaterOrEqual;
import fr.ensimag.deca.tree.Divide;
import fr.ensimag.deca.tree.Lower;
import fr.ensimag.deca.tree.LowerOrEqual;
import fr.ensimag.deca.tree.Modulo;
import fr.ensimag.deca.tree.LowerOrEqual;
import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;

public class CodeGenMnemo {
    

    /**
     * Generate the Assembly Instruction for Operator 
     * @param compiler compiler
     * @param op type of the operand could be +, - , *
     * @param val value to add,sub,multiply
     * @param Register
     * @throws Exception
     */
    private static int compteur = 0;
    
    public static void Mnemo(DecacCompiler compiler, AbstractBinaryExpr op, DVal val, GPRegister register) throws InstructionException{
        if (op instanceof Minus){
            compiler.addInstruction(new SUB(val, register));
        }
        else if (op instanceof Plus){
            compiler.addInstruction(new ADD(val, register));
        }
        else if (op instanceof Multiply){
            compiler.addInstruction(new MUL(val, register));
        }
        else if (op instanceof Divide){
            Divide div = (Divide) op;
            if (div.getLeftOperand().getType().isFloat() && div.getRightOperand().getType().isFloat()){
                compiler.addInstruction(new DIV(val, register));
            }
            else if (div.getLeftOperand().getType().isFloat() && div.getRightOperand().getType().isInt()){
                compiler.addInstruction(new FLOAT(val, register));
                compiler.addInstruction(new DIV(val, register));
            }
            else if (div.getLeftOperand().getType().isInt() && div.getRightOperand().getType().isFloat()){
                compiler.addInstruction(new FLOAT(val, register));
                compiler.addInstruction(new DIV(val, register));
            }
            else
            {
                compiler.addInstruction(new QUO(val, register) );
            }
        }
        else if (op instanceof Modulo){
            compiler.addInstruction(new REM(val, register));
        }
        
        else if (op instanceof Equals){
            Label l1 = new Label("EGALFalse"+compteur);
            Label lab_fin = new Label("EGALFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(val,Register.R1));
            compiler.addInstruction(new CMP(register,Register.R1));
            compiler.addInstruction(new BNE(l1));
            compiler.addInstruction(new LOAD(1,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(0,register));
            compiler.addLabel(lab_fin);
        }

        else if (op instanceof NotEquals){
            Label l1 = new Label("NotEGALFalse"+compteur);
            Label lab_fin = new Label("NotEGALFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(val,Register.R1));
            compiler.addInstruction(new CMP(register,Register.R1));
            compiler.addInstruction(new BEQ(l1));
            compiler.addInstruction(new LOAD(1,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(0,register));
            compiler.addLabel(lab_fin);
        }

        else if (op instanceof Greater){
            Label l1 = new Label("GreaterTrue"+compteur);
            Label lab_fin = new Label("GreaterFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(register,Register.R1));
            compiler.addInstruction(new CMP(val,Register.R1));
            compiler.addInstruction(new BGT(l1));
            compiler.addInstruction(new LOAD(0,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(1,register));
            compiler.addLabel(lab_fin);
        }

        else if (op instanceof GreaterOrEqual){
            Label l1 = new Label("GreaterOrEqualTrue"+compteur);
            Label lab_fin = new Label("GreaterOrEqualFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(register,Register.R1));
            compiler.addInstruction(new CMP(val,Register.R1));
            compiler.addInstruction(new BGE(l1));
            compiler.addInstruction(new LOAD(0,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(1,register));
            compiler.addLabel(lab_fin);
        }

        else if (op instanceof Lower){
            Label l1 = new Label("LowerTrue"+compteur);
            Label lab_fin = new Label("LowerFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(register,Register.R1));
            compiler.addInstruction(new CMP(val,Register.R1));
            compiler.addInstruction(new BLT(l1));
            compiler.addInstruction(new LOAD(0,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(1,register));
            compiler.addLabel(lab_fin);
        }

        else if (op instanceof LowerOrEqual){
            Label l1 = new Label("LowerOrEqualTrue"+compteur);
            Label lab_fin = new Label("LowerOrEqualFin"+compteur);
            compteur++;
            compiler.addInstruction(new LOAD(register,Register.R1));
            compiler.addInstruction(new CMP(val,Register.R1));
            compiler.addInstruction(new BLE(l1));
            compiler.addInstruction(new LOAD(0,register));
            compiler.addInstruction(new BRA(lab_fin));
            compiler.addLabel(l1);
            compiler.addInstruction(new LOAD(1,register));
            compiler.addLabel(lab_fin);
        }

        else{
            throw new InstructionException();   
        }
    }



}
