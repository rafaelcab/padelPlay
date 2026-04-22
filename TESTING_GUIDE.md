# Guia de testing del proyecto

## Objetivo del documento

Este documento define como deben implementarse los tests en este proyecto para mantener un criterio unico y consistente entre modulos, desarrolladores e IA. La guia no describe testing en abstracto: recoge el estilo real ya presente en el repositorio y fija reglas para seguir ampliandolo de forma uniforme.

Este proyecto solo contempla en esta guia los siguientes tipos de pruebas:

- tests unitarios
- tests de controller con `MockMvc`
- tests de integracion
- tests de performance

Quedan fuera de alcance otros tipos de pruebas. Si la IA detecta tests de otro tipo en el repositorio, no debe usarlos como referencia principal para generar nuevas pruebas a menos que se le pida expresamente.

## Principios que deben guiar cualquier test

Los tests deben salir de los requisitos y del comportamiento esperado, no solo del codigo existente. Un test correcto no valida simplemente "lo que hace ahora el metodo", sino lo que deberia hacer el sistema.

Reglas base:

- Los tests unitarios deben ser la mayoria.
- Cada prueba debe tener un objetivo unico y un nombre descriptivo.
- Deben cubrir caso feliz, errores, excepciones y casos borde relevantes.
- La IA puede generar un punto de partida, pero nunca se debe dejar el resultado minimo sin revisar.
- La cobertura ayuda, pero no sustituye el juicio tecnico.
- Debe priorizarse la cobertura de ramas y decisiones importantes, no solo la ejecucion superficial de lineas.

## Estrategia de testing del proyecto

La estrategia oficial del repositorio es esta:

- `test` ejecuta el conjunto rapido de pruebas.
- Las pruebas de integracion se ejecutan aparte con su propia tarea Gradle.
- Las pruebas de performance se ejecutan aparte con su propia tarea Gradle.
- La cobertura de unitarios se revisa con JaCoCo.

En este repositorio la separacion ya esta implementada en Gradle:

- En raiz, `test` excluye los tags `integration` y `performance`.
- En `server/build.gradle` existe `integrationTest` para `@Tag("integration")`.
- En `server/build.gradle` existe `performanceTest` para `@Tag("performance")`.

Por tanto, cualquier test nuevo debe respetar esa organizacion y no mezclar responsabilidades.

## Mapa de herramientas por tipo de prueba

### 1. Test unitario

Objetivo:
probar una clase o metodo de forma aislada, sin contexto Spring completo ni acceso real a red o base de datos.

Herramientas esperadas:

- JUnit 5
- AssertJ para aserciones
- Mockito para dependencias externas

Ejemplos reales del proyecto:

- `web-client/src/test/java/es/deusto/spq/webclient/service/ServerApiServiceTest.java`
- `commons/src/test/java/es/deusto/spq/persistence/UserTest.java`
- `commons/src/test/java/es/deusto/spq/serializable/UserDataTest.java`

### 2. Test de controller con MockMvc

Objetivo:
probar la capa web de Spring sin levantar el servidor completo, validando rutas, parametros, codigos HTTP, modelo, vistas o JSON.

Herramientas esperadas:

- `@WebMvcTest`
- `MockMvc`
- `@MockitoBean` para dependencias del controller
- `ObjectMapper` cuando haya body JSON

Ejemplos reales del proyecto:

- `server/src/test/java/es/deusto/spq/UserControllerTest.java`
- `server/src/test/java/es/deusto/spq/MessageControllerTest.java`
- `web-client/src/test/java/es/deusto/spq/webclient/controller/UserWebControllerTest.java`
- `web-client/src/test/java/es/deusto/spq/webclient/controller/MessageWebControllerTest.java`

### 3. Test de integracion

Objetivo:
verificar que varias capas reales colaboran correctamente, normalmente contexto Spring completo mas persistencia real.

Herramientas esperadas:

- `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
- `RestTemplate` para consumir la API expuesta
- AssertJ y JUnit 5
- `@Tag("integration")`

Ejemplo real del proyecto:

- `server/src/test/java/es/deusto/spq/ServerIntegrationTest.java`

### 4. Test de performance

Objetivo:
medir el comportamiento de operaciones criticas bajo carga. No valida solo que algo funcione, sino como responde en concurrencia, volumen o tiempo.

Herramientas esperadas:

- `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
- JUnitPerf
- `RestTemplate`
- `@Tag("performance")`

Ejemplo real del proyecto:

