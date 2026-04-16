# PawCare 

## 🐾 Sistema de Gestión PawCare
---
### 📌 1. Introducción

---

**PawCare** es una aplicación móvil nativa desarrollada en Kotlin para **Android**, diseñada para optimizar la gestión de peluquerías caninas y centros de cuidado animal.

### El sistema permite administrar de forma eficiente:
- Empleados
- Clientes (dueños)
- Mascotas
- Inventario de productos
- Agenda de citas
- Facturación con generación de recibos en PDF

## 🏗️ 2. Arquitectura del Proyecto
---
La aplicación implementa Clean Architecture (Arquitectura Limpia), separando la lógica de negocio de la interfaz y la persistencia de datos.

### Se divide en tres capas principales:

### 🔹 A. Capa de Dominio (Domain)

Es el núcleo del sistema. Contiene la lógica pura sin dependencias de Android.
Modelos:
- **Objetos de negocio como:**
 `Pet`, `Owner`, `Appointment`, `Payment`, `Service`, `Product`.

- **Repositorios (Interfaces):**
 Definen qué datos se pueden obtener, sin especificar cómo.

- **Casos de Uso (Use Cases):** 
 Clases que ejecutan acciones específicas, por ejemplo:
```kotlin
 GetAppointmentsUseCase.
```

### 🔹 B. Capa de Datos (Data)

Responsable de la persistencia y comunicación externa.

- **API (Retrofit):**
 PawCareApiService define los endpoints REST consumidos desde Railway.
 
- **Base de Datos Local (Room):**
 PawCareDatabase maneja SQLite con enfoque Offline-First.
 
- **Mappers:**
  - *Transforman datos entre:*
    - DTO (red)
    - Entities (local)
    - Modelos de dominio
  - *Repositorios (Implementación):*
    - Utilizan el patrón networkBoundResource para:
    - Mostrar datos locales primero
    - Sincronizar con la red en segundo plano

### 🔹 C. Capa de Presentación (Presentation)

Interfaz construida completamente con Jetpack Compose.
- *Patrón MVI (Model-View-Intent):*
    - State: Estado de la UI
    - Event: Acciones del usuario
    - Effect: Navegación y mensajes
- Navegación:
    - NavGraph centraliza rutas y argumentos entre pantallas
---
## ⚙️ 3. Funcionalidades
### 🛡️ Módulo de Seguridad y Empleados
- **Login por perfil:**
  - Selección de empleado (ej. Ana, María, Carlos).

  - Cada acción queda asociada al empleado act

---

### 🏠 Dashboard Inteligente
**Estadísticas en tiempo real:**

  - **Uso de StateFlow para mostrar:**
    
    - Citas pendientes

    - Citas completadas del día
      
  - **Diálogo de cobro:**

    - Filtra citas pendientes para evitar errores al facturar.

---

### 🐕 Gestión de Mascotas y Dueños
- **Registro dual:**
  - Se crea el dueño en la API
  - Se registra la mascota con su ID asociado
  
- **Búsqueda reactiva:**
  - **Filtrado por nombre o raza en:**
    - Base local
    - Datos remotos

---

### 💰 Módulo de Cobros (Billing)
- **Procesamiento de pagos:**
  - Tarjeta
  - Efectivo
  - Validación de campos
- **Generación de recibos PDF:**
  
   Uso de PdfDocument para crear comprobantes digitales
- **Historial de cobros:**
  - Filtros por método de pago
  - Contadores dinámicos

---


