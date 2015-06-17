package org.transmartproject.rest.misc

import groovy.sql.Sql

import javax.sql.DataSource

class SQLUtils {

    public static Sql sql(DataSource source) {
        Sql sql = new Sql(source)
        sql.withStatement { stmt -> stmt.setFetchSize(5000) }
        return sql
    }
}
