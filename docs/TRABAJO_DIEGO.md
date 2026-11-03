# Trabajo de Diego para el laboratorio 12

El reparto acordado es Ángel A+B y Diego C+D del grupo 5 de TASKS_INTEGRATION.md. Esta guia no representa trabajo realizado por Diego. Él debe implementar, ejecutar y comprobar su aporte, usando su propia cuenta e identidad de Git.

## Acceso

Ángel puede invitar a DiegoMagallanes11 como colaborador desde Settings, Collaborators del repositorio; Diego debe aceptar la invitacion. Alternativamente, Diego puede hacer un fork desde su cuenta y abrir un PR hacia AngelRosales78/Lab12ConstSoftware. No hace falta compartir contraseñas ni usar la identidad de Ángel.

## Rama y commits

Si el PR de Ángel ya se integro, partir de origin/main. Si sigue abierto, partir de origin/feature/angel-lab12-asignaciones-consultas y abrir inicialmente el PR contra esa rama. Cambiar la base a main cuando se integre el PR de Ángel.

```powershell
git clone https://github.com/AngelRosales78/Lab12ConstSoftware.git
cd Lab12ConstSoftware
git fetch origin
git switch -c feature/diego-lab12-principal-experiencia origin/feature/angel-lab12-asignaciones-consultas
git config --local user.name "Diego Magallanes"
```

Diego debe configurar tambien `git config --local user.email` con su propio correo asociado a GitHub. No se proporciona ni inventa su correo. Escribir mensajes de commit en español, sin prefijos. Si trabaja en un fork, agregarlo como remoto y subir la rama alli.

## Implementacion

Agregar en VetSpecialtyController:

| Operacion | Endpoint | Resultado |
|---|---|---|
| Marcar principal | PUT /vet-specialties/{vetId}/{specId}/primary | 200 y VetSpecialtyDTO; 404 si no esta asignada |
| Filtrar experiencia | GET /specialties/{specId}/vets?minExperience=7 | Lista de veterinarios de esa especialidad con experiencia mayor o igual al minimo |
| Actualizar experiencia | PUT /vet-specialties/{vetId}/{specId}/experience | JSON de entrada: yearsExperience; respuesta 200 con VetSpecialtyDTO |
| Actualizar certificacion | PUT /vet-specialties/{vetId}/{specId}/certification | JSON de entrada: certificationDate en YYYY-MM-DD; respuesta 200 con VetSpecialtyDTO |

El servicio ya ofrece setPrimarySpecialty, findVetsByMinExperience, updateYearsExperience y updateCertificationDate. El filtro debe ampliar el GET existente con un parametro opcional: sin parametro mantiene el comportamiento probado por Ángel; con parametro llama al filtro del servicio. No crear dos metodos GET con el mismo mapeo ambiguo. Las respuestas de asignacion deben seguir usando VetSpecialtyDTO.from.

## Pruebas propias de Diego

Agregar cada metodo tanto en VetSpecialtyControllerTest como en VetSpecialtyControllerMockitoTest:

1. testSetPrimarySpecialty.
2. testSetPrimarySpecialty_OnlyOnePrimary.
3. testSetPrimarySpecialty_NotAssigned.
4. testFindVetsByMinExperience.
5. testUpdateYearsExperience.
6. testUpdateCertificationDate.

En H2, usar los datos semilla y rollback. Confirmar cambios con flush/clear y una nueva lectura. Veterinario 3: especialidad 2 principal y 3 secundaria; radiology (id 1): veterinarios 2 y 5, con ocho y siete años. Para comprobar que el filtro realmente excluye resultados, agregar dentro de la prueba una asignacion con menos de siete años y confirmar que no aparece. Confirmar que cambiar una principal no altera otros veterinarios y que una especialidad no asignada devuelve 404 sin cambiar la principal anterior.

En Mockito, usar MockitoBean del servicio y extender VetSpecialtyObjectCreator cuando haga falta. Simular excepciones y resultados; verificar los argumentos enviados al servicio y el JSON. Conservar las doce pruebas de Ángel.

## Validacion y PR

```powershell
./mvnw.cmd test "-Dtest=VetSpecialtyControllerTest,VetSpecialtyControllerMockitoTest" "-Dspring.profiles.active=h2"
./mvnw.cmd clean test "-Dspring.profiles.active=h2"
git status
git add src/main/java/com/tecsup/petclinic/webs/VetSpecialtyController.java src/test/java/com/tecsup/petclinic/webs/VetSpecialtyControllerTest.java src/test/java/com/tecsup/petclinic/webs/VetSpecialtyControllerMockitoTest.java
git commit -m "Agrega los endpoints y pruebas de principal y experiencia"
```

Agregar tambien los archivos auxiliares y la documentacion que efectivamente se hayan modificado. Subir su rama y abrir un PR para que Ángel revise. Registrar en README los aportes reales y resultados efectivos. Revisar tambien el PR de Ángel desde DiegoMagallanes11. No afirmar una cantidad de pruebas ni aprobaciones antes de ejecutarlas y verificarlas.
