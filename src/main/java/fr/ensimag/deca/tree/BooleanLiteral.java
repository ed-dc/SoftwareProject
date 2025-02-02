package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 *
 * @author gl14
 * @date 01/01/2025
 */
public class BooleanLiteral extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(Boolean.class);
    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            Symbol symbol = DecacCompiler.symbolTable.create("boolean");
            BooleanType t = new BooleanType(symbol);
            this.setType(t);
            return t;
        }
        
        @Override
        public BooleanLiteral verifyRValue(DecacCompiler compiler,
        EnvironmentExp localEnv, ClassDefinition currentClass, 
        Type expectedType) throws ContextualError {
            if (expectedType.isBoolean()){
                return this;
            }
            else{
                throw new ContextualError("Expected type boolean, got " + expectedType.getName(),getLocation());
                
            }
        }
        
        @Override 
        public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass, Type returnType)
        throws ContextualError {
            LOG.debug("verify Boolean: start");
        if (returnType.isBoolean()){
            this.setType(returnType);
            LOG.debug("verify Boolean : end");
        }
        else{
            throw new ContextualError("Expected type boolean, got " + returnType.getName(),getLocation());

        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}
