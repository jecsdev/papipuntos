# Papi Puntos

Aplicación móvil multiplataforma para parejas que convierte tareas y gestos cotidianos en puntos. Cada persona usa su propio perfil protegido con PIN, reclama acciones y la otra persona las aprueba o rechaza antes de que afecten el saldo.

> El nombre de producto cambiará a **Usify** más adelante. Por ahora, el paquete y los módulos conservan `papipuntos`.

## Qué incluye hoy

- Perfiles de pareja con PIN, sesión local y cambio de perfil.
- Registro e inicio de sesión local; inicio de sesión con Google mediante Supabase Auth.
- Acciones pendientes: quien reclama no puede aprobar su propia acción.
- Aprobación o rechazo con motivo obligatorio y detalle visible en el historial.
- Saldo soberano por perfil: solo las acciones aprobadas suman puntos.
- Catálogo de recompensas local y canje inmediato contra el saldo del perfil activo.
- Perfil con puntos, racha y logros derivados de datos persistidos.
- Persistencia local con Room KMP y contraseñas/PIN protegidos con PBKDF2-HMAC-SHA256 y salt aleatorio.
- UI compartida con Compose Multiplatform para Android e iOS.

La sincronización de acciones, canjes y perfiles con Supabase todavía no está implementada. En esta etapa esos datos viven localmente en Room.

## Regla central del producto

Cada perfil es soberano:

```text
saldo del perfil = puntos de sus acciones aprobadas − costo de sus propios canjes
```

Una acción creada por Papi queda pendiente para Mami, y viceversa. Nadie puede aprobar su propia solicitud ni canjear puntos del otro perfil. Consulta las reglas completas en [LOCAL_BUSINESS_RULES.md](LOCAL_BUSINESS_RULES.md).

## Stack

- Kotlin Multiplatform + Compose Multiplatform
- Material 3 y Compose Resources
- Room KMP + SQLite bundled
- Koin para inyección de dependencias
- Coroutines y StateFlow
- Supabase Auth (`supabase-kt`) para OAuth web con Google
- KSP para generación de Room
- GitHub Actions, ktlint y detekt

## Arquitectura

```text
core/
  model/          Entidades puras y estados compartidos
  domain/         Repositorios, casos de uso y reglas de negocio
  data/           Room, hashing, Auth y adaptadores de persistencia
  designsystem/   Theme, componentes e i18n de Compose Resources

feature/
  login/          Cuenta, PIN y perfiles
  addaction/      Reclamar una acción
  approvals/      Aprobar o rechazar solicitudes pendientes
  scoreboard/     Marcador e historial
  rewards/        Catálogo y canje de recompensas
  profile/        Perfil, racha, logros y planes

shared/           Router Compose y composición de módulos Koin
androidApp/       Entrada Android
iosApp/           Entrada SwiftUI/iOS
```

La dirección de dependencias para flujos de negocio es:

```text
Repository → Use case → ViewModel → UI → Tests
```

## Requisitos

- JDK 17
- Android Studio reciente y Android SDK
- Para ejecutar iOS: macOS con Xcode

Windows y Linux pueden compilar el target Kotlin/Native de iOS, pero no ejecutar el simulador de Apple.

## Configuración local

1. Clona el repositorio y abre la carpeta raíz del proyecto:

   ```bash
   cd papipuntos
   ```

2. Opcionalmente, configura Supabase para habilitar Google OAuth:

   ```bash
   cp secrets.properties.example secrets.properties
   ```

   En PowerShell:

   ```powershell
   Copy-Item secrets.properties.example secrets.properties
   ```

3. Completa `secrets.properties` con los valores del dashboard de Supabase:

   ```properties
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.anonKey=TU-ANON-KEY
   ```

`secrets.properties` está ignorado por Git. Nunca agregues una `service_role` key: esa clave no pertenece a una app cliente. Si no creas el archivo, el proyecto igual compila; simplemente el inicio de sesión remoto no podrá conectarse.

## Ejecutar y verificar

| Propósito | macOS/Linux | Windows PowerShell |
| --- | --- | --- |
| Compilar Android | `./gradlew :androidApp:assembleDebug` | `.\gradlew.bat :androidApp:assembleDebug` |
| Ejecutar todos los tests host | `./gradlew allTests` | `.\gradlew.bat allTests` |
| Compilar iOS común | `./gradlew compileKotlinIosSimulatorArm64` | `.\gradlew.bat compileKotlinIosSimulatorArm64` |

Para ejecutar Android, usa la configuración `androidApp` desde Android Studio o instala el APK generado. Para ejecutar iOS, abre `iosApp/` con Xcode en una Mac.

## Calidad y CI

Cada push y pull request ejecuta:

- Build Android y tests host.
- Compilación del target iOS en un runner macOS.
- ktlint para formato Kotlin.
- detekt para code smells.

Antes de abrir un PR, ejecuta al menos:

```bash
./gradlew assembleDebug allTests
./gradlew compileKotlinIosSimulatorArm64
```

## Estado y roadmap

- [x] UI Compose Multiplatform.
- [x] Auth local, perfiles con PIN y Google OAuth.
- [x] Acciones, aprobaciones, historial, canjes, logros y persistencia local.
- [ ] Sincronización remota con Supabase y resolución de conflictos.
- [ ] Sign in with Apple (requiere Apple Developer Program).
- [ ] Distribución iOS mediante TestFlight.
