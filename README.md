***

# Nutricheck 🍏

Nutricheck es una aplicación integral diseñada para nutricionistas y profesionales de la salud. La aplicación combina una gestión médica eficiente de pacientes con una suite de herramientas de cálculo clínico automatizado.

El objetivo principal es centralizar la información de los pacientes de forma segura, permitiendo no solo el registro de métricas, sino también la planificación nutricional inmediata mediante calculadoras especializadas integradas.

## 🚀 Versión Actual y Funcionalidades

La versión actual ha evolucionado de un simple registro a una herramienta de trabajo clínico completa.

### 🔐 Seguridad y Acceso
* **Autenticación con Firebase:** Sistema de inicio de sesión y registro mediante correo electrónico y contraseña.
* **Gestión de Sesión:** Incluye validaciones de formato, recuperación de contraseña ("Olvidé mi contraseña") y cierre de sesión seguro.

### 📋 Gestión Clínica (CRUD)
* **Expediente de Pacientes:** Funcionalidad completa para crear, leer, editar y eliminar perfiles.
* **Empty States Amigables:** Interfaz guiada para usuarios nuevos sin pacientes registrados.
* **Persistencia Híbrida:** Autenticación en la nube con almacenamiento de datos de pacientes en local (Room) para máxima velocidad.

### 🧮 Suite de Herramientas Nutricionales
Cada perfil de paciente cuenta con un "Hub de Herramientas" personalizado:
* **Calculadora Energética:** Estimación del Gasto Energético Total usando fórmulas estándar (**Mifflin-St Jeor** y **Harris-Benedict**) con factores de actividad y estrés clínico. *El resultado se guarda automáticamente en el perfil del paciente.*
* **Distribución de Macros:** Herramienta interactiva con *sliders* para ajustar porcentajes de Carbohidratos, Proteínas y Grasas, calculando automáticamente gramos y equivalentes **SMAE**.
* **Calculadora Hídrica:** Determinación rápida de requerimientos de agua basada en peso y factor hídrico (ml/kg).
* **Protocolo ESPEN (UCI):** Lógica clínica avanzada para pacientes críticos, determinando objetivos calóricos según el IMC y aplicando guías para obesidad (Peso Real vs. Peso Ideal).

## 🛠️ Stack Tecnológico y Arquitectura

El proyecto sigue las guías de arquitectura moderna de Android, integrando ahora servicios en la nube.

| Componente | Tecnología/Librería | Propósito |
| :--- | :--- | :--- |
| **Backend / Auth** | **Firebase Authentication** | **Gestión de identidad, login, registro y recuperación de cuentas.** |
| UI | XML, Material 3 | Construcción de interfaces de usuario nativas y adaptables. |
| Lenguaje | Kotlin | Lenguaje principal, aprovechando corrutinas y flujos. |
| Arquitectura | MVVM (Model-View-ViewModel) | Separación de responsabilidades entre UI, lógica y datos. |
| Asincronía | Kotlin Coroutines & Flow | Gestión de operaciones en segundo plano y flujos de datos reactivos. |
| Base de Datos | Room | Persistencia local de expedientes de pacientes. |
| Navegación | Android Navigation Component | Gestión del flujo (Login -> Lista -> Detalle -> Herramientas). |

## Arquitectura en Detalle

### 1. Autenticación y Seguridad
* **LoginViewModel:** Gestiona la comunicación con **Firebase Auth**, manejando estados de carga, éxito y errores (ej. correo inválido, contraseña incorrecta).
* **Flujo de Navegación:** La app valida el estado de la sesión al inicio. Si no hay usuario autenticado, redirige al Login; de lo contrario, accede directamente a la lista de pacientes.

### 2. Base de Datos Local (Room)
* **Entidad Paciente:** Ahora incluye campos extendidos como `targetCalories` para persistir los cálculos realizados en la calculadora energética.
* **DAO Reactivo:** Expone `Flow<List<Patient>>` para que la lista de pacientes y los estados vacíos se actualicen en tiempo real.

### 3. Lógica de Negocio (Calculadoras)
* La lógica de cálculo (Macros, ESPEN, Energía) está desacoplada de la vista, permitiendo actualizaciones dinámicas en la UI sin bloquear el hilo principal.

## 🎯 Roadmap y Futuras Funcionalidades

Se han completado hitos importantes como el login y el módulo de cálculos. Los siguientes pasos son:

* **Sincronización en la Nube (Firestore):** Migrar la base de datos de Room a Firestore para permitir que el profesional acceda a sus pacientes desde múltiples dispositivos.
* **Mayor detalle en historia clínica:** Incorporar antecedentes patológicos, medidas antropométricas completas (pliegues cutáneos, circunferencias) y estudios de laboratorio.
* **Gráficas de Progreso:** Visualizar la evolución del peso y otras métricas del paciente a lo largo del tiempo.
* **Exportación de Datos:** Generar reportes en PDF con el diagnóstico y la distribución de macros calculada.
* **Inyección de Dependencias (Hilt):** Refactorizar para optimizar la gestión de dependencias.
