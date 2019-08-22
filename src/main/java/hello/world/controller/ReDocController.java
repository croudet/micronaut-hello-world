package hello.world.controller;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import hello.world.OpenApiConfig;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Serves the ReDoc view. See template in src/main/resources/views/redoc
 *
 * Uses handlebars template engine.
 *
 * @author croudet
 */
@Hidden
@Controller("/redoc")
public class ReDocController {

    @Inject
    OpenApiConfig config;

    @View("redoc/index")
    @Get
    public OpenApiConfig index() {
        LoggerFactory.getLogger(ReDocController.class).info("Trying to render redoc-view - {}", config);
        return config;
    }

}