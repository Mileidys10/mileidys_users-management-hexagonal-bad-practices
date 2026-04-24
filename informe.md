# Informe de Soluciones de Clean Code y Arquitectura Hexagonal

Este documento presenta las soluciones a las violaciones documentadas en el catálogo de malas prácticas del proyecto **Users Management**. Las soluciones se basan en principios de Clean Code, Domain-Driven Design (DDD) y Arquitectura Hexagonal.

---

## 1. Soluciones a Violaciones de Arquitectura Hexagonal y Estilo con Java

| # | Regla | Solución Propuesta |
|---|---|---|
| 1 | **Estructura — Arquitectura Hexagonal** | Eliminar toda dependencia de frameworks o de la capa de infraestructura (`UserEntity`) dentro del modelo de dominio. El Entrypoint debe usar un Mapper (como MapStruct) para transformar datos de entrada en *Commands* de aplicación en lugar de construirlos directamente. |
| 2 | **Modelado y tipos** | Sustituir `@Data` por `@Value` en `UserModel` o migrar a un `record` de Java. Esto garantiza la inmutabilidad y previene la exposición de *setters*, protegiendo las invariantes del agregado de dominio. Transformar `UserResponse` a `record`. |
| 3 | **Lombok y validaciones** | Las anotaciones de validación (como `@Valid`, `@NotNull`) deben formar parte del contrato; por ende, deben ubicarse en la interfaz del caso de uso (puerto de entrada) y no sobre el método `@Override` en la implementación del servicio. |
| 4 | **Estilo y naming** | Renombrar todas las variables abreviadas (ej. `v`, `usrs`) a nombres descriptivos (`validator`, `users`). Reemplazar verificaciones `== null` por `Objects.isNull()`. Eliminar importaciones con comodín (`*`) a favor de explícitas. Agregar `@UtilityClass` a clases con puros métodos estáticos. |
| 5 | **Manejo de Strings / Retornos** | En lugar de retornar `null` cuando no hay usuarios, se debe retornar una colección vacía (`Collections.emptyList()`) para evitar `NullPointerException` en los consumidores. |
| 6 | **Excepciones, logging y telemetría** | Configurar el logger para omitir o enmascarar información sensible (PII) como emails en todas las capas. Dejar que las excepciones no recuperables fluyan hasta un *Global Handler* en vez de silenciarlas en bloques `try-catch` locales. |
| 7 | **Mappers y (de)serialización** | Sustituir las clases de mapeo manual por MapStruct, lo cual genera el *boilerplate* automáticamente. Delegar la construcción de *Commands* desde el *Entrypoint* hacia la capa de aplicación usando el mapper pertinente. |
| 9 | **Buenas prácticas de diseño** | Extraer responsabilidades de servicios monolíticos en servicios más granulares y cohesivos. Por ejemplo, separar la persistencia, notificaciones y validaciones. Invertir las dependencias haciendo que la infraestructura dependa de las abstracciones del dominio (Ports). |
| 10 | **Calidad (Magic Numbers y Literales)** | Reemplazar todos los *magic numbers* (`8`, `12`) por constantes semánticas (ej. `MIN_PASSWORD_LENGTH`). Extraer textos y mensajes de error hardcodeados hacia archivos de recursos (`messages.properties`) o constantes descriptivas. |
| 11 | **Pruebas** | Refactorizar las clases de prueba para aplicar la estructura `Arrange - Act - Assert` (Preparar, Actuar, Afirmar). Actualizar las aserciones a las propias de JUnit 5 (usar `assertNotNull(x)` en vez de `assertTrue(x != null)`). Agregar la anotación `@DisplayName` para definir la intención de la prueba. |

---

## 2. Soluciones a Violaciones según Clean Code

