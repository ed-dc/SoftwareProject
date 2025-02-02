package fr.ensimag.deca.tree.trigo;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tree.AbstractExpr;

public class Tan extends AbstractTrigo {
    public Tan(AbstractExpr arguments) {
        super(arguments);
    }

    @Override
    public Type calculateValue() {
        //not yet implemented
        return null;
    }


    @Override
    protected String getFunctionName() {
        return "tan";
    }

}
