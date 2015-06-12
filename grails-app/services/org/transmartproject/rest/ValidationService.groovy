package org.transmartproject.rest
import grails.transaction.Transactional
import groovy.sql.Sql

import javax.sql.DataSource

@Transactional
class ValidationService {

    // -------------------- Private Variables --------------------

    def DataSource dataSource
    def studiesResourceService

    // -------------------- Public Methods --------------------

    def validateTissueType(studyId, platform, tissueType) {
        def study = studiesResourceService.getStudyById(studyId as String)
        def studyName = study.ontologyTerm.fullName
        def query = "SELECT count(*) as count FROM I2B2 i2b2 WHERE i2b2.c_visualattributes = 'LAH' and upper(c_fullname) LIKE ?"
        def row = sql(dataSource).firstRow(query, ["${studyName?.toUpperCase}%${platform?.toUpperCase}\\${tissueType?.toUpperCase}%"])
        return row.count == 0
    }

    // -------------------- Private Methods --------------------

    private static Sql sql(DataSource source) {
        Sql sql = new Sql(source)
        sql.withStatement { stmt -> stmt.setFetchSize(5000) }
        return sql
    }
}
