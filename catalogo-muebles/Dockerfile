
FROM eclipse-temurin:21-jdk

# Crear un volumen temporal para archivos temporales de Tomcat
VOLUME /tmp

# Copiar el JAR generado por Maven al contenedor y renombrar a app.jar
COPY target/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]