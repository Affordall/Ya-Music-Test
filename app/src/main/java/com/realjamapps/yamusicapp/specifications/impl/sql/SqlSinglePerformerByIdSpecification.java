package com.realjamapps.yamusicapp.specifications.impl.sql;

import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

public class SqlSinglePerformerByIdSpecification implements ISqlSpecification {

    public SqlSinglePerformerByIdSpecification(int wordsForSearch) {
        this.mSearch = wordsForSearch;
    }

    private int mSearch;

    @Override
    public String toSqlQuery() {
        return "SELECT  * FROM "
                + TablePerformers.TABLE_PERFORMERS
                + " WHERE " + TablePerformers.KEY_ID
                + "=?" + mSearch
                //+ "=" + "'" + mSearch + "'"
                + ";";
    }

}
