# Backend en Java para Subastas Inmobiliarias - Real Estate Auctionator

**Categoría:** Software  
**Subcategoría:** Java Backend  
**Dificultad:** Alta

---

## 🌐 Mi Motivación y Contexto

Decidí afrontar este reto para simular un sistema de subastas inmobiliarias.Con este proyecto, quise demostrar mis habilidades desarrollando una API segura en Java. Implementé diversas funcionalidades que incluyen:  
- Autenticación y registro de usuarios mediante JWT.  
- Docker para contenizar la aplicación.
- Extracción (scraping) de datos de propiedades directamente desde Idealista.  
- Un sistema automático de solicitud de hipotecas basado en datos financieros del usuario.  
- Un sistema de subastas para propiedades utilizando RabbitMQ para gestionar las pujas concurrentes.

El objetivo principal fue evaluar mi capacidad para desarrollar APIs seguras, extraer datos externos, implementar reglas de negocio y manejar el procesamiento asíncrono de mensajes a través de colas.

---

## 📂 Estructura del Repositorio

Organizo el proyecto con la siguiente estructura base:

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
- **API Health Check (`GET http://localhost:3000/health`):**
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
      - **Tamaño:** El área de la propiedad (en m²).
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
    - Aprobar o rechazar la solicitud basándome en sí la cuota mensual supera el umbral permitido.
    - Si es aprobada, almaceno los detalles de la hipoteca en la base de datos.

- **Dashboard del usuario:**
     
    - Implemente el endpoint (`GET /api/user/dashboard`) que muestra los datos personales del usuario, incluyendo la información de empleo y las hipotecas asociadas.
    
- **Response:**  
    - Devuelve una respuesta JSON que indica si se aprobó la hipoteca, el pago mensual calculado y (si se aprobó) el ID de la hipoteca.

---

___

### Task 5: Sistema de Pujas para una propiedad.

**Objetivo:**
Implementar un sistema de subastas donde se puedan ofertar propiedades mediante pujas concurrentes utilizando RabbitMQ para gestionarlas.

- **Creación de la Subasta:**
   - **Endpoint: (`POST /api/auction/create`)**

   - **Funcionalidad:**
       Este endpoint permite crear una nueva subasta para una propiedad.
   
   Detalles que debe incluir la subasta:

   - Referencia de la propiedad (la propiedad debe existir en la base de datos).
   - Horas de inicio y fin (formato ISO 8601).
   - Precio de salida.
   - Incremento mínimo de la puja.
   - La puja máxima inicial se establece con el precio de salida.


- **Realización de Pujas**
  - **Endpoint: (`POST /api/auction/{auctionId}/bid`)**

  -**Funcionalidad:**
   Permite a los usuarios autenticados realizar pujas.

   Acciones al realizar una puja:

   - Verificar que la subasta exista y esté aún abierta.
   - Crear un mensaje de puja que contenga: ID de la subasta, ID del usuario, monto de la puja y la marca de tiempo.
   - Publicar el mensaje de puja en una cola de RabbitMQ.
   - El mensaje de puja se procesa de forma asíncrona mediante un consumidor, el cual actualiza la puja máxima actual de la subasta en caso de que sea necesario.

- **Detalles de la Subasta**
  - **Endpoint: (`GET /api/auction/{auctionId}`)**

  - **Funcionalidad:**
   Permite obtener los detalles de la subasta, incluyendo todas las pujas realizadas.

- **Cierre de la Subasta**

  - **Endpoint: (`PATCH /api/auction/{auctionId}/close`)**

  - **Funcionalidad:**
   Este endpoint cierra la subasta y realiza las siguientes acciones:

   - Cambia el estado de la subasta a "cerrada".
   - Procesa todas las pujas para determinar la puja ganadora (la puja más alta).
   - Actualiza la disponibilidad de la propiedad a "No disponible".
   - Retorna una respuesta en formato JSON que contenga el monto de la puja ganadora y el ID del usuario ganador.

**Concurrencia & RabbitMQ**

  - El sistema de pujas utiliza RabbitMQ para manejar grandes volúmenes de pujas de manera concurrente.
  - Las pujas se encolan y luego se procesan de forma asíncrona mediante un listener de mensajes, minimizando así las condiciones de carrera.
  - Se puede implementar un retraso en el consumidor (para fines de prueba) y observar cómo se acumulan los mensajes en la cola.

  - **Configuración en RabbitMQ:**

   - `bid.queue`
   - `bid.exchange`
   - `bid.routingkey`
___






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

| Field               | Type          | Null | Key | Default | Extra          |
|---------------------|---------------|------|-----|---------|----------------|
| id                  | bigint        | NO   | PRI | NULL    | auto_increment |
| current_highest_bid | decimal(10,2) | YES  |     | NULL    |                |
| end_time            | datetime(6)   | YES  |     | NULL    |                |
| min_increment       | decimal(10,2) | YES  |     | NULL    |                |
| start_time          | datetime(6)   | YES  |     | NULL    |                |
| starting_price      | decimal(10,2) | YES  |     | NULL    |                |
| status              | varchar(255)  | YES  |     | NULL    |                |
| property_id         | bigint        | NO   | UNI | NULL    |                |
| winning_user_id     | bigint        | YES  | UNI | NULL    |                |


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

| Field              | Type          | Null | Key | Default | Extra          |
|--------------------|---------------|------|-----|---------|----------------|
| id                 | bigint        | NO   | PRI | NULL    | auto_increment |
| monthly_payment    | decimal(10,2) | YES  |     | NULL    |                |
| number_of_months   | int           | NO   |     | NULL    |                |
| property_id        | bigint        | NO   | MUL | NULL    |                |
| user_id            | bigint        | NO   | MUL | NULL    |                |
| interestRate       | decimal(2,2)  | YES  | MUL | NULL    |                |
| allowed_percentage | double        | NO   | MUL | NULL    |                |
| approvalDate       | datetime      | NO   | MUL | NULL    |                |
| totalCost          | decimal(10,2) | NO   | MUL | NULL    |                |

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