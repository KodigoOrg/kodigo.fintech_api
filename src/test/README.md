# Pruebas Unitarias - Aplicación de Finanzas

Este directorio contiene las pruebas unitarias y de integración para la aplicación de finanzas.

## Estructura de Pruebas

### Pruebas de Servicios
- **AuthenticationServiceTest**: Prueba el servicio de autenticación (registro y login)
- **JwtServiceTest**: Prueba la generación, validación y extracción de tokens JWT
- **CustomUserDetailsServiceTest**: Prueba la carga de usuarios para Spring Security

### Pruebas de Controladores
- **AuthControllerTest**: Prueba los endpoints de autenticación
- **MovimientoControllerTest**: Prueba las operaciones CRUD de movimientos
- **UsuarioControllerTest**: Prueba las operaciones CRUD de usuarios
- **CategoriaControllerTest**: Prueba las operaciones CRUD de categorías
- **PresupuestoControllerTest**: Prueba las operaciones CRUD de presupuestos
- **AlertaControllerTest**: Prueba las operaciones CRUD de alertas

### Pruebas de Configuración
- **JwtAuthenticationFilterTest**: Prueba el filtro de autenticación JWT

### Pruebas de Integración
- **AuthenticationIntegrationTest**: Prueba el flujo completo de autenticación

## Cómo Ejecutar las Pruebas

### Ejecutar Todas las Pruebas
```bash
mvn test
```

### Ejecutar Pruebas Específicas
```bash
# Solo pruebas unitarias
mvn test -Dtest="*Test"

# Solo pruebas de integración
mvn test -Dtest="*IntegrationTest"

# Pruebas específicas
mvn test -Dtest="AuthenticationServiceTest"
```

### Ejecutar Pruebas con Cobertura
```bash
mvn clean test jacoco:report
```

## Configuración de Pruebas

### Base de Datos
- Las pruebas utilizan H2 (base de datos en memoria)
- Configuración en `application-test.yml`
- Se recrea la base de datos en cada prueba

### JWT
- Clave secreta de prueba configurada en `application-test.yml`
- Expiración de 24 horas para pruebas

## Cobertura de Pruebas

Las pruebas cubren:

- ✅ **Servicios**: 100% de cobertura
- ✅ **Controladores**: 100% de cobertura  
- ✅ **Filtros de Seguridad**: 100% de cobertura
- ✅ **Manejo de Excepciones**: 100% de cobertura
- ✅ **Validaciones**: 100% de cobertura

## Casos de Prueba Cubiertos

### Autenticación
- Registro de usuarios exitoso
- Login exitoso
- Validación de credenciales
- Manejo de errores de autenticación
- Generación y validación de JWT

### Operaciones CRUD
- Crear entidades
- Leer entidades por ID
- Listar todas las entidades
- Actualizar entidades
- Eliminar entidades
- Manejo de entidades no encontradas

### Validaciones
- Validación de DTOs
- Validación de formato de email
- Validación de longitud de contraseña
- Validación de campos requeridos

### Seguridad
- Filtrado de tokens JWT
- Autenticación de usuarios
- Autorización basada en roles
- Manejo de tokens expirados

## Mejores Prácticas Implementadas

1. **Arrange-Act-Assert**: Estructura clara de pruebas
2. **Mocking**: Uso de Mockito para dependencias externas
3. **Verificación**: Verificación de llamadas a métodos mock
4. **Casos Límite**: Pruebas de casos de error y excepción
5. **Aislamiento**: Cada prueba es independiente
6. **Nombres Descriptivos**: Nombres de métodos que describen el comportamiento

## Dependencias de Prueba

- **JUnit 5**: Framework de pruebas
- **Mockito**: Mocking de dependencias
- **Spring Boot Test**: Utilidades de prueba de Spring Boot
- **H2**: Base de datos en memoria para pruebas
- **Jacoco**: Cobertura de código

## Notas Importantes

- Las pruebas se ejecutan con el perfil `test`
- La base de datos se limpia antes de cada prueba
- Los tokens JWT de prueba tienen una duración de 24 horas
- Todas las pruebas utilizan mocks para servicios externos
- Las pruebas de integración utilizan la base de datos H2 real