- `server/src/test/java/es/deusto/spq/ServerPerformanceTest.java`

## Regla oficial de etiquetas

Las etiquetas deben usarse de acuerdo con la configuracion actual de Gradle, no de forma arbitraria.

### Etiquetas obligatorias

- `@Tag("integration")` para toda prueba de integracion.
- `@Tag("performance")` para toda prueba de rendimiento.

### Etiquetas que no deben usarse como criterio de ejecucion por defecto

- Los tests unitarios no necesitan tag para ejecutarse en `./gradlew test`.
- Los tests de controller con `MockMvc` tampoco necesitan tag especial mientras formen parte del lote rapido.

### Regla practica para la IA

Cuando la IA cree pruebas nuevas:

- si el test es unitario, no poner `integration` ni `performance`
- si el test es de controller con `MockMvc`, no poner `integration` ni `performance`
- si el test levanta el contexto completo y prueba colaboracion real entre capas, poner `@Tag("integration")`
- si el test ejecuta carga, concurrencia o mediciones con JUnitPerf, poner `@Tag("performance")`

### Importante

No usar `@Tag("integration")` para tests de controller con `@WebMvcTest`. Aunque prueban Spring Web, en este proyecto forman parte del lote rapido y se ejecutan dentro de `test`.

## Convenciones de ubicacion

Los tests deben vivir junto a su modulo y reflejar el paquete de la clase bajo prueba.

Reglas:

- `commons/src/test/java/...` para entidades, DTOs y logica compartida.
- `server/src/test/java/...` para controllers REST, integracion y performance del backend.
- `web-client/src/test/java/...` para controllers MVC y servicios cliente del frontend server-side.

La ubicacion debe dejar clara la capa que se esta verificando.

## Convenciones de nombres

### Nombre de clase

Usar el patron:

- `NombreClaseTest`
- `NombreControllerTest`
- `NombreServicioTest`
- `NombreModuloIntegrationTest` si ya existe un patron consolidado
- `NombreModuloPerformanceTest` para rendimiento

En este proyecto ya existen estos patrones:

- `ServerApiServiceTest`
- `UserControllerTest`
- `ServerIntegrationTest`
- `ServerPerformanceTest`

### Nombre de metodo

Usar nombres largos, descriptivos y basados en comportamiento:

- `registerUser_createsNewUserAndReturnsSaved`
- `sayMessage_withInvalidPassword_returnsBadRequest`
- `getMessagesByUser_returnsEmptyListForUnknownUser`

Patron recomendado:

- `metodo_condicion_resultadoEsperado`

No usar nombres genericos como:

- `testUser`
- `test1`
- `works`
- `shouldWork`

## Estructura interna recomendada de cada test

Aunque no haga falta comentar siempre `arrange`, `act` y `assert`, la estructura debe seguir ese orden de forma clara.

Patron recomendado:

1. Preparacion de datos y mocks.
2. Ejecucion de la accion.
3. Verificacion del resultado y de interacciones relevantes.

Ejemplo corto de estilo unitario:

```java
@Test
void registerUser_postsToUsersAddAndReturnsBody() {
    when(restTemplate.exchange(...))
            .thenReturn(new ResponseEntity<>("Saved", HttpStatus.OK));

    String result = service.registerUser("alice", "pw");

    assertThat(result).isEqualTo("Saved");
    verify(restTemplate).exchange(...);
}
```

## Reglas por tipo de test

### Tests unitarios

#### Cuando escribirlos

Deben escribirse para:

- logica de negocio
- transformaciones de datos
- validaciones
- adaptadores cliente que usen dependencias mockeadas
- entidades y DTOs con comportamiento propio

#### Que deben probar

Como minimo:

- caso feliz
- decisiones relevantes
- comportamiento con entradas vacias o nulas si aplica
- errores o respuestas no exitosas si el codigo las maneja
- efectos laterales de la unidad probada

#### Que no deben hacer

- no levantar Spring sin necesidad
- no usar base de datos real
- no depender de red real
- no probar librerias externas por si mismas
- no comprobar implementaciones internas irrelevantes

#### Estilo esperado en este proyecto

Para clases sin Spring:

- instanciacion manual de la clase bajo prueba
- dependencias con `Mockito.mock(...)`
- aserciones con `assertThat(...)`
- `verify(...)` cuando haya llamadas relevantes a colaboradores

#### Plantilla base

