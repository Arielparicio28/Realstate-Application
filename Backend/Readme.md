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
 