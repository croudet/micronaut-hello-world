package hello.world.controller;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import hello.world.OpenApiConfig;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Serves the RapiDoc view. See template in src/main/resources/views/rapidoc
 *
 * Uses handlebars template engine.
 *
 * @author croudet
 */
@Hidden
@Controller("/rapidoc")
public class RapiDocController {

    @Inject
    OpenApiConfig config;

    @View("rapidoc/index")
    @Get
    public OpenApiConfig index() {
        LoggerFactory.getLogger(RapiDocController.class).info("Trying to render rapidoc-view - {}", config);
        return config;
    }

}