package fr.ensimag.deca.tree;

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
public abstract class AbstractOpBool extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpBool.class);


    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify AbstractOpBool: start");
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if(type1.isBoolean() && type2.isBoolean())
        {
            LOG.debug("verify AbstractOpBool: end");
            return type1;
        }
        else
        {
            throw new ContextualError("Les opérandes doivent être de type boolean", this.getLocation());
        }
    }

}
