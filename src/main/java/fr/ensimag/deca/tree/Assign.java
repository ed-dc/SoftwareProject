package fr.ensimag.deca.tree;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl14
 * @date 01/01/2025
 */
public class Assign extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(Assign.class);

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            LOG.debug("verify Assign: start");
                this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
                this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
            if (this.getLeftOperand().getType() != this.getRightOperand().getType()){
                throw new ContextualError("Type error between the 2 operands", getLocation());
            }
            else{
                this.setType(this.getLeftOperand().getType());
                LOG.debug("verify Assign: end");
                return this.getType();
            }
    }

    @Override 
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        LOG.debug("verify Assign: start");
        getLeftOperand().verifyInst(compiler, localEnv, currentClass, returnType);
        getRightOperand().verifyInst(compiler, localEnv, currentClass, getLeftOperand().getType());
        if (this.getLeftOperand().getType() != this.getRightOperand().getType()){
            throw new ContextualError("Type error between the 2 operands", getLocation());
        }
        else{
            this.setType(this.getLeftOperand().getType());
        }
        LOG.debug("verify Assign: end");
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
