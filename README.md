# PadelPlay

Proyecto **multimódulo con Gradle** formado por un **cliente web**, un **servidor backend** y un módulo **common** para compartir clases entre ambos.

---

> [!WARNING]
> **No ejecutes comandos de Gradle dentro de las subcarpetas** `padelplay-server` o `padelplay-cliente`. Hazlo siempre desde la carpeta principal `padelPlay`.

---

## 📁 Estructura del Proyecto

```text
padelPlay (Raíz)
├── padelplay-common
├── padelplay-server
└── padelplay-cliente
```
## GIT
## Configuración e Instalación rápida
### 1. Clonar el repositorio
Desde tu terminal, clona el proyecto y accede a la carpeta raíz:
```bash
git clone [https://github.com/rafaelcab/padelPlay.git](https://github.com/rafaelcab/padelPlay.git)
```
```bash
cd padelPlay
```

### 2. Verificar módulos
Comprueba que Gradle detecta los módulos correctamente:

```
gradlew.bat projects
```
Resultado esperado:

```
Root project 'padelPlay'
+--- Project ':padelplay-common'
+--- Project ':padelplay-server'
\--- Project ':padelplay-cliente'
```

### 3. Compilar el proyecto
Para compilar todos los módulos a la vez:
```
gradlew.bat build
```
## Ejecución de los Módulos
### Ejecutar el Servidor (Backend)
```
gradlew.bat :padelplay-server:bootRun
```

URL: http://localhost:8080

### Ejecutar el Cliente (Frontend)
Abre una nueva terminal y ejecuta:

```
gradlew.bat :padelplay-cliente:bootRun
```

URL: http://localhost:8081

## Flujo de Trabajo con Git
Repositorio: https://github.com/rafaelcab/padelPlay

Comandos básicos:
Actualizar: git pull

Añadir cambios: git add .

Confirmar: git commit -m "mensaje del commit"

Subir: git push ()

## Gestión de Ramas
(Este apartado se completará más adelante cuando definamos la estrategia de ramas del proyecto.)

## 1. Qué es este proyecto

PadelPlay está dividido en **3 módulos**:

* **padelplay-server** → backend REST con Spring Boot
* **padelplay-cliente** → cliente web con Spring Boot, MVC y Thymeleaf
* **padelplay-common** → módulo compartido con DTOs y clases comunes

La idea es separar responsabilidades:

* el **server** gestiona lógica de negocio, persistencia y API REST
* el **cliente** muestra vistas y consume la API del servidor
* **common** evita duplicar DTOs y clases compartidas

---

## 2. Estructura del proyecto

```text
padelPlay-app/
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│
├── padelplay-common/
│   ├── build.gradle
│   └── src/main/java/com/padelplay/common/
│       └── dto/
│
├── padelplay-server/
│   ├── build.gradle
│   └── src/main/java/com/padelplay/server/
│
└── padelplay-cliente/
    ├── build.gradle
    └── src/main/java/com/padelplay/cliente/
```

---

## 3. Cómo importar el proyecto correctamente

## Muy importante

**No abrir solo `padelplay-server` o solo `padelplay-cliente`.**

Hay que abrir **la carpeta raíz del proyecto**:

```text
padelPlay-app
```

Porque este proyecto usa **Gradle multimódulo** y la raíz es la que conecta todos los módulos.

## En VS Code

1. Abrir **File > Open Folder**
2. Seleccionar la carpeta **`padelPlay-app`**
3. Esperar a que Gradle cargue los módulos

## En IntelliJ

1. Abrir el proyecto desde la carpeta **`padelPlay-app`**
2. Importar como proyecto **Gradle**
3. Esperar a que se sincronicen los módulos

## Si Gradle no refresca

En terminal, desde la raíz:

```bash
gradlew.bat build
```

---

## 4. Qué hace cada módulo

## 4.1 `padelplay-server`

Es el **backend** del proyecto.

Se encarga de:

* exponer endpoints REST
* contener la lógica de negocio
* acceder a la base de datos
* trabajar con entidades, repositorios y servicios

### Librerías principales del servidor

* `spring-boot-starter-webmvc`
* `spring-boot-starter-data-jpa`
* `h2` (temporalmente, para desarrollo)
* más adelante: `mysql-connector-j`

### Arquitectura esperada del servidor

```text
controller
facade
service
repository (o dao)
entity
dto
config
exception
```

### Flujo típico en server

```text
Controller -> Facade -> Service -> Repository -> Base de datos
```

---

## 4.2 `padelplay-cliente`

Es el **cliente web** del proyecto.

Se encarga de:

* mostrar vistas HTML
* usar Thymeleaf
* recibir peticiones del navegador
* llamar al backend por HTTP

### Librerías principales del cliente

* `spring-boot-starter-webmvc`
* `spring-boot-starter-thymeleaf`
* `spring-boot-starter-test`

### Arquitectura esperada del cliente

```text
controller
service
client (o rest)
dto
config
```

### Flujo típico en cliente

```text
Navegador -> Controller MVC -> Service -> llamada HTTP al server -> Thymeleaf
```

---

## 4.3 `padelplay-common`

Es el módulo compartido.

Sirve para meter clases que usan tanto el cliente como el servidor, por ejemplo:

