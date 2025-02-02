package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * String literal
 *
 * @author gl14
 * @date 01/01/2025
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        value = value.substring(1, value.length() - 1);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
        throws ContextualError 
        {
            Symbol symbol = DecacCompiler.symbolTable.create("string");
            StringType t = new StringType(symbol);
            this.setType(t);
            return t;
        }

    @Override
    public StringLiteral verifyRValue(DecacCompiler compiler,
    EnvironmentExp localEnv, ClassDefinition currentClass, 
    Type expectedType) throws ContextualError {
        if (expectedType.isString()){
            return this;
        }
        else{
            throw new ContextualError("Expected type String, got " + expectedType.getName(),getLocation());

        }
    }

    @Override 
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        if (returnType.isString()){
            this.setType(returnType);
        }
        else{
            throw new ContextualError("Expected type String, got " + returnType.getName(),getLocation());

        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("\"" + value + "\"");
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
        return "StringLiteral (" + value + ")";
    }

}
