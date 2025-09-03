# Pruebas de Autenticación JWT

## 1. Registrar un nuevo usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "nombre": "Usuario Test",
    "password": "password123"
  }'
```

Respuesta esperada:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "test@example.com",
  "nombre": "Usuario Test"
}
```

## 2. Hacer login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 3. Acceder a un endpoint protegido

```bash
curl -X GET http://localhost:8080/api/movimientos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 4. Crear un usuario (endpoint público)

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuevo@example.com",
    "nombre": "Nuevo Usuario",
    "password": "password123"
  }'
```

## 5. Actualizar un usuario (requiere autenticación)

```bash
curl -X PUT http://localhost:8080/api/usuarios/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "email": "actualizado@example.com",
    "nombre": "Usuario Actualizado"
  }'
```

## Notas importantes:

1. Reemplaza `{id}` con el UUID real del usuario
2. Reemplaza `eyJhbGciOiJIUzI1NiJ9...` con el token JWT real obtenido del login/registro
3. El token tiene una duración de 24 horas
4. Las contraseñas se encriptan automáticamente con BCrypt

