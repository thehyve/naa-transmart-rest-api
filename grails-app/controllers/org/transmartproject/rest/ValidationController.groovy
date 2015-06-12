package org.transmartproject.rest

class ValidationController {

    // -------------------- Statics --------------------

    @SuppressWarnings("GroovyUnusedDeclaration")
    static responseFormats = ['json', 'hal']

    // -------------------- Private Variables --------------------

    def validationService

    // -------------------- Public Methods --------------------

    /**
     * GET request on /validation/studies/{studyId}/platforms/{platform}/tissue_type/{tissueType}
     *
     *  This will return a Boolean indicating if the given tissue type is valid for the study
     *  and platform.
     */
    def indexByTissueType() {
        def studyId = params.studyId
        def platform = params.platform
        def tissueType = params.tissueType
        def returnValue = [
                'valid': validationService.validateTissueType(studyId, platform, tissueType)
        ]
        respond returnValue
    }

    /**
     * GET request on /validation/studies/{studyId}/subjects
     *
     *  This will return a Boolean indicating if the given subjects are valid for the study
     *  and platform.
     */
    def indexBySubjects() {
        def studyId = params.studyId
        def subjectIds = params.getList('subjects').collect { it as String }
        def returnValue = [
                'valid': validationService.validateSubjects(studyId, subjectIds)
        ]
        respond returnValue
    }

    /**
     * GET request on /validation/studies/{studyId}/samples
     *
     *  This will return a Boolean indicating if the given list of samples is valid for the study
     *  and platform.
     */
    def indexBySamples() {
        def studyId = params.studyId
        def sampleIds = params.getList('samples').collect { it as String }
        def returnValue = [
                'valid': validationService.validateSamples(studyId, sampleIds)
        ]
        respond returnValue
    }

    /**
     * GET request on /validation/platform/{platform}/platform_ids
     *
     *  This will return a Boolean indicating if the given list of samples is valid for the study
     *  and platform.
     */
    def indexByPlatformIds() {
        def platform = params.platform
        def platformIds = params.getList('platform_ids').collect { it as String }
        def returnValue = [
                'valid': validationService.validatePlatformIds(platform, platformIds)
        ]
        respond returnValue
    }

}
