package org.transmartproject.rest
import grails.transaction.Transactional
import groovy.sql.Sql

import javax.sql.DataSource
import java.util.regex.Matcher

@Transactional
class ValidationService {

    // -------------------- Private Variables --------------------

    def DataSource dataSource
    def studiesResourceService

    // -------------------- Public Methods --------------------

    def validateTissueType(studyId, platform, tissueType) {
        def study = studiesResourceService.getStudyById(studyId as String)
        def studyName = study.ontologyTerm.fullName as String
        def query = "SELECT count(*) as count FROM I2B2 i2b2 WHERE i2b2.c_visualattributes = 'LAH' and upper(c_fullname) LIKE ?"

        def queryParam = "${studyName?.toUpperCase()}%${platform?.toUpperCase()}\\${tissueType?.toUpperCase()}%" as String
        def row = sql(dataSource).firstRow(query, [queryParam])
        return row.count == 0
    }

    def validateSubjects(studyId, subjects) {
        def sql = sql(dataSource)
        String trial = GexHelperUtils.getTrialShortName(study, sql) + "%"

        Set<String> subjectsHash = new HashSet<String>()
        subjects.each { subjectsHash.add(it) }

        sql.eachRow("select pd.sourcesystem_cd from patient_dimension pd where pd.sourcesystem_cd like ?", [trial], { row ->
            String source = row.sourcesystem_cd
            Matcher match = SUBJECT.matcher(source)
            boolean found = match.find()
            if (found) {
                String subjectId = match.group(1)
                subjectsHash.remove(subjectId)
            }
        })

        return subjectsHash
    }

    // -------------------- Private Methods --------------------

    private static Sql sql(DataSource source) {
        Sql sql = new Sql(source)
        sql.withStatement { stmt -> stmt.setFetchSize(5000) }
        return sql
    }
}
