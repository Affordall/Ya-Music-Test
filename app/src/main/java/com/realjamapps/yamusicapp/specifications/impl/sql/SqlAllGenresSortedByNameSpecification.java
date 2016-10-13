package com.realjamapps.yamusicapp.specifications.impl.sql;

import com.realjamapps.yamusicapp.database.sql.tables.TableGenres;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

public class SqlAllGenresSortedByNameSpecification implements ISqlSpecification {

    private static final String ORDER_BY_DESCEND = " DESC";
    private static final String ORDER_BY_ASCEND = " ASC";

    @Override
    public String toSqlQuery() {
        return "SELECT  * FROM "
                + TableGenres.TABLE_GENRES
                + " ORDER BY "+ TableGenres.KEY_GENRES_NAME
                + ORDER_BY_ASCEND
                + ";";
    }
}

