package org.transmartproject.rest
import grails.transaction.Transactional
import org.transmartproject.core.ontology.OntologyTerm

import javax.sql.DataSource
import java.sql.Connection

import static org.transmartproject.rest.misc.SQLUtils.sql

@Transactional
class OntologyHelperService {

    // -------------------- Private Static Variables --------------------

    private static def batchSize = 1000;

    // -------------------- Private Variables --------------------

    def DataSource dataSource

    // -------------------- Public Methods --------------------

    def getLeafTypes(List<OntologyTerm> concepts) {
        def query = """\
SELECT
    ib.c_fullname,
    CASE
        WHEN ib.c_visualattributes = 'LA'
            THEN CASE WHEN EXISTS (
                SELECT
                  *
                FROM
                    OBSERVATION_FACT ofa
                    JOIN CONCEPT_DIMENSION cd on (cd.concept_cd = ofa.concept_cd)
                WHERE
                  cd.concept_path = ib.c_fullname
                  AND ofa.valtype_cd = 'N'
            ) THEN 'NUMERIC' ELSE 'TEXT' END
          ELSE NULL
        END type
FROM
  I2B2 ib
WHERE
  ib.c_fullname IN (%s)
  AND ib.c_visualattributes = 'LA'
"""
        def extendedAttrs = [:]
        sql(dataSource).withTransaction { Connection conn ->
            def batchCount = 0
            while (batchCount < concepts.size()) {
                def currentBatchSize = Math.min(batchSize, concepts.size() - batchCount)
                def params = (0..<currentBatchSize).collect { it -> return "?" }.join(", ")
                def pstmt = conn.prepareStatement(String.format(query, params))
                def rset = null
                try {
                    def endBatch = batchCount + currentBatchSize
                    def paramIndex = 1;
                    for (; batchCount < endBatch; batchCount++) {
                        pstmt.setString(paramIndex++, concepts.get(batchCount).getFullName())
                    }
                    rset = pstmt.executeQuery()
                    while (rset.next()) {
                        extendedAttrs[rset.getString("c_fullname")] = rset.getString("type")
                    }
                } finally {
                    pstmt?.close()
                    rset?.close()
                }
            }
        }
        return extendedAttrs
    }
}
