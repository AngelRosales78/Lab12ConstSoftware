# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

## Laboratorio 12 del equipo

Repositorio compartido: https://github.com/AngelRosales78/Lab12ConstSoftware.
Base: https://github.com/jgomezz/petclinic_integration_test, conservando su historial y autoria.

El equipo comparte el grupo 5 de TASKS_INTEGRATION.md. Los cuatro ejercicios del Word no se presentan como completados por este aporte.

| Integrante | Bloques | Aporte |
|---|---|---|
| Ángel Rosales, AngelRosales78 | A y B: asignaciones y consultas | Controlador, soporte comun y seis pruebas H2 mas seis Mockito |
| Diego Magallanes, DiegoMagallanes11 | C y D: principal, experiencia y certificacion | Pendiente de implementar, probar y subir desde su cuenta |

## Ejecutar

Usar JDK 21. Abrir esta carpeta como proyecto Maven en IntelliJ y cargar las dependencias.

```powershell
./mvnw.cmd test "-Dtest=VetSpecialtyControllerTest,VetSpecialtyControllerMockitoTest" -Dspring.profiles.active=h2
./mvnw.cmd clean test -Dspring.profiles.active=h2
```

En PowerShell, si Maven interpreta incorrectamente una propiedad, escribirla entre comillas: `"-Dspring.profiles.active=h2"`. H2 funciona en memoria; no requiere MySQL. Para guardar el log de la aplicacion en la carpeta del proyecto: `$env:LOGGING_FILE_NAME='logs/application.log'`.

## Parte de Ángel

VetSpecialtyController implementa POST /vet-specialties, DELETE /vet-specialties/{vetId}/{specId}, GET /vets/{vetId}/specialties y GET /specialties/{specId}/vets. La creacion devuelve 201; las consultas y eliminacion, 200; una asignacion duplicada, 409. La respuesta de asignacion usa VetSpecialtyDTO con IDs planos.

Se reutiliza el soporte del servicio de Ángel del laboratorio 11, adaptado a las entidades ya existentes del proyecto de integracion. Se conservan las asociaciones originales de Vet y Specialty y se omiten al serializar para evitar ciclos JSON. Se conservan schema.sql, data.sql y las pruebas originales. La clave de vet_specialties sigue formada por vet_id y specialty_id.

VetSpecialtyControllerTest utiliza MockMvc, el stack real y datos semilla de H2. Los cambios se revierten al finalizar cada prueba; flush/clear y consultas SQL comprueban persistencia. VetSpecialtyControllerMockitoTest usa MockitoBean del servicio y el helper VetSpecialtyObjectCreator. Las solicitudes no ejecutan el servicio ni escriben en la base, aunque Spring inicializa H2 al cargar el contexto.

## Trabajo de Diego

Seguir [la guia de contribucion de Diego](docs/TRABAJO_DIEGO.md). Su aporte es implementar los endpoints C+D y agregar sus doce pruebas (seis reales y seis Mockito), con su propia rama, commits y PR. Los metodos del servicio ya existen como soporte reutilizado; no cuentan como contribucion nueva de Diego.

No se hacen commits en nombre del compañero. Cada uno revisa el PR del otro antes de integrar. La base main conserva el proyecto original; los cambios del laboratorio se proponen mediante PR.

## Aportes y conflictos

Ángel: preparacion del repositorio, soporte comun del grupo 5, endpoints A+B y doce pruebas web. Resultados reales en [RESULTADOS_ANGEL.md](docs/RESULTADOS_ANGEL.md).

Diego: pendiente de su implementacion y sus commits reales. Nadie ha firmado cambios con su identidad.

Conflictos resueltos: ninguno hasta el momento. No se inventan conflictos ni merges. Se debe documentar el conflicto que ocurra realmente, indicando archivos, resolucion y commit.

Pendientes de entrega: aporte C+D de Diego, revisiones y merges de ambos integrantes, evidencia real del conflicto, capturas del historial y de la suite completa, informe y tres conclusiones del laboratorio.
