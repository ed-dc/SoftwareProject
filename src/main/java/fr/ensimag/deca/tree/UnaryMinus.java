package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * @author gl14
 * @date 01/01/2025
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type currentType = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!currentType.isInt() && !currentType.isFloat()) {
            throw new ContextualError("Unary minus can only be applied to int and float", getLocation());
        }
        return currentType;
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

}
