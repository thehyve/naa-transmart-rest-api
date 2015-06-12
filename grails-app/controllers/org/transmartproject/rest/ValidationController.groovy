package org.transmartproject.rest

class ValidationController {

    // -------------------- Private Variables --------------------

    def validationService

    // -------------------- Public Methods --------------------

    /**
     * GET request on /validation/study/{studyId}/platform/{platform}/tissue_type/{tissueType}
     *  This will return a Boolean indicating if the given tissue type is valid for the study
     *  and platform.
     */
    def indexByTissueType() {
        def studyId = params.studyId
        def platform = params.platform
        def tissueType = params.tissueType
        render validationService.validateTissueType(studyId, platform, tissueType)
    }

}