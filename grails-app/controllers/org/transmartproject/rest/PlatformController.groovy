package org.transmartproject.rest

class PlatformController {

    // -------------------- Statics --------------------

    @SuppressWarnings("GroovyUnusedDeclaration")
    static responseFormats = ['json', 'hal']

    // -------------------- Private Variables --------------------

    def platformService;

    // -------------------- Public Methods --------------------

    /**
     * GET request on /platforms/
     * <p>
     *  This will return a list of all available data platforms that are currently
     *  defined in the database.
     */
    def index() {
        respond platformService.listPlatforms()
    }

    /**
     * GET request on /platforms/{$platform}/platform_ids
     * <p>
     *  This will return a list of all available data platforms that are currently
     *  defined in the database.
     */
    def indexWithIds(String platform) {
        respond platformService.listPlatformIds(platform)
    }
}