```java
class NombreServicioTest {

    private final Dependencia dependencia = Mockito.mock(Dependencia.class);
    private final NombreServicio service = new NombreServicio(dependencia);

    @Test
    void metodo_condicion_resultadoEsperado() {
        when(dependencia.algo()).thenReturn(...);

        Resultado result = service.metodo(...);

        assertThat(result).isEqualTo(...);
        verify(dependencia).algo();
    }
}
```

### Tests de controller con MockMvc

#### Cuando escribirlos

Deben escribirse para cada controller que exponga comportamiento relevante en web o REST.

#### Que deben probar

En controllers REST:

- endpoint correcto
- metodo HTTP correcto
- codigo de estado esperado
- estructura y contenido del JSON
- respuestas ante datos invalidos o credenciales incorrectas

En controllers MVC del web-client:

- vista renderizada
- atributos del modelo
- parametros recibidos
- delegacion al servicio correspondiente

#### Configuracion esperada

Para controllers Spring:

- usar `@WebMvcTest(ClaseController.class)`
- usar `@Autowired MockMvc`
- usar `@MockitoBean` para dependencias del controller
- usar `ObjectMapper` si se envian cuerpos JSON

#### Que no deben hacer

- no levantar toda la aplicacion con `@SpringBootTest` si solo se quiere probar el controller
- no acceder a base de datos real
- no mezclar objetivos de controller e integracion en el mismo test

#### Plantilla REST con JSON

```java
@WebMvcTest(NombreController.class)
class NombreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Dependencia dependencia;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void endpoint_condicion_resultadoEsperado() throws Exception {
        when(dependencia.operacion(...)).thenReturn(...);

        mockMvc.perform(post("/ruta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campo").value("valor"));
    }
}
```

#### Plantilla MVC con vista

```java
@WebMvcTest(NombreWebController.class)
class NombreWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NombreServicio apiService;

    @Test
    void accion_condicion_resultadoEsperado() throws Exception {
        when(apiService.operacion(...)).thenReturn(...);

        mockMvc.perform(post("/ruta").param("campo", "valor"))
                .andExpect(status().isOk())
                .andExpect(view().name("vista"))
                .andExpect(model().attributeExists("resultado"));

        verify(apiService).operacion(...);
    }
}
```

### Tests de integracion

#### Cuando escribirlos

Reservarlos para flujos donde interese validar colaboracion real entre capas.

Casos tipicos:

- controller + servicio + repositorio
- serializacion y deserializacion reales
- persistencia real
- respuestas HTTP reales de la aplicacion arrancada

#### Reglas obligatorias

