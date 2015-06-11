package org.transmartproject.rest
/**
 * @author Scott Faria <scott.faria@genedata.com>
 */
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
}
