package org.transmartproject.rest
import groovy.sql.Sql

import javax.sql.DataSource

/**
 * @author Scott Faria <scott.faria@genedata.com>
 */
class PlatformService {

    // -------------------- Private Variables --------------------

    def DataSource dataSource

    // -------------------- Public Methods --------------------

    def listPlatforms() {
        def platforms = []
        sql(dataSource).eachRow("SELECT gpl.title FROM DE_GPL_INFO gpl", { row ->
            platforms << row.title
        })

        return [
            "platforms" : platforms
        ]
    }

    // -------------------- Private Methods --------------------

    private static Sql sql(DataSource source) {
        Sql sql = new Sql(source)
        sql.withStatement { stmt -> stmt.setFetchSize(5000) }
        return sql
    }

}
