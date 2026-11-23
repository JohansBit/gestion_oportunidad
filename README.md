# Gestion Movil de Oportunidades y Activaciones Corporativas (DSY1105)
## 1. Nombre del Proyecto
**Gestion Movil de Oportunidades y Activaciones Corporativas**
Caso: DSY1105_003D - Evaluacion Parcial 4 (Asignatura: Desarrollo de Aplicaciones Moviles - DSY1105, Duoc UC).
## 2. Integrantes
 * **Nicolas Fernandez**
 * **Johans Sepulveda**
## 3. Funcionalidades
Aplicacion movil nativa desarrollada en Android utilizando Kotlin y Jetpack Compose bajo arquitectura MVVM:
 * **Autenticacion y Registro de Usuarios:** Inicio de sesion y creacion de cuentas con validacion de formato de correo, longitud minima de contrasena (6 caracteres) y aceptacion de terminos y condiciones.
 * **Control de Acceso Basado en Roles:** Diferenciacion de permisos y flujos en la interfaz segun el rol autenticado (SUPERVISOR o EJECUTIVO).
 * **Gestion CRUD de Oportunidades en Tiempo Real:** Listado reactivo (LazyColumn), visualizacion de detalle, creacion y edicion de oportunidades comerciales conectadas mediante Retrofit.
 * **Filtrado y Busqueda Dinamica:** Busqueda en tiempo real por texto y filtrado por nivel de prioridad (Todas, Alta, Media, Baja).
 * **Recurso Nativo (Camara y Escaner QR):** Lectura de codigos QR en tiempo de ejecucion mediante CameraX y ML Kit Barcode Scanning para asignar automaticamente la fuente o referencia de una oportunidad.
 * **Consumo de API Externa:** Integracion visual de datos obtenidos desde una API publica externa mediante Retrofit dentro de la interfaz movil.
 * **Pruebas Unitarias:** Cobertura logica igual o superior al 80% en capas ViewModel y Repository implementadas con JUnit y MockK.
## 4. Endpoints Utilizados
### 4.1 API de Microservicio Consumida desde Android (Retrofit)
URL Base configurada en cliente Retrofit (Emulador Android): http://10.0.2.2:8080
#### Modulo de Usuarios
 * POST /api/usuarios/registro
   Registra un nuevo usuario en el sistema asignandole por defecto el rol EJECUTIVO.
   Body JSON: {"nombre": "...", "correo": "...", "clave": "...", "direccion": "..."}
 * POST /api/usuarios/login
   Valida las credenciales del usuario y retorna su perfil junto con su rol.
   Body JSON: {"correo": "...", "clave": "..."}
 * GET /api/usuarios
   Obtiene el listado completo de usuarios registrados.
 * PUT /api/usuarios/{id}/rol?nuevoRol={ROL}&adminId={ID_SUPERVISOR}
   Actualiza el rol de un usuario existente cuando el usuario autenticado posee rol SUPERVISOR.
#### Modulo de Oportunidades
 * GET /api/oportunidades
   Retorna la lista completa de oportunidades comerciales registradas.
 * GET /api/oportunidades/{id}
   Consulta el detalle de una oportunidad especifica segun su identificador.
 * POST /api/oportunidades
   Crea una nueva oportunidad comercial enviando los datos ingresados en el formulario.
 * PUT /api/oportunidades/{id}?solicitanteId={ID_USUARIO}
   Actualiza los datos de una oportunidad existente enviando el identificador del usuario solicitante.
 * DELETE /api/oportunidades/{id}?solicitanteId={ID_USUARIO}
   Elimina una oportunidad comercial desde la interfaz cuando el usuario autenticado posee rol SUPERVISOR.
### 4.2 API Externa (Retrofit)
 * **Servicio Externo Consumido:** API REST publica de indicadores economicos / referencia comercial (GET via Retrofit).
   *(Nota: Reemplazar esta linea con la URL exacta y endpoint utilizado en la interfaz Retrofit externa de la app movil, por ejemplo: GET https://mindicador.cl/api o el servicio configurado en el proyecto).*
## 5. Pasos para Ejecutar en Android
 1. Abrir **Android Studio** (version Ladybug o superior recomendada) con el SDK de Android configurado.
 2. Clonar o abrir la carpeta raiz del proyecto movil (com.dsy1105.gestion_movil).
 3. Esperar a que Gradle sincronice todas las dependencias (Sync Project with Gradle Files).
 4. Asegurar que los servicios REST configurados en Retrofit esten accesibles desde el emulador o dispositivo fisico.
 5. Seleccionar un emulador Android (API 26 o superior) o un dispositivo fisico conectado por USB con depuracion habilitada.
 6. Presionar **Run (Shift + F10)** para compilar e instalar la aplicacion en el d
