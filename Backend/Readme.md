# 🏦 Mi Desafío Backend en Java para Subastas Inmobiliarias - Real Estate Auctionator 🏡

**Categoría:** Software  
**Subcategoría:** Java Backend  
**Dificultad:** Alta

---

## 🌐 Mi Motivación y Contexto

Decidí afrontar este reto para simular un sistema de subastas inmobiliarias para CaixaBank. Con este proyecto, quise demostrar mis habilidades desarrollando una API segura en Java. Implementé diversas funcionalidades que incluyen:  
- Autenticación y registro de usuarios mediante JWT.  
- Extracción (scraping) de datos de propiedades directamente desde Idealista.  
- Un sistema automático de solicitud de hipotecas basado en datos financieros del usuario.  
- Un sistema de subastas para propiedades utilizando RabbitMQ para gestionar las pujas concurrentes.

El objetivo principal fue evaluar mi capacidad para desarrollar APIs seguras, extraer datos externos, implementar reglas de negocio y manejar el procesamiento asíncrono de mensajes a través de colas.

Para más detalles, también leí detenidamente el archivo [RealEstate_App_info](https://cdn.nuwe.io/challenge-asset-files/CB-Round3/RealEstate_App_Info.pdf).

---

## 📂 Estructura del Repositorio

Organizo el proyecto con una estructura base, que aunque puede variar según la solución final, sigue la siguiente referencia:

```bash
caixabank-backend-java-realestate
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── round3
│       │           └── realestate
│       │               ├── config           # Configuraciones de Spring Security, RabbitMQ y otras
│       │               ├── controller       # Endpoints de la API REST   
│       │               ├── dtos             # Data Transafer Object para manipular los datos a mi BD.
│       │               ├── entity           # Entidades JPA
│       │               ├── enums            # Enums creado para manejar algunas entradas en mi BD.
│       │               ├── exceptions       # Clases con excepciones globales de la aplicación.
│       │               ├── messaging        # Clases para publicar/consumir mensajes (BidMessage, BidPublisher, etc.)
│       │               ├── payload          # Clases para request y response
│       │               ├── repository       # Repositorios de Spring Data
│       │               ├── response         # Login and Register Response.
│       │               └── security         # Implementaciones de JWT y UserDetails
│       │               ├── services         # Services con toda la lógica de los endpoints de la applicación.
│       │               ├── utils         # Clases utiles para realizar algunos cálculos en la app.
│       └── resources
│           ├── application.properties         # Configuración de la aplicación
│           └── ...
├── Dockerfile                                 # Instrucciones de build para Docker
├── docker-compose.yml
├── README.md
└── ...
```

---

## 🎯 Tareas y Objetivos

1. **Task 1: Dockerfile & Health Check.**  
2. **Task 2: Login y Registro con JWT.**  
3. **Task 3: Obtención de datos de propiedades desde Idealista.**  
4. **Task 4: Sistema automático de solicitud de hipoteca**
5. **Task 5: Sistema de pujas para propiedades**  
---

### Task 1: Dockerfile & Health Check.

En esta primera tarea me aseguré de que el entorno Docker estuviera correctamente configurado. Implementé un endpoint de health check para validar que la API estuviera activa y que la conexión a la base de datos se realizara sin inconvenientes.

Para probar esto, levanto el entorno ejecutando:

```bash
docker-compose up --build
```
---

### Task 2: Login y Registro con JWT
**Objetivo:**  
Implementé endpoints seguros para el registro y autenticación de usuarios usando JSON Web Tokens (JWT).

- **Registro (`POST /api/auth/register`):**
    - Recibo datos del usuario (nombre de usuario, email y contraseña).
    - Valido que el email sea único.
    - Encripto la contraseña (usando BCrypt) antes de almacenarla.
    - Los nuevos usuarios se registran con el estado `unemployed` por defecto.
    - Devuelvo un mensaje de éxito al finalizar el registro.

- **Login (`POST /api/auth/login`):**
    - Verifico las credenciales del usuario (username/email and password).
    - Si son correctas, genero un JWT que incluye, al menos, el ID del usuario.
    - El token tiene una fecha de expiración definida en `application properties`.
    - El JWT se retorna en la respuesta. Ejemplo: `{ "success": true, "token": "<JWT_TOKEN>" }`.

- **Chequeo de sesión (GET /api/user/me):**

    - Permite verificar que el token funciona correctamente devolviendo el ID, nombre de usuario y email.
    - Implementé filtros de seguridad para proteger los endpoints que requieren autenticación.

---
### Task 3 Extracción de Datos de Idealista
**Objetivo:**
Desarrollé un endpoint que extrae detalles de una propiedad desde Idealista a partir de una URL proporcionada. Ejemplo: (`https://www.idealista.com/inmueble/106898591/`).

- **Endpoint (`POST /api/scrape`):**  

    - Recibo un JSON con:
      - `"url"`: La URL de la propiedad en Idealista.
      - `"store"`: Un flag booleano que indica si debo almacenar la propiedad en la base de datos.
  
- **Data Extraction:**  
    - Utilicé Jsoup para conectarme a la URL y extraer:
      - **Tipo de propiedad:** extraído del título la primera palabra del título(ejemplo: "Chalet", "Apartamento"). Se guarda como "name" en la BD.
      - **Título Completo:** El título completo de la propiedad que aparece en la web.
      - **Ubicación:** La ubicación de la propiedad.
      - **Precio:** El precio, asegurándose de que se convierta a un formato numérico estándar (decimal).
      - **Tamaño:** El area de la propiedad (en m²).
      - **Habitaciones:** The number of rooms, extracted from text like "4 hab.".
    - Si los datos de alguno de estos campos no están disponibles, (se configuran en una cadena vacía o en un valor predeterminado).

- **Almacenamiento:**  
    - Si `store` es true, creo un registro de la propiedad en la base de datos, asignándole por defecto el estado `Available`.

    
- **Respuesta:**  
    - Devuelvo un JSON con los datos extraídos y un indicador de si la propiedad fue guardada o no.

---

### Task 4: Sistema Automático de Solicitud de Hipoteca

**Objetivo:**
Implementé un sistema que evalúa automáticamente si un usuario califica para una hipoteca basándose en su información financiera.

- **Datos de empleo:**  
  - **Endpoint (`POST /api/employment`):** 
 Para que los usuarios autenticados envíen o actualicen sus datos de empleo, tales como:

- **Tipo de Contrato**: Valores como `indefinite`, `temporary` o `NULL` (por defecto en el registro). (Lógica en la carpeta enums, `enum ContractType` )
-**Salario**: El salario bruto anual.

Calculo el salario neto mensual utilizando los tramos impositivos vigentes en España y actualizo el estado de empleo del usuario. (Se encuentra el cálculo en la `carpeta utils` en la clase llamada `TaxCalculator`)

- **Solicitud de hipoteca:**
  - **Endpoint (`POST /api/mortgage`):** 
  Para que el usuario solicite una hipoteca:

    - Verifico que el usuario tenga empleo.
    - Calculo el coste total de la propiedad (precio + 15% extras).
    - Utilizo el salario neto mensual y el tipo de contrato para determinar el umbral permitido (30% para indefinido, 15% para temporal).
    - Empleo la fórmula estándar de amortización para calcular la cuota mensual.
    - Aprobar o rechazar la solicitud basándome en si la cuota mensual supera el umbral permitido.
    - Si es aprobada, almaceno los detalles de la hipoteca en la base de datos.

- **Dashboard del usuario:**
     
    - Implemente el endpoint (`GET /api/user/dashboard`) que muestra los datos personales del usuario, incluyendo la información de empleo y las hipotecas asociadas.
    
- **Response:**  
    - Devuelve una respuesta JSON que indica si se aprobó la hipoteca, el pago mensual calculado y (si se aprobó) el ID de la hipoteca.

---

# Estructura de la Base de Datos – realestate

A continuación, se muestra la lista de tablas.

---

## Listado de Tablas

- **auctions**
- **bids**
- **employment**
- **mortgages**
- **properties**
- **users**
- **revoked_token**

---

## Detalle de las Tablas

### 1. Tabla: auctions

| Field                | Type          | Null | Key | Default | Extra          |
|----------------------|---------------|------|-----|---------|----------------|
| id                   | bigint        | NO   | PRI | NULL    | auto_increment |
| current_highest_bid  | decimal(10,2) | YES  |     | NULL    |                |
| end_time             | datetime(6)   | YES  |     | NULL    |                |
| min_increment        | decimal(10,2) | YES  |     | NULL    |                |
| start_time           | datetime(6)   | YES  |     | NULL    |                |
| starting_price       | decimal(10,2) | YES  |     | NULL    |                |
| status               | varchar(255)  | YES  |     | NULL    |                |
| property_id          | bigint        | NO   | UNI | NULL    |                |

---

### 2. Tabla: bids

| Field      | Type          | Null | Key | Default | Extra          |
|------------|---------------|------|-----|---------|----------------|
| id         | bigint        | NO   | PRI | NULL    | auto_increment |
| bid_amount | decimal(10,2) | YES  |     | NULL    |                |
| timestamp  | datetime(6)   | YES  |     | NULL    |                |
| auction_id | bigint        | NO   | MUL | NULL    |                |
| user_id    | bigint        | NO   | MUL | NULL    |                |

---

### 3. Tabla: employment

| Field             | Type          | Null | Key | Default | Extra          |
|-------------------|---------------|------|-----|---------|----------------|
| id                | bigint        | NO   | PRI | NULL    | auto_increment |
| contract          | Enum          | YES  |     | NULL    |                |
| employment_status | varchar(255)  | YES  |     | NULL    |                |
| net_monthly       | decimal(10,2) | YES  |     | NULL    |                |
| salary            | decimal(10,2) | YES  |     | NULL    |                |
| user_id           | bigint        | NO   | UNI | NULL    |                |

---

### 4. Tabla: mortgages

| Field            | Type          | Null | Key | Default | Extra          |
|------------------|---------------|------|-----|---------|----------------|
| id               | bigint        | NO   | PRI | NULL    | auto_increment |
| monthly_payment  | decimal(10,2) | YES  |     | NULL    |                |
| number_of_months | int           | NO   |     | NULL    |                |
| property_id      | bigint        | NO   | MUL | NULL    |                |
| user_id          | bigint        | NO   | MUL | NULL    |                |

---

### 5. Tabla: properties

| Field        | Type          | Null | Key | Default | Extra          |
|--------------|---------------|------|-----|---------|----------------|
| id           | bigint        | NO   | PRI | NULL    | auto_increment |
| availability | varchar(255)  | YES  |     | NULL    |                |
| location     | varchar(255)  | YES  |     | NULL    |                |
| name         | varchar(255)  | YES  |     | NULL    |                |
| price        | decimal(38,2) | YES  |     | NULL    |                |
| rooms        | varchar(255)  | YES  |     | NULL    |                |
| size         | varchar(255)  | YES  |     | NULL    |                |

---

### 6. Tabla: users

| Field    | Type         | Null | Key | Default | Extra          |
|----------|--------------|------|-----|---------|----------------|
| id       | bigint       | NO   | PRI | NULL    | auto_increment |
| email    | varchar(255) | NO   | UNI | NULL    |                |
| password | varchar(255) | NO   |     | NULL    |                |
| username | varchar(255) | NO   | UNI | NULL    |                |
| status   | Enum         | NO   | UNI | NULL    |                |

___

---

### 7. Tabla: revoked_tokens

| Field           | Type         | Null | Key | Default | Extra          |
|-----------------|--------------|------|-----|---------|----------------|
| id              | bigint       | NO   | PRI | NULL    | auto_increment |
| expiration_date | datetime(6)  | NO   | UNI | NULL    |                |
| token           | varchar(500) | NO   |     | NULL    |                |
| username        | varchar(255) | NO   | UNI | NULL    |                |

___