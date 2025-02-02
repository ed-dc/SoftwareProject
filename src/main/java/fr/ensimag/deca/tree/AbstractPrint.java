package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl14
 * @date 01/01/2025
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(
        DecacCompiler compiler, 
        EnvironmentExp localEnv, 
        ClassDefinition currentClass, 
        Type returnType)
        throws ContextualError 
        {
            for (AbstractExpr a : getArguments().getList()) {
                Type t = a.verifyExpr(compiler, localEnv, currentClass);
                if (t != null){
                    if (
                        !t.isInt() && !t.isFloat() && 
                        !t.isString() && !t.isBoolean()
                        ) 
                    {
                        throw new ContextualError("print/println cannot be used with this argument", a.getLocation());
                    }
                    else
                    {
                        a.setType(t);
                    }
                }
            }
        }

    @Override
    protected void codeGenInst(DecacCompiler compiler) 
    {
        for (AbstractExpr a : getArguments().getList()) 
        {
            a.codeGenPrint(compiler, this.printHex);
        }
    }

    private boolean getPrintHex() 
    {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        boolean hex = getPrintHex();
        s.print("print" + getSuffix());
        if (hex) {s.print("x");}
        s.print("(");
        for (AbstractExpr a : getArguments().getList()){
            a.decompile(s);
        }
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
