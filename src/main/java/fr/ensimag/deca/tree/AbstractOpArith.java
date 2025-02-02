package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl14
 * @date 01/01/2025
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
                this.getLeftOperand().verifyRValue(compiler, localEnv, currentClass, expectedType); 
                this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, expectedType);
                return this;
            }

    @Override 
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        this.getLeftOperand().verifyInst(compiler, localEnv, currentClass, returnType);
        this.getRightOperand().verifyInst(compiler, localEnv, currentClass, returnType);
        this.setType(returnType);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError 
            {
        //on vérifie que les deux opérandes sont de type int ou float
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        Type finaType;
        if((type1.isInt() || type1.isFloat()) && (type2.isInt() || type2.isFloat()))
        {
            if(type1.isInt() && type2.isInt())
            {
                finaType = type1; // On est forcément en int 
            }
            else if(type1.isFloat() && type2.isFloat()) // On est un float alors on renvoie
            {
                finaType =  type1;
            }
            else if(type1.isInt()) // Si type1 (leftOperand) est un int alors on convertit le leftOperand en float
            {
                ConvFloat conv = new ConvFloat(this.getLeftOperand());
                this.setLeftOperand(conv);
                this.getLeftOperand().setType(compiler.environmentType.FLOAT);
                finaType = type2;
            }
            else // Si type2 (rightOperand) est un int alors on convertit le rightOperand en float
            {
                ConvFloat conv = new ConvFloat(this.getRightOperand());
                this.setRightOperand(conv);
                this.getRightOperand().setType(compiler.environmentType.FLOAT);
                finaType = type1;
            }
            return finaType;
        }
        else
        {
            throw new ContextualError("Les opérandes doivent être de type int ou float", this.getLocation());
        }

    }
}
