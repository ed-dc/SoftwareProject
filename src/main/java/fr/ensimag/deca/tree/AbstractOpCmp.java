package fr.ensimag.deca.tree;

import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl14
 * @date 01/01/2025
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpCmp.class);


    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify AbstractOpCmp: start");
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if((type1.isInt() || type1.isFloat()) && (type2.isInt() || type2.isFloat()))
        {
            if(type1.isInt() && type2.isFloat()) // Si type1 (leftOperand) est un int alors on convertit le leftOperand en float
            {
                ConvFloat conv = new ConvFloat(this.getLeftOperand());
                this.setLeftOperand(conv);
            }
            else if (type1.isFloat() && type2.isInt()) // Si type2 (rightOperand) est un int alors on convertit le rightOperand en float
            {
                ConvFloat conv = new ConvFloat(this.getRightOperand());
                this.setRightOperand(conv);
            }
            LOG.debug("verify AbstractOpCmp: end");
            this.setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        else
        {
            throw new ContextualError("Les opérandes doivent être de type int ou float", this.getLocation());
        }
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        this.setType(compiler.environmentType.BOOLEAN);
    }

    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        this.verifyExpr(compiler, localEnv, currentClass);
        this.setType(compiler.environmentType.BOOLEAN);
        return this;
    }

}
