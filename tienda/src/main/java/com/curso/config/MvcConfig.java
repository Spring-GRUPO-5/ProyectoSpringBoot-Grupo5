package com.curso.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${tienda.imagenes.ruta-absoluta}")
    private String rutaAbsoluta;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String carpetaDestino = new File(rutaAbsoluta).getAbsolutePath();

        // El prefijo virtual de la URL es /imagenes/
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + carpetaDestino + File.separator);
    }
}