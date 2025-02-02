package fr.ensimag.deca.tree.trigo;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.TreeFunction;
import fr.ensimag.deca.tree.ConvFloat;

public abstract class AbstractTrigo extends AbstractExpr {
    protected AbstractExpr arguments;
    protected Type value;
    private static final Logger LOG = Logger.getLogger(AbstractTrigo.class);

    public AbstractTrigo(AbstractExpr arguments) {
        this.arguments = arguments;
        this.value = null;
    }

    public AbstractExpr getArguments() {
        return arguments;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getFunctionName() + "(");
        arguments.decompile(s);
        s.print(")");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify " + getFunctionName() + ": start");
        Type type = this.getArguments().verifyExpr(compiler, localEnv, currentClass);
        if (!type.isFloat() && !type.isInt()) {
            throw new ContextualError(getFunctionName() + " function argument must be a float", this.getLocation());
        }
        if (type.isFloat()) {
            this.setType(type);
        } 
        else 
        {
            ConvFloat conv = new ConvFloat(this.getArguments());
            this.setArguments(conv);
            this.setType(compiler.environmentType.FLOAT);
        }
        LOG.debug("verify " + getFunctionName() + ": end");
        return type;
    }

    public void setArguments(AbstractExpr arguments) {
        this.arguments = arguments;
    }

    @Override
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        this.verifyExpr(compiler, localEnv, currentClass);
        this.setValue(this.calculateValue());
        return this;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.verifyExpr(compiler, localEnv, currentClass);
    }

    protected abstract String getFunctionName();

    public Type getValue() {
        return value;
    }

    public void setValue(Type value) {
        this.value = value;
    }

    public abstract Type calculateValue();

    @Override
    protected  void prettyPrintChildren(PrintStream s, String prefix){
        arguments.prettyPrint(s, prefix, true);
    }

    @Override
    protected  void iterChildren(TreeFunction f){
        arguments.iter(f);
    }
}
