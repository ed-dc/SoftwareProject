package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.codegen.*;

/**
 * @author gl14
 * @date 01/01/2025
 */
public class DeclVar extends AbstractDeclVar {
    private static final Logger LOG = Logger.getLogger(DeclVar.class);


    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.debug("verifyDeclVar: start");
        Type t = type.verifyType(compiler);
        varName.setType(t);
        type.setType(t);
        if (localEnv.get(varName.getName()) != null) {
            throw new ContextualError("Variable " + varName.getName() + " is already defined", varName.getLocation());
        }
        if(initialization != null){
            initialization.verifyInitialization(compiler, t, localEnv, currentClass);
        }
        VariableDefinition var = new VariableDefinition(t, varName.getLocation());
        try {
            localEnv.declare(varName.getName(),(ExpDefinition) var);
        } catch (DoubleDefException e) {
            throw new ContextualError("Error while declaring variable " + varName.getName() + ": " + e.getMessage(), varName.getLocation());
        }
        varName.setDefinition(var);
        LOG.debug("verifyDeclVar: end");
    }

    
    @Override
    public void decompile(IndentPrintStream s) 
    {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }

    protected void codeGenDeclVar(DecacCompiler compiler){
        if (initialization instanceof Initialization){
            Initialization init = (Initialization) initialization;
            CodeGenCodeExp.GenCodeExp(compiler,new Assign(varName, init.getExpression()),2 );
        }
        else{
            CodeGenCodeExp.GenCodeExp(compiler,varName, 2 );
        }
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