| # | Regla | Solución Propuesta |
|---|---|---|
| 1 | **Una sola cosa por función** | Extraer y delegar sub-tareas (validación, persistencia, notificación) a métodos privados o a dependencias inyectadas para que la función orquestadora solo tenga un propósito de alto nivel. |
| 2 | **Funciones pequeñas** | Descomponer los métodos "mini-clases" agrupando líneas de código lógicamente asociadas en nuevos métodos privados más pequeños y descriptivos. |
| 3 | **Un solo nivel de abstracción** | Evitar mezclar lógica de negocio (alto nivel) con manipulaciones de bajo nivel (I/O, parsing de strings). Extraer el detalle técnico a otro método o clase adaptadora. |
| 4 | **Lectura secuencial** | Reordenar los métodos dentro de las clases de manera que el lector lea desde el método público más general en la parte superior hasta llegar a los métodos privados de detalle al final. |
| 5 | **Pocos parámetros por función** | Agrupar listas largas de parámetros sueltos en objetos cohesivos (*Parameter Object* o *Value Objects*) que encapsulen esos datos, simplificando la firma de la función. |
| 6 | **Evitar parámetros booleanos de control** | Dividir la función en dos métodos distintos sin parámetros de control (ej. `notifyUser(...)` y `notifyUserAndLog(...)`) delegando la responsabilidad del flujo a la clase que invoca. |
| 7 | **Evitar efectos secundarios ocultos** | Si un método va a loguear o alterar un estado colateralmente, el nombre del método debe reflejarlo claramente (ej. `saveAndNotify()`), o bien, extraer el efecto secundario fuera de la función principal. |
| 8 | **Separar comandos y consultas (CQS)** | Dividir funciones mixtas en dos: un método que retorne información sin alterar el estado del sistema (*Query*) y otro que modifique el estado sin retornar datos (*Command*, usualmente de tipo `void`). |
| 9 | **Código expresivo antes que comentarios** | Eliminar la necesidad del comentario mejorando el nombre de las variables y métodos, o extrayendo el bloque de código documentado a una función cuyo nombre cuente lo que hacía el comentario. |
| 10 | **Eliminar comentarios redundantes** | Borrar cualquier comentario que meramente repita en palabras lo que el código hace obvio (ej. `// suma 1 a i` encima de `i++`). |
| 11 | **Evitar duplicación de conocimiento** | Refactorizar la lógica repetida de orquestación y validaciones, consolidándola en métodos de utilería estandarizados o mejor aún, en las reglas del propio *Value Object* del dominio. |
| 12 | **Alta cohesión real** | Separar clases grandes y variadas (como `LoginService`) en clases de propósito único y estrictamente enfocado que agrupen solo métodos relacionados entre sí. |
| 13 | **Evitar clases utilitarias innecesarias** | Trasladar la lógica de validación de negocio al modelo de dominio (agregados/Value Objects) y reemplazar mappers utilitarios estáticos por frameworks como MapStruct. |
| 14 | **Ley de Deméter** | Modificar objetos expuestos añadiendo métodos que actúen como delegados (ej. en lugar de `user.getPassword().verifyPlain(...)`, hacer `user.verifyPassword(...)`) para no revelar estructura interna. |
| 15 | **Inmutabilidad como preferencia** | Quitar anotaciones como `@Data` y reemplazar por constructores que establezcan un estado válido final y propiedades de solo lectura (`@Value` o `record`). |
| 16 | **Evitar condicionales repetitivas** | Reemplazar bloques masivos de `if/else` usando polimorfismo, el patrón *Strategy* o asociando comportamiento y propiedades directamente en los enumeradores. |
| 17 | **Manejo limpio de condiciones** | Simplificar condicionales densos extrayendo las validaciones lógicas largas hacia funciones booleanas privadas altamente descriptivas (ej. `if (isUserEligible(user))`). |
| 18 | **Evitar magic numbers y literales** | Reemplazar números (`8`, `12`) o strings (`"ACTIVE"`) sueltos por enumeraciones y constantes con nombres claros en mayúsculas (ej. `UserStatus.ACTIVE`). |
| 19 | **Evitar temporal coupling** | Rediseñar la clase, quizás forzando los requerimientos de inicialización en su constructor (vía inyección de dependencias) para garantizar que los objetos siempre se instancien en un estado íntegro y preparado. |
| 20 | **Objeto antes que primitivo** | Utilizar un encapsulamiento estricto creando *Value Objects* (`UserId`, `UserEmail`) con sus propias validaciones internamente en lugar de regar verificaciones de tipos `String` o `int` crudos por toda la base de código. |
| 21 | **No usar códigos especiales de error** | En lugar de utilizar valores arbitrarios como bandera de error (ej. `-1`), apoyarse en el sistema de excepciones de Java (ej. `UserNotFoundException`) o envolver el resultado en un `Optional`. |
| 22 | **Código fácil de refactorizar** | Basar la estructura del proyecto fuertemente en inyección de dependencias a través de interfaces, no atando el núcleo de la aplicación a ninguna implementación de infraestructura concreta. |
| 23 | **Minimizar conocimiento disperso** | Unificar cualquier concepto fracturado. Por ejemplo, definir qué hace que un email sea válido solamente dentro del contexto del objeto de dominio `UserEmail`. |
| 24 | **Consistencia semántica** | Aplicar el modelo del Lenguaje Ubicuo (Ubiquitous Language); si decidimos llamar al concepto "email", utilizar ese mismo término consistente en todas las variables y capas, nunca usar "correo" ni "correoElectronico". |
| 25 | **Preferir claridad sobre ingenio** | Reemplazar uniones de operadores lógicos o expresiones Lambda y Stream crípticas con bucles o variables bien estructuradas que el equipo completo pueda leer a primera vista. |
| 26 | **Evitar sobrecompactación** | Desanidar flujos, declarando variables temporales intermedias que auto-documenten cada paso del bloque para mejorar sustancialmente el entendimiento de la lógica global. |
| 27 | **Código listo para leer** | Seguir todas las convenciones anteriores combinadas para obtener rutinas de código que hablen por sí mismas, sin que el lector tenga que ir saltando entre archivos o pidiendo contexto histórico. |
