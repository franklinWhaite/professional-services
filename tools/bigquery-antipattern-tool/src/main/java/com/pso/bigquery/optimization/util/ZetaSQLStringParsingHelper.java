package com.pso.bigquery.optimization.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZetaSQLStringParsingHelper {

    private static String GET_TABLE_FROM_EXPR_STRING = ".*column=([a-z0-9-]*\\.[\\w]+\\.[\\w\\-]+)";

    public static String getTableNameFromExpr(String exprString) {
        String regex = GET_TABLE_FROM_EXPR_STRING;
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(exprString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }



}
