package fr.ensimag.deca.tree.trigo;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tree.AbstractExpr;

public class Cos extends AbstractTrigo {
    public Cos(AbstractExpr arguments) {
        super(arguments);
    }

    

    @Override
    public Type calculateValue() {
        //not yet implemented
        return null;
    }

    @Override
    protected String getFunctionName() {
        return "cos";
    }

}
