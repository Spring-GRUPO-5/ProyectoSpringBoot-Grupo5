# S17-1 Demo 1: Dockerfile de la API "fallos" (versión correcta)
#
# Se construye desde la carpeta del proyecto, la que contiene este fichero y el pom.xml.
# El punto final del comando es esa carpeta (el contexto de construcción):
#   docker build -t fallos:1.0 .
#
# Antes hay que generar el JAR, porque la imagen solo contiene Java y el JAR:
#   mvn clean package

# FROM: imagen base con Java 21 (solo el JRE, sin Maven ni el código fuente).
FROM eclipse-temurin:21-jre

# COPY: copia el JAR de la carpeta target del proyecto a la imagen, con el nombre app.jar.
# El nombre de origen es artifactId-version.jar tal como lo genera Maven.
COPY target/tienda-0.0.1-SNAPSHOT.jar app.jar

# EXPOSE: solo anota que la aplicación escucha en el 8080 dentro del contenedor.
# No publica nada: la puerta la abre -p en docker run.
EXPOSE 8080

# ENTRYPOINT: el comando que se ejecuta al arrancar el contenedor.
# La forma de lista hace que Java sea el proceso principal y reciba docker stop.
ENTRYPOINT ["java", "-jar", "app.jar"]
