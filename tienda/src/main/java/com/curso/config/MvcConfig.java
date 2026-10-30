package com.curso.config;

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
        // 1. Limpiamos espacios y estandarizamos las barras a formato URL (/)
        String rutaLimpia = rutaAbsoluta.trim().replace("\\", "/");

        // 2. Si no termina en barra, se la agregamos para que Spring entienda que es un directorio
        if (!rutaLimpia.endsWith("/")) {
            rutaLimpia += "/";
        }

        // 3. Agregamos el prefijo 'file:' si no lo tiene.
        // En Docker (/app/tienda-imagenes/) quedará como: file:/app/tienda-imagenes/
        // En IntelliJ (../tienda-imagenes/) quedará como: file:../tienda-imagenes/
        if (!rutaLimpia.startsWith("file:")) {
            rutaLimpia = "file:" + rutaLimpia;
        }

        // El prefijo virtual de la URL coincide exactamente con tus HTML: /imagenes/
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(rutaLimpia);
    }
}
