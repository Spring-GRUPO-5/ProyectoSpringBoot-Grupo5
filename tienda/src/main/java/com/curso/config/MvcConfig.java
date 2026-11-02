package com.curso.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${tienda.imagenes.ruta-absoluta}")
    private String rutaAbsoluta;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Limpiamos espacios laterales
        String rutaOriginal = rutaAbsoluta.trim();

        // 2. Quitamos el prefijo 'file:' si algún desarrollador lo puso en sus propiedades
        if (rutaOriginal.startsWith("file:")) {
            rutaOriginal = rutaOriginal.substring(5);
        }

        // 3. ¡EL SECRETO PARA MAC Y WINDOWS!: Convertimos la ruta relativa (ej: ../tienda-imagenes)
        // a una ruta absoluta del sistema operativo en tiempo real.
        Path caminoFisico = Paths.get(rutaOriginal).toAbsolutePath().normalize();

        // 4. Convertimos a formato URL estandarizado agregando las barras correctas para file:
        // En Windows devolverá algo como: file:/C:/usuarios/.../tienda-imagenes/
        // En macOS devolverá algo como: file:///Users/.../tienda-imagenes/
        String rutaFormateada = caminoFisico.toUri().toString();

        // El prefijo virtual de la URL coincide exactamente con tus archivos HTML: /imagenes/
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations(rutaFormateada);
    }
}

