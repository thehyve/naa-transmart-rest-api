/*
 * Copyright 2014 Janssen Research & Development, LLC.
 *
 * This file is part of REST API: transMART's plugin exposing tranSMART's
 * data via an HTTP-accessible RESTful API.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version, along with the following terms:
 *
 *   1. You may convey a work based on this program in accordance with
 *      section 5, provided that you retain the above notices.
 *   2. You may convey verbatim copies of this program code as you receive
 *      it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.transmartproject.rest.marshallers

import org.transmartproject.core.ontology.OntologyTerm

/**
 * Wraps an OntologyTerm for serialization.
 * Marshallers/Serializers have a static registry where the class is the key, and transSMART already
 * has its own serializer for OntologyTerm, so we use this class to wrap the OntologyTerm and pick the right Serializer
 * for REST API
 */
class OntologyTermWrapper {

    OntologyTerm delegate

    private final Map<String, Object> extendedAttrs

    OntologyTermWrapper(OntologyTerm term, Map<String, Object> extendedAttrs) {
        this.extendedAttrs = extendedAttrs
        this.delegate = term
    }

    OntologyTermWrapper(OntologyTerm term) {
        this(term, [:])
    }

    static List<OntologyTermWrapper> wrap(List<OntologyTerm> source, Map<String, Map<String, Object>> extendedAttrs) {
        source.collect {
            def extendedAttrSet = extendedAttrs[it.getFullName()]
            if (extendedAttrSet == null) {
                extendedAttrSet = [:]
            }
            new OntologyTermWrapper(it, extendedAttrSet)
        }
    }

    // Return TEXT or NUMERIC if it's a leaf, null otherwise
    String getLeafType() {
        return extendedAttrs?.getAt('leafType') as String
    }

    boolean isHighDim() {
        OntologyTerm.VisualAttributes.HIGH_DIMENSIONAL in this.delegate.visualAttributes
    }
}
