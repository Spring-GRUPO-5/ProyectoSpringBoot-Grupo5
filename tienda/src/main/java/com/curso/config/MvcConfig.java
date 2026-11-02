
package com.curso.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${tienda.imagenes.ruta-absoluta}")
    private String rutaAbsoluta;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path carpetaImagenes = Paths.get(rutaAbsoluta)
                .toAbsolutePath()
                .normalize();

        try {
            Files.walk(carpetaImagenes, 2)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ubicacion = carpetaImagenes.toUri().toString();

        System.out.println("CARPETA: " + ubicacion);

        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(ubicacion);
    }


}
