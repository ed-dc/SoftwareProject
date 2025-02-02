package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *
 * @author gl14
 * @date 01/01/2025
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(type1.isBoolean())
        {
            this.setType(type1);
            return type1; // On est forcément en boolean
        }
        else
        {
            throw new ContextualError("L'opérande doit être de type boolean", this.getLocation());
        }
    }

    @Override
    public AbstractExpr verifyRValue(DecacCompiler compiler,
    EnvironmentExp localEnv, ClassDefinition currentClass, 
    Type expectedType) throws ContextualError {
        this.getOperand().verifyRValue(compiler, localEnv, currentClass, expectedType);
        if (expectedType.isBoolean()){
            return this;
        }
        else{
            throw new ContextualError("Expected type Boolean, got " + expectedType.getName(),getLocation());
            
        }
    }
    
    @Override 
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (returnType.isBoolean()){
            this.setType(returnType);
        }
        else{
            throw new ContextualError("Expected type Boolean, got " + returnType.getName(),getLocation());

        }
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

}