* DTOs
* enums compartidos
* requests/responses comunes
* pequeñas constantes comunes

### Qué sí debe ir en `common`

* `PruebaDto`
* `UsuarioDto`
* `PartidoDto`
* `ReservaDto`
* enums compartidos

### Qué NO debe ir en `common`

* entidades JPA
* repositorios
* servicios
* controladores
* lógica de negocio del servidor

---

## 5. Cómo se conectan los módulos

## Dependencias correctas

```text
padelplay-server   -> padelplay-common
padelplay-cliente  -> padelplay-common
```

## Dependencias incorrectas

```text
padelplay-cliente -> padelplay-server   ❌
padelplay-server  -> padelplay-cliente  ❌
```

### Por qué

Porque **cliente y servidor no se conectan por dependencia Gradle**, sino por **HTTP**.

El cliente llama al servidor por URL, por ejemplo:

```text
http://localhost:8080/api/...
```

Eso significa:

* el cliente **no debe importar código interno del servidor**
* el servidor **no debe depender del cliente**
* ambos solo comparten DTOs a través de `common`

---

## 6. Qué es la multimodularidad en este proyecto

Este proyecto usa **Gradle multimódulo**.

Eso significa que hay **un proyecto raíz** y varios **subproyectos** conectados.

### Ventajas

* mejor organización
* menos duplicación de código
* DTOs compartidos en un único sitio
* dependencias separadas por responsabilidad
* más fácil de mantener
* más fácil de escalar
* cada módulo tiene un objetivo claro

### Ejemplo de ventaja real

Sin `common`, tendríamos que duplicar DTOs en cliente y servidor.

Con `common`:

* se define el DTO una sola vez
* ambos módulos lo usan
* si se cambia, se cambia en un único sitio

---

## 7. Base de datos actual

Actualmente el servidor usa **H2** de forma temporal para desarrollo.

### Por qué se usa H2 ahora

* permite arrancar el proyecto sin depender todavía de MySQL
* facilita pruebas rápidas
* permite empezar con entidades y repositorios

### Importante

Esto es **temporal**.

Más adelante se cambiará a **MySQL**, que es la base de datos real del proyecto.

### Idea importante

H2 sirve para desarrollar rápido, pero **no sustituye** la base de datos final.

---

## 8. Cómo ejecutar el proyecto

Todos los comandos se ejecutan **desde la raíz**:

```text
padelPlay-app
```

## Ver módulos detectados por Gradle

```bash
gradlew.bat projects
```

## Compilar todo el proyecto

```bash
gradlew.bat build
```

## Ejecutar el servidor

```bash
gradlew.bat :padelplay-server:bootRun
```

## Ejecutar el cliente

```bash
gradlew.bat :padelplay-cliente:bootRun
```

---

## 9. Puertos usados

Normalmente:

* **Servidor** → `8080`
* **Cliente** → `8081`

### Ejemplo

* servidor: `http://localhost:8080`
* cliente: `http://localhost:8081`

---

## 10. Ejemplo de comunicación cliente-servidor

### En el server

Se expone un endpoint REST:

```text
GET /api/test
```

### En el cliente

Un controlador o servicio hace una llamada HTTP al backend.

### Flujo completo

```text
Navegador -> Cliente MVC -> llamada HTTP -> Server REST -> respuesta -> Cliente -> vista Thymeleaf
```

---


## 12. Qué puede confundir al principio

### “¿Por qué hay varios `build.gradle`?”

Porque cada módulo tiene sus propias dependencias.

### “¿Por qué hay un `build.gradle` en raíz?”

Porque la raíz coordina todo el proyecto multimódulo.

### “¿Por qué `common` no es un proyecto Spring Boot?”

Porque `common` no es una aplicación. Solo es una librería compartida.

### “¿Por qué cliente no depende del servidor?”

Porque el cliente consume el servidor por HTTP, no por imports internos.

---

## 14. Próximos pasos recomendados

1. Crear DTOs compartidos en `padelplay-common`
2. Crear la arquitectura por capas del servidor
3. Separar bien controller / facade / service / repository
4. Empezar con entidades del dominio
5. Cambiar H2 por MySQL cuando la configuración final esté decidida
6. Añadir documentación de endpoints más adelante

---

## 15. Resumen fácil

Si alguien del grupo se pierde, que recuerde esto:

* **server** = backend
* **cliente** = web con Thymeleaf
* **common** = DTOs compartidos
* **raíz** = donde se ejecuta Gradle
* **cliente y server se comunican por HTTP**
* **no se duplican DTOs**
* **no se trabaja dentro de un módulo aislado, sino desde la raíz**

---

## 16. Comandos más usados

```bash
gradlew.bat projects
gradlew.bat build
gradlew.bat :padelplay-server:bootRun
gradlew.bat :padelplay-cliente:bootRun
```

---

## 17. Nota final

Si algo deja de funcionar, antes de tocar cosas al azar comprobar:

1. si se abrió la raíz del proyecto
2. si Gradle detecta los módulos
3. si el DTO está en `common`
4. si el cliente está llamando al puerto correcto del servidor
5. si el servidor está arrancado

Tocar cosas sin comprobar eso primero es la forma más rápida de romper el proyecto.
