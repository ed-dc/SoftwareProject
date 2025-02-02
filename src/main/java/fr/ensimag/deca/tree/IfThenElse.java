package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CodeGenControl;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

import java.io.PrintStream;
import fr.ensimag.deca.codegen.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


/**
 * Full if/else if/else statement.
 *
 * @author gl14
 * @date 01/01/2025
 */
public class IfThenElse extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(IfThenElse.class);
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void setElseBranch(ListInst elseBranch){
        this.elseBranch = elseBranch;
    }

    public void add(AbstractInst inst){
        this.elseBranch.add(inst);
    }

    public void addElseIf(AbstractExpr condition, ListInst thenBranch) {
        ListInst tempElseBranch = new ListInst();
        IfThenElse newElseIf = new IfThenElse(condition, thenBranch, tempElseBranch);
        this.setElseBranch(new ListInst());
        this.elseBranch.add(newElseIf);
    }    

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError 
        {
            LOG.debug("verify IfThenElse: start");
            Type typeCondition = condition.verifyExpr(compiler, localEnv, currentClass);
            if (!typeCondition.isBoolean()) {
                throw new ContextualError("Condition must be a boolean", condition.getLocation());
            }
            thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
            elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
            LOG.debug("verify IfThenElse: end");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        CodeGenControl.wCodeGenIf(compiler, condition);
        for (AbstractInst inst : thenBranch.getList()) {
            inst.codeGenInst(compiler);
        }
        Label lab_sinon = CodeGenControl.wCodeGenEndIf(compiler,null, true);
        for (AbstractInst inst : elseBranch.getList()) {
            if (inst instanceof IfThenElse) {
                ((IfThenElse) inst).codeGenInst(compiler, lab_sinon);
            }
            else {
                inst.codeGenInst(compiler);
            }
        }
        CodeGenControl.wCodeGenFinalEndIf(compiler);
    }

    
    protected void codeGenInst(DecacCompiler compiler, Label lab_else) {

        CodeGenControl.wCodeGenIf(compiler, condition);
        for (AbstractInst inst : thenBranch.getList()) {
            inst.codeGenInst(compiler);
        }
        CodeGenControl.wCodeGenEndIf(compiler,lab_else, false);
        for (AbstractInst inst : elseBranch.getList()) {
            if (inst instanceof IfThenElse) {
                ((IfThenElse) inst).codeGenInst(compiler, lab_else);
            }
            else {
                inst.codeGenInst(compiler);
            }
        }
  
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.print(") { \n");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("} else { \n");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("} ");
    }


    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
