package com.google.zetasql.toolkit.antipattern.parser.visitors.singlerowinsert;

import com.google.common.collect.ImmutableList;
import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class IdentifySingleRowInsertsVisitor extends ParseTreeVisitor {

    private final static String SINGLE_ROW_INSERTS = "SINGLE ROW INSERTS";
    private final static String OTHER_INSERT_PATTERN = "OTHER INSERT PATTERN";
    private ArrayList<String> result = new ArrayList<String>();

    public ArrayList<String> getResult() {
        return result;
    }


    @Override
    public void visit(ASTNodes.ASTInsertValuesRowList node) {
        ImmutableList<ASTNodes.ASTInsertValuesRow> nodes = node.getRows();
        if(nodes.size() == 1 ){
            result.add(String.format(SINGLE_ROW_INSERTS));
        }
    }

}