- deben llevar `@Tag("integration")`
- deben usar `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
- deben probar flujos completos relevantes, no microcasos triviales
- deben evitar duplicar masivamente lo ya cubierto por unitarios o controller tests

#### Estilo esperado en este proyecto

Patron actual:

- puerto aleatorio con `@LocalServerPort`
- `RestTemplate` inicializado en `@BeforeEach`
- helper `url(String path)`
- aserciones sobre `ResponseEntity`
- excepciones HTTP comprobadas con `assertThrows(...)` cuando aplique

#### Que debe cubrir una integracion buena

- flujo de alta o modificacion
- flujo de consulta
- flujo con error relevante
- persistencia real o colaboracion real entre componentes

#### Plantilla base

```java
@Tag("integration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class NombreModuloIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void flujo_condicion_resultadoEsperado() {
        ResponseEntity<String> response = restTemplate.postForEntity(url("/ruta"), payload, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("...");
    }
}
```

### Tests de performance

#### Cuando escribirlos

Solo para operaciones importantes, frecuentes o potencialmente costosas. No crear pruebas de rendimiento para acciones irrelevantes o puramente decorativas.

Prioridades recomendadas:

- altas de entidades clave
- busquedas frecuentes
- consultas agregadas
- operaciones concurrentes
- endpoints criticos del sistema

#### Reglas obligatorias

- deben llevar `@Tag("performance")`
- deben usar JUnitPerf
- deben centrarse en operaciones criticas
- deben documentar claramente el escenario de carga

#### Estilo esperado en este proyecto

Patron actual:

- `@ExtendWith(JUnitPerfInterceptor.class)`
- `@JUnitPerfTestActiveConfig`
- reporte HTML en `build/reports/junitperf/report.html`
- datos semilla creados en `@BeforeAll` cuando el escenario lo necesite
- tests con `threads`, `durationMs` y `warmUpMs`

#### Que debe evitar la IA

- no convertir pruebas funcionales normales en performance tests
- no medir endpoints sin interes real para el negocio
- no mezclar demasiadas aserciones funcionales dentro del mismo test de carga

#### Plantilla base

```java
@Tag("performance")
@ExtendWith(JUnitPerfInterceptor.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NombreModuloPerformanceTest {

    @JUnitPerfTestActiveConfig
    private static final JUnitPerfReportingConfig REPORTER_CONFIG =
            JUnitPerfReportingConfig.builder()
                    .reportGenerator(new HtmlReportGenerator("build/reports/junitperf/report.html"))
                    .build();

    @Test
    @JUnitPerfTest(threads = 10, durationMs = 2000, warmUpMs = 500)
    void operacionCritica_underLoad() {
        // ejecutar la operacion
    }
}
```

## Cobertura con JaCoCo

JaCoCo se usa para medir cobertura del conjunto rapido de pruebas.

Comandos utiles:

```bash
./gradlew test
./gradlew jacocoTestReport
./gradlew :server:jacocoTestReport
./gradlew :web-client:jacocoTestReport
```

Ruta del reporte HTML por modulo:

```text
<modulo>/build/reports/jacoco/index.html
```

### Como interpretar la cobertura

La cobertura sirve para detectar zonas no ejercitadas, pero no demuestra por si sola que los tests sean buenos.

La IA debe priorizar:

- ramas de `if` y `else`
- errores controlados
- respuestas HTTP alternativas
- caminos con `null`, vacios o no encontrado
- flujos que modifican estado

No se debe perseguir cobertura alta con tests irrelevantes o redundantes.

## Criterios minimos que debe seguir la IA al generar tests

Cuando la IA implemente nuevas pruebas en este proyecto debe cumplir siempre estas reglas:

- usar como referencia principal el estilo ya existente en el modulo afectado
- elegir el tipo de test mas barato que cubra bien el objetivo
- preferir unitario antes que integracion si el comportamiento puede aislarse
- usar `MockMvc` para controllers en lugar de arrancar todo Spring
- etiquetar integracion y performance obligatoriamente
- no etiquetar como integracion un `@WebMvcTest`
- cubrir al menos un caso feliz y un caso de error cuando el comportamiento lo permita
- verificar interacciones con mocks solo cuando aporten valor real
- mantener nombres de tests basados en comportamiento
- no introducir frameworks distintos a los ya presentes salvo peticion explicita

## Seleccion correcta del tipo de prueba

Si la IA duda entre varios tipos, debe aplicar estas decisiones:

- Si se prueba una clase aislada con dependencias simuladas: test unitario.
- Si se prueba un controller Spring con rutas, JSON, vistas o modelo y dependencias mockeadas: test de controller con `MockMvc`.
- Si se necesita validar que varias capas reales trabajan juntas con contexto completo: test de integracion.
- Si se necesita medir respuesta bajo carga o concurrencia: test de performance.

## Checklist antes de dar un test por bueno

- El nombre explica claramente que se prueba.
- El tipo de test elegido es el adecuado y no mas costoso de lo necesario.
- Si es integracion o performance, lleva el tag correcto.
- El test cubre un comportamiento importante, no una trivialidad.
- Hay al menos una comprobacion clara del resultado esperado.
- Si hay mocks, se usan para aislar dependencias reales y no por inercia.
- El test es legible y sigue el estilo del modulo.
- No se ha convertido un requisito ambiguo en un test superficial.

## Comandos del proyecto que deben respetarse

Con esta guia, la IA debe asumir estos comandos como flujo oficial:

```bash
./gradlew test
./gradlew :server:integrationTest
./gradlew :server:performanceTest
./gradlew jacocoTestReport
```

Resumen de ejecucion:

- `./gradlew test`: unitarios y controllers con `MockMvc`
- `./gradlew :server:integrationTest`: solo integracion
- `./gradlew :server:performanceTest`: solo rendimiento

## Decision final de estandarizacion

El criterio unificado del proyecto queda fijado asi:

- Los tests unitarios son la base y deben ser la mayoria.
- Los tests de controller con `MockMvc` forman parte del lote rapido de `test`.
- Los tests de integracion se etiquetan con `integration` y se ejecutan aparte.
- Los tests de performance se etiquetan con `performance` y se ejecutan aparte.
- La cobertura con JaCoCo se usa como apoyo, no como unico objetivo.
- La IA debe completar los casos importantes y no limitarse al caso feliz ni al comportamiento accidental del codigo actual.
