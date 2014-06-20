package org.codehaus.groovy.grails.web.servlet.mvc

import grails.artefact.Artefact
import grails.util.GrailsWebUtil
import grails.web.Action
import grails.core.DefaultGrailsApplication
import org.codehaus.groovy.grails.web.mapping.AbstractUrlMappingsSpec
import org.grails.web.mapping.mvc.GrailsControllerUrlMappings
import org.grails.web.mapping.mvc.UrlMappingsHandlerMapping
import org.grails.web.mapping.mvc.UrlMappingsInfoHandlerAdapter
import org.springframework.web.context.request.RequestContextHolder

/**
 * Created by graemerocher on 26/05/14.
 */
class UrlMappingsHandlerMappingSpec extends AbstractUrlMappingsSpec{

    void "Test that a matched URL returns a URLMappingInfo"() {

        given:
            def grailsApplication = new DefaultGrailsApplication(FooController)
            grailsApplication.initialise()
            def holder = getUrlMappingsHolder {
                "/foo/bar"(controller:"foo", action:"bar")
            }

            holder = new GrailsControllerUrlMappings(grailsApplication, holder)
            def handler = new UrlMappingsHandlerMapping(holder)

        when:"A URI is matched"

            def webRequest = GrailsWebUtil.bindMockWebRequest()
            def request = webRequest.request
            request.setRequestURI("/foo/bar")
            def handlerChain = handler.getHandler(request)

        then:"A handlerChain is created"
            handlerChain != null

        when:"A HandlerAdapter is used"
            def handlerAdapter = new UrlMappingsInfoHandlerAdapter()
            def result = handlerAdapter.handle(request, webRequest.response, handlerChain.handler)

        then:"The model and view is correct"
            result.viewName == 'bar'
            result.model == [foo:'bar']

    }

    void cleanup() {
        RequestContextHolder.setRequestAttributes(null)
    }
}

@Artefact('Controller')
class FooController {
    @Action
    def bar() {
        [foo:"bar"]
    }
}
