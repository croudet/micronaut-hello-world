package hello.world.controller;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import hello.world.OpenApiConfig;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Serves the Swagger view. See template in src/main/resources/views/swagger
 *
 * Uses handlebars template engine.
 *
 * @author croudet
 */
@Hidden
@Controller("/swagger-ui")
public class SwaggerController {

    @Inject
    OpenApiConfig config;

    @View("swagger/index")
    @Get
    public OpenApiConfig index() {
        LoggerFactory.getLogger(SwaggerController.class).info("Trying to render swagger-view - {}", config);
        return config;
    }

}