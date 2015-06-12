package org.transmartproject.rest
import grails.transaction.Transactional
import groovy.sql.Sql

import javax.sql.DataSource
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional
class ValidationService {
    // -------------------- Private Variables --------------------

    private final static Pattern SUBJECT = ~/:([^:]+)\$"/

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
        def study = studiesResourceService.getStudyById(studyId as String)
        String trial = getTrialShortName(study.ontologyTerm.fullName, sql) + "%"

        Set<String> subjectsHash = new HashSet<String>()
        subjects.each { subjectsHash.add(it as String) }

        sql.eachRow("select pd.sourcesystem_cd from patient_dimension pd where pd.sourcesystem_cd like ?", [trial], { row ->
            String source = row.sourcesystem_cd
            Matcher match = SUBJECT.matcher(source)
            boolean found = match.find()
            if (found) {
                String subjectId = match.group(1)
                subjectsHash.remove(subjectId)
            }
        })

        return !subjectsHash.isEmpty()
    }

    def validateSamples(studyId, sampleIds) {
        def sql = sql(dataSource)
        def sampleHash = new HashSet<String>()
        sampleIds.each { sampleHash.add(it as String) }

        def study = studiesResourceService.getStudyById(studyId as String)
        def trial = getTrialShortName(study, sql)

        def existing = new HashSet<String>()
        sql.eachRow("select ssm.sample_cd from de_subject_sample_mapping ssm where ssm.trial_name = ?", [trial]) { row ->
            def id = row.sample_cd
            if (sampleHash.contains(id)) {
                existing << id
            }
        }
        return !existing.isEmpty()
    }

    def validatePlatformIds(platform, platformIds) {
        def sql = sql(dataSource)
        def query = "select gpl.platform from de_gpl_info gpl where gpl.title = ?"
        def gpl = sql.firstRow(query, [platform]).platform

        query = "select mrna.probe_id from de_mrna_annotation mrna where mrna.GPL_ID = ?"

        Set<String> platformIdsHash = new HashSet<String>()
        platformIds.each { platformIdsHash.add(it as String) }

        sql.eachRow(query,[gpl], { row ->
            platformIdsHash.remove(row.probe_id)
        })

        return !platformIdsHash.isEmpty()
    }

    // -------------------- Private Methods --------------------

    private static String getTrialShortName(studyPath, sql) {
        String trialQuery = "select c.sourcesystem_cd from i2b2 c where c.c_hlevel = 1 and c.c_fullname = ?"
        return sql.firstRow(trialQuery, [studyPath]).sourcesystem_cd
    }

    private static Sql sql(DataSource source) {
        Sql sql = new Sql(source)
        sql.withStatement { stmt -> stmt.setFetchSize(5000) }
        return sql
    }
}
