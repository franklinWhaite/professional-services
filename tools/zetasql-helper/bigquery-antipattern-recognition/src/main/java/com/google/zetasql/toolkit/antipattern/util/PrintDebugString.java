package com.google.zetasql.toolkit.antipattern.util;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.IdentifyCrossJoin;
import com.google.zetasql.toolkit.antipattern.parser.IdentifyInSubqueryWithoutAgg;
import com.google.zetasql.toolkit.antipattern.parser.IdentifySelectStar;

public class PrintDebugString {

  public static void main(String[] args) {
    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    String query = "-- Translation time: 2023-04-07T20:34:33.719200Z\n"
        + "-- Translation job ID: 33d147db-f6a5-4689-9f11-3ae14e7496d5\n"
        + "-- Source: pso-dev-whaite-etl/1680899652250093327-igbxxftxl1jqx/preprocessed/wf_ONE_BookingsEnduserParent--2011839656.sql\n"
        + "-- Translated from: Oracle\n"
        + "-- Translated to: BigQuery\n"
        + "\n"
        + "SELECT\n"
        + "    /*+ PARALLEL(OBAW.WC_BW_BOOK_F,4) */ WC_BW_BOOK_F.ROW_WID,\n"
        + "    WC_BW_BOOK_F.BOOKINGS_DATE_WID,\n"
        + "    WC_BW_BOOK_F.PRG_TYPE_ADJ_UNBUNDLED AS PRG_TYPE_ADJ_UNBUNDLED,\n"
        + "    WC_PRODUCT_DH.PRODUCT_SKU_FAMILY AS PRODUCT_SKU_FAMILY,\n"
        + "    WC_PRODUCT_DH.PRODUCT_TYPE AS PRODUCT_TYPE,\n"
        + "    PER_NAME_ENT_QTR AS BOOKINGS_QTR,\n"
        + "    WC_BW_BOOK_F.SEGMENT_ACTUAL,\n"
        + "    ltrim(rtrim(upper(WC_BW_BOOK_F.X_MDM_GLBL_PARENT_NAME))),\n"
        + "    WC_PRODUCT_DH.PRODUCT_SKU AS PRODUCT_SKU,\n"
        + "    WC_BW_BOOK_F.ORDER_NUMBER\n"
        + "  FROM\n"
        + "    bq_project.library.WC_BW_BOOK_F\n"
        + "    CROSS JOIN bq_project.library.WC_GEO_D\n"
        + "    CROSS JOIN bq_project.library.W_DAY_D\n"
        + "    CROSS JOIN bq_project.library.WC_PRODUCT_DH\n"
        + "  WHERE W_DAY_D.ROW_WID = WC_BW_BOOK_F.BOOKINGS_DATE_WID\n"
        + "   AND WC_BW_BOOK_F.GEO_WID = WC_GEO_D.ROW_WID\n"
        + "   AND WC_BW_BOOK_F.PRODUCT_WID = WC_PRODUCT_DH.ROW_WID\n"
        + "   AND BOOKINGS_FLAG = 'Y'\n"
        + "   AND trim(upper(WC_PRODUCT_DH.SEGMENT)) = 'ENTERPRISE'\n"
        + "   AND ltrim(rtrim(upper(WC_BW_BOOK_F.X_MDM_GLBL_PARENT_NAME))) = 'MITSUBISHI CORPORATION'\n"
        + ";";


    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    System.out.println(parsedQuery);
    // System.out.println(new IdentifySelectStar().run(parsedQuery));
    // System.out.println(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    System.out.println(new IdentifyCrossJoin().run(parsedQuery));

  }
}
