package com.realjamapps.yamusicapp.specifications.impl.sql;

import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

public class SqlAllPerformersSortedByNameSpecification implements ISqlSpecification {

    private static final String ORDER_BY_DESCEND = " DESC";
    private static final String ORDER_BY_ASCEND = " ASC";

    @Override
    public String toSqlQuery() {
        return "SELECT  * FROM "
                + TablePerformers.TABLE_PERFORMERS
                + " ORDER BY "+ TablePerformers.KEY_PERFORMER_NAME
                + ORDER_BY_ASCEND
                + ";";
    }
}

