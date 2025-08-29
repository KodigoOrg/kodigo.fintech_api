# Finanzas App - Autenticación JWT

Esta aplicación Spring Boot incluye autenticación JWT para gestionar usuarios y sesiones de forma segura.

## Características

- Autenticación JWT
- Registro de usuarios
- Login de usuarios
- Protección de endpoints
- Encriptación de contraseñas con BCrypt

## Endpoints de Autenticación

### Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "usuario@ejemplo.com",
    "nombre": "Usuario Ejemplo",
    "password": "password123"
}
```

### Login de Usuario
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "usuario@ejemplo.com",
    "password": "password123"
}
```

### Respuesta de Autenticación
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "usuario@ejemplo.com",
    "nombre": "Usuario Ejemplo"
}
```

## Uso del Token JWT

Para acceder a endpoints protegidos, incluye el token en el header Authorization:

```http
GET /api/movimientos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Endpoints Públicos

- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Login de usuarios
- `POST /api/usuarios` - Crear usuario (sin autenticación)

## Endpoints Protegidos

Todos los demás endpoints requieren autenticación JWT válida.

## Configuración

El token JWT tiene una duración de 24 horas (86400000 ms) y se configura en `application.yml`:

```yaml
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000
```

## Estructura de la Base de Datos

La entidad Usuario incluye:
- `id` (UUID)
- `email` (String, único)
- `nombre` (String)
- `password` (String, encriptado)
- `role` (Enum: USER, ADMIN)
- `creadoEn` (LocalDateTime)

## Tecnologías Utilizadas

- Spring Boot 3.5.5
- Spring Security
- JWT (JSON Web Tokens)
- BCrypt para encriptación
- PostgreSQL
- JPA/Hibernate
