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
        SQLUtils.sql(dataSource).eachRow("SELECT gpl.title FROM DE_GPL_INFO gpl", { row ->
            platforms << row.title
        })

        return [
            "platforms" : platforms
        ]
    }

}
