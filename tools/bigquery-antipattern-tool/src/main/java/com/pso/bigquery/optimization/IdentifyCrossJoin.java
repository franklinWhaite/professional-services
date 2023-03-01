/*
 * Copyright 2023 Google LLC All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pso.bigquery.optimization;

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoin;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoinVisitor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IdentifyCrossJoin implements BasePatternDetector {

  private final String CROSS_JOIN_SUGGESTION_MESSAGE =
      "CROSS JOIN between tables: %s and %s. Try to change for a INNER JOIN if possible.";
  ArrayList<String> result = new ArrayList<String>();

  public String run(
      String query,
      String billingProjectId,
      SimpleCatalog catalog,
      QueryAnalyzer.CatalogScope catalogScope) {
    CrossJoinVisitor visitor = new CrossJoinVisitor(billingProjectId, catalog);
    parser.visitQuery(billingProjectId, query, catalog, catalogScope, visitor);

    List<CrossJoin> crossJoinList = visitor.getResult();
    int numCrossJoinsFound = crossJoinList.size();
    crossJoinList.forEach(crossJoin -> parseCrossJoin(crossJoin, numCrossJoinsFound));
    return StringUtils.join(result, "\n");
  }

  public void parseCrossJoin(CrossJoin crossJoin, int numCrossJoinsFound) {
    result.add(
        String.format(
            CROSS_JOIN_SUGGESTION_MESSAGE, crossJoin.getTableNames().toArray(new String[0])));
  }
}
