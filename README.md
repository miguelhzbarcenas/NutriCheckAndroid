# Nutricheck 🍏

Nutricheck es una aplicación diseñada como una herramienta para nutricionistas y profesionales de la salud. La aplicación permite una gestión médica de pacientes, incluyendo el registro de métricas como peso, talla, y edad

El objetivo principal es centralizar la información de los pacientes de forma segura y accesible en el dispositivo (offline-first). A futuro, esta base de datos permitirá alimentar un módulo de cálculos nutricionales automáticos (déficit/superávit calórico, macronutrientes, TMB, etc.) para agilizar la planificación de dietas.

## 🚀 Versión Actual y Funcionalidades

La versión actual sienta las bases del proyecto, enfocándose en la gestión completa de la información de los pacientes.

* **Gestión de Pacientes (CRUD):** Implementación completa para Crear, Leer, Actualizar y Eliminar perfiles de pacientes.
* **Registro de Métricas:** Almacenamiento de datos vitales asociados a cada paciente (peso, talla, edad, etc.).
* **Persistencia Local con Room:** Todos los datos se almacenan de forma segura en una base de datos local, garantizando el acceso y funcionamiento completo de la app sin conexión a internet (offline-first).
* **Navegación Intuitiva:** Flujo de usuario claro y definido usando Android Navigation Component, moviéndose entre la lista de pacientes, el formulario de creación/edición y la vista de detalles.
* **Interfaz Reactiva:** La UI se actualiza automáticamente cuando se añade, modifica o elimina un paciente, gracias al uso de Flow y StateFlow.
* **Diseño Moderno:** Interfaz de usuario limpia y moderna construida con Material 3.

## 🛠️ Stack Tecnológico y Arquitectura

El proyecto está construido siguiendo las guías de arquitectura moderna de Android para crear una aplicación robusta, escalable y fácil de mantener.

| Componente | Tecnología/Librería | Propósito |
| :--- | :--- | :--- |
| UI | XML, Material 3 | Construcción de interfaces de usuario nativas y adaptables. |
| Lenguaje | Kotlin | Lenguaje principal, aprovechando corrutinas y flujos. |
| Arquitectura | MVVM (Model-View-ViewModel) | Separación de responsabilidades entre UI, lógica y datos. |
| Asincronía | Kotlin Coroutines & Flow | Gestión de operaciones en segundo plano y flujos de datos reactivos. |
| Base de Datos | Room | Abstracción sobre SQLite para persistencia de datos local. |
| Navegación | Android Navigation Component | Gestión del flujo de navegación y paso de argumentos (Safe Args). |
| Ciclo de Vida | AndroidX Lifecycle (ViewModel, LiveData) | Componentes conscientes del ciclo de vida para evitar memory leaks. |

## Arquitectura en Detalle

### 1. Base de Datos (Capa de Datos)

* **Room:** Se utiliza para la persistencia de los registros.
    * **`@Entity`:** Define las tablas del modelo de datos (`Paciente`).
    * **`@DAO`:** Interfaz que abstrae las consultas SQL (INSERT, UPDATE, DELETE, SELECT). Las consultas que devuelven listas de datos están expuestas como `Flow<List<Paciente>>` para habilitar la programación reactiva.
    * **`@Database`:** Clase principal que configura y sirve como punto de acceso a la base de datos.

### 2. Arquitectura MVVM y Flujo de Datos Reactivo

* **View (Fragments):** Su única responsabilidad es observar los datos expuestos por el ViewModel (usando `StateFlow`) y notificar las interacciones del usuario (clics, envíos de formularios).
* **ViewModel:** Contiene la lógica de presentación. Se comunica con el Repositorio para solicitar o enviar datos. Utiliza las Corrutinas de Kotlin para ejecutar estas operaciones en segundo plano y expone el estado de la UI a la Vista a través de `StateFlow`.
* **Model (Repository + Room):** El Repositorio actúa como la única fuente de verdad (Single Source of Truth), mediando entre el ViewModel y las fuentes de datos (en este caso, Room).

### 3. Navigation Component con Type-Safety

* **`nav_graph.xml`:** Define todas las pantallas (destinos) y las acciones de navegación entre ellas.
* **Safe Args:** Se utiliza este plugin para pasar datos entre destinos de forma segura y robusta, evitando errores en tiempo de ejecución. Por ejemplo, al pasar el `pacienteId` de la lista a la pantalla de edición, Safe Args genera clases que garantizan la corrección de tipos.


## 🎯 Roadmap y Futuras Funcionalidades

Sobre la base sólida ya implementada, la versión final de Nutricheck incluirá:

* **Mayor cantidad de datos en historia clinica:** Falta consultar con los especialistas mayor número de datos de registro para la historia clinica. (Medidas antropometricas, estudios de laboratorio, etc.)
* **Módulo de Cálculos Nutricionales:**
    * Cálculo de Tasa Metabólica Basal (TMB), Gasto Energético Total (GET), etc.
    * Cálculo de calorías para déficit y superávit.
    * Distribución recomendada de macronutrientes.
* **Gráficas de Progreso:** Visualizar la evolución del peso y otras métricas del paciente a lo largo del tiempo.
* **Exportación de Datos:** Permitir al profesional exportar el registro o plan nutricional del paciente (p.ej., en formato PDF).
* **Inyección de Dependencias (Hilt):** Refactorizar el proyecto para usar Hilt, simplificando la gestión de dependencias y mejorando la capacidad de realizar pruebas unitarias.
* **Autenticación y Nube (Opcional):** Añadir un sistema de login para profesionales y sincronizar los datos de pacientes en la nube (p.ej., con Firebase) para acceso multi-dispositivo.
