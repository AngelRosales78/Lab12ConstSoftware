# Validacion del aporte de Ángel

Fecha: 3 de noviembre de 2026. Entorno local: JDK 21.0.12, Maven Wrapper 3.9.11, Spring Boot 3.5.6 y H2.

Comando de pruebas propias:

```powershell
./mvnw.cmd test "-Dtest=VetSpecialtyControllerTest,VetSpecialtyControllerMockitoTest" "-Dspring.profiles.active=h2"
```

Resultado real: BUILD SUCCESS, doce pruebas, cero fallos, cero errores y cero omitidas. Finalizo a las 14:41:22, hora de Lima, con duracion Maven de 29.477 segundos.

Comando de validacion completa:

```powershell
./mvnw.cmd clean test "-Dspring.profiles.active=h2"
```

Resultado real: BUILD SUCCESS, 39 pruebas, cero fallos, cero errores y cero omitidas. Finalizo a las 14:43:09, hora de Lima, con duracion Maven de 35.646 segundos. JAVA_HOME apunta al JDK 21 indicado y LOGGING_FILE_NAME se configuro como logs/application.log. No se modificaron los comandos de prueba ni se omitieron clases para obtener este resultado.

| Clase | Pruebas | Fallos | Errores |
|---|---:|---:|---:|
| VetSpecialtyControllerMockitoTest de Ángel | 6 | 0 | 0 |
| VetSpecialtyControllerTest H2 de Ángel | 6 | 0 | 0 |
| PetControllerMockitoTest original | 5 | 0 | 0 |
| PetControllerTest original | 7 | 0 | 0 |
| PetServiceMockitoTest original | 7 | 0 | 0 |
| PetServiceTest original | 7 | 0 | 0 |
| PetClinicApplicationTests original | 1 | 0 | 0 |
| Total | 39 | 0 | 0 |

## Comprobaciones

- Cada uno de los seis escenarios A+B existe en las dos clases de prueba: asignar, eliminar, rechazar duplicado, consultar especialidades por veterinario, consultar veterinarios por especialidad y devolver lista vacia.
- H2 confirma insercion y eliminacion mediante SQL despues de flush/clear, conservacion de los padres y de otra relacion, y ausencia de sobrescritura ante un duplicado. Los datos semilla se restauran por rollback despues de cada prueba.
- Mockito comprueba el contrato JSON, los estados HTTP y las llamadas al servicio. Un duplicado se simula primero como creacion exitosa y luego como excepcion, esperando 409.
- Se conservaron schema.sql, data.sql, las pruebas originales y el historial de la referencia 2453ce2ec325139ac31dbc131c9fd039c14ce5de.
- Las asociaciones ManyToMany originales de Vet y Specialty se conservan. Se agregaron campos del esquema y se excluyeron las asociaciones de JSON, toString y equals/hashCode para evitar ciclos y cargar relaciones innecesarias.
- La logica comun de VetSpecialtyService se reutiliza del aporte de Ángel en el laboratorio 11. Las pruebas nuevas de este laboratorio solo acreditan A+B. No se afirma haber terminado ni probado los endpoints C+D de Diego.

Los informes XML y TXT efectivos se generan en target/surefire-reports. La suite puede mostrar advertencias de Mockito sobre agentes de Java 21; no representan fallos. Los resultados de CI, revisiones y merges deben comprobarse aparte y no se inventan.

## Pendientes

Diego debe implementar C+D con sus doce pruebas y commits propios; revisar el PR de Ángel; y abrir su PR para revision de Ángel. Faltan integrar ambos aportes despues de la revision, documentar el conflicto real exigido por TASKS_INTEGRATION.md, tomar las capturas y completar el informe y las tres conclusiones. Este aporte no finaliza por si solo el laboratorio completo del equipo.
