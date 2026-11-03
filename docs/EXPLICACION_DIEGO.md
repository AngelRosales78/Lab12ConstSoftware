# Laboratorio 12 bloques C y D de Diego

Autor: Diego Magallanes, GitHub DiegoMagallanes11. El trabajo parte de la rama de Angel con A+B y conserva sus pruebas. Corresponde al grupo 5 de TASKS_INTEGRATION.md, segun el reparto compartido por el equipo. El Word general enumera otras entidades; el equipo debe confirmar con el docente que este es el grupo asignado.

## Endpoints implementados

| Operacion | Peticion | Respuesta |
|---|---|---|
| Elegir principal | PUT /vet-specialties/3/3/primary | 200 y DTO de la asignacion |
| Filtrar experiencia | GET /specialties/1/vets?minExperience=7 | 200 y lista de veterinarios |
| Cambiar experiencia | PUT /vet-specialties/2/1/experience con yearsExperience | 200 y DTO actualizado |
| Cambiar certificacion | PUT /vet-specialties/2/1/certification con certificationDate | 200 y DTO actualizado |

El GET sin minExperience conserva el comportamiento de A+B. No se duplican rutas ambiguas. La conversion de la respuesta usa el DTO plano de Angel para evitar exponer proxies JPA. El controlador delega las reglas al servicio existente y valida que yearsExperience este presente antes de convertirlo a int.

## Pruebas del bloque C

- testSetPrimarySpecialty comprueba estado HTTP, identificadores y primarySpecialty=true, y en H2 confirma el cambio persistido.
- testSetPrimarySpecialty_OnlyOnePrimary comprueba en H2 que elegir la especialidad 3 desmarque la 2 para el veterinario 3 y no cambie la principal del veterinario 2. En Mockito se comprueba el contrato y la delegacion de cambios, no una garantia de persistencia.
- testSetPrimarySpecialty_NotAssigned comprueba 404 y que la principal anterior permanezca intacta en la base real.

## Pruebas del bloque D

- testFindVetsByMinExperience usa los veterinarios 2 y 5 de radiology. En H2 agrega un candidato con seis años y comprueba filtros de siete, ocho y nueve años, ademas de conservar la consulta sin filtro.
- testUpdateYearsExperience comprueba el JSON y una lectura nueva de la experiencia persistida. Confirma que fecha y principal no cambien.
- testUpdateCertificationDate comprueba una fecha bisiesta valida, el contrato JSON y la lectura desde H2, conservando experiencia y principal.

Cada escenario se agrego tanto a VetSpecialtyControllerTest como a VetSpecialtyControllerMockitoTest, manteniendo los seis metodos existentes de Angel en cada clase. Se agregaron tres pruebas negativas por modalidad: experiencia ausente o negativa, fecha ausente o mal formada y filtro negativo o no numerico. Los errores devuelven 400 sin modificar los datos en H2.

## Metodo y limites

Las pruebas reales atraviesan MockMvc, controlador, servicio, repositorio y H2 sin mocks. Usan los datos semilla y rollback. flush y clear fuerzan una lectura real posterior a las escrituras. Las pruebas con MockitoBean simulan el servicio; comprueban HTTP, JSON y argumentos enviados al servicio. No se presentan como una prueba de restricciones SQL ni de concurrencia.

No se modifican schema.sql, data.sql ni las implementaciones de servicio y repositorio de Angel. No se reutilizan los resultados del laboratorio 11 como evidencia del laboratorio 12.

## Ejecutar y colaborar

Con JDK 21 y JAVA_HOME configurado:

```powershell
./mvnw.cmd test "-Dtest=VetSpecialtyControllerTest,VetSpecialtyControllerMockitoTest" "-Dspring.profiles.active=h2"
./mvnw.cmd clean test "-Dspring.profiles.active=h2"
```

La rama feature/diego-lab12-principal-experiencia parte de feature/angel-lab12-asignaciones-consultas porque su PR seguia abierto. El PR de Diego apunta inicialmente a esa misma rama. Despues de integrar el PR de Angel debe cambiarse su base a main. Angel debe revisar el aporte de Diego antes del merge.

No se afirma que se haya resuelto un conflicto en este laboratorio. Las evidencias del laboratorio 11 pertenecen a otro repositorio y no sustituyen este requisito.
