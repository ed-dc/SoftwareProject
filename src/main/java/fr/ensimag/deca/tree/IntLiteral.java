package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Integer literal
 *
 * @author gl14
 * @date 01/01/2025
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }
     private static final Logger LOG = Logger.getLogger(Integer.class);

    private int value;

    public IntLiteral(int value) {
        this.value = value;
        // Faire attention quand il y aura l'environnement avec hexadecimal
       
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Symbol symbol = DecacCompiler.symbolTable.create("int");
        IntType t = new IntType(symbol);
        this.setType(t);
        return t;
    }


    @Override
    public IntLiteral verifyRValue(DecacCompiler compiler,
    EnvironmentExp localEnv, ClassDefinition currentClass, 
    Type expectedType) throws ContextualError {
        if (expectedType.isInt()){
            return this;
        }
        else{
            throw new ContextualError("Expected type int, got " + expectedType.getName(),getLocation());

        }
    }

    @Override 
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
    ClassDefinition currentClass, Type returnType)
    throws ContextualError {
        LOG.debug("verify Int: start");
        if (returnType.isInt()){
            this.setType(returnType);
        }
        else{
            throw new ContextualError("Expected type int, got " + returnType.getName(),getLocation());

        }
        LOG.debug("verify Int: end");
    }
    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
