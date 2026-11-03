# Resultados de Diego laboratorio 12

Validacion local del 3 de noviembre de 2026 con Java 21.0.11 y Maven Wrapper. Se ejecuto `mvnw.cmd -B test -Dspring.profiles.active=h2`, con un archivo de log independiente para evitar interferencias del entorno.

Resultado verificado: **BUILD SUCCESS; 57 pruebas, 0 fallos, 0 errores y 0 omitidas**.

| Aporte | Pruebas |
|---|---:|
| Base original y A+B de Angel | 39 |
| C+D de Diego y validaciones adicionales | 18 |
| Total | 57 |

VetSpecialtyControllerTest contiene 15 pruebas reales y VetSpecialtyControllerMockitoTest contiene 15 con servicio simulado. Cada clase conserva las seis de Angel y agrega nueve de Diego: seis del reparto C+D y tres pruebas de entradas invalidas.

Las otras 27 pruebas pertenecen al proyecto base. No se atribuyen a Diego. Las doce pruebas de Angel se conservaron sin cambiar sus escenarios.

Se verifican JSON, codigos HTTP 200, 400 y 404 y los argumentos de delegacion. En H2 se comprueba la persistencia de principal, experiencia y certificacion y la ausencia de cambios ante entradas invalidas. El filtro usa casos positivos, limite inclusivo, un candidato excluido y lista vacia. El esquema y los datos iniciales no fueron modificados.

Los informes reales se generan en target/surefire-reports. El PR debe ser revisado por Angel antes de integrarse. Todavia no se ha resuelto un conflicto en este laboratorio; no se presenta como completado.
