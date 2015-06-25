package org.transmartproject.rest
import grails.transaction.Transactional
import org.transmartproject.rest.misc.SQLUtils

import javax.sql.DataSource

@Transactional
class PlatformService {

    // -------------------- Private Variables --------------------

    def DataSource dataSource

    // -------------------- Public Methods --------------------

    def listPlatforms() {
        def platforms = []

        SQLUtils.sql(dataSource).eachRow("""
            SELECT
                gpl.platform,
                gpl.title,
                count(ma.probe_id) platform_size
            FROM
                DE_GPL_INFO gpl
                JOIN DE_MRNA_ANNOTATION ma ON gpl.platform = ma.gpl_id
            GROUP BY
                gpl.platform,
                gpl.title
        """.stripMargin(), { row ->
            platforms <<  [
                platform: row.platform,
                title: row.title,
                size: row.platform_size
            ]
        })

        return [ "platforms" : platforms ]
    }

    def listPlatformIds(def platform) {
        def sql = SQLUtils.sql(dataSource)
        platform = sql.firstRow("""
            SELECT
                gpl.platform
            FROM
                DE_GPL_INFO gpl
            WHERE
                gpl.platform = ?
        """.stripMargin(), [platform])?.platform

        Set<String> platformIds = new HashSet<String>()
        sql.eachRow("""
            SELECT
                mrna.probe_id
            FROM
                DE_MRNA_ANNOTATION mrna
            WHERE
                mrna.GPL_ID = ?
            """.stripMargin(), [platform], { row ->
                platformIds << row.probe_id
        })

        return [ "platform_ids" : platformIds.asList() ]
    }

}
