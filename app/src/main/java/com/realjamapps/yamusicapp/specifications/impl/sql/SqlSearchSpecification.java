package com.realjamapps.yamusicapp.specifications.impl.sql;

import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

public class SqlSearchSpecification implements ISqlSpecification {

    private String[] mSearch;

    public SqlSearchSpecification(String[] wordsForSearch) {
            this.mSearch = wordsForSearch;
    }

    @Override
    public String toSqlQuery() {
        return getFinalQuery();
    }

    private String getSelectQuery() {
        return "SELECT  * FROM "
                + TablePerformers.TABLE_PERFORMERS
                + " WHERE " + TablePerformers.KEY_PERFORMER_GENRES
                + " LIKE " + "(cast(" + "%cns" +" as text))";
    }

    private String getFinalQuery() {
        String selectQuery = getSelectQuery();
        StringBuilder cns = new StringBuilder();
        cns.append("'%$");
        for(int i = 0; i < mSearch.length; i++) {
            cns.append(String.valueOf(mSearch[i]));
            if (i < mSearch.length - 1) {
                cns.append("%','%$");
            }
        }
        cns.append("%'");
        String finalQuery = null;
        try {
            finalQuery = selectQuery.replaceAll("%cns", cns.toString());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return finalQuery;
    }
}
