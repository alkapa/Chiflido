# Chiflido

Sistema Android de **alertas persistentes**: vibra en bucle hasta que la
condición se descarte (notificación leída, cargador conectado, botón
"Silenciar"). Escrito en Kotlin con Jetpack Compose, 100 % offline.

## Triggers soportados

| Trigger     | Estado          | Cómo dispara                                       |
|-------------|-----------------|----------------------------------------------------|
| Mensajería  | implementado    | NotificationListenerService + match por contacto + clasificador heurístico |
| Batería baja| implementado    | BroadcastReceiver para `ACTION_BATTERY_LOW/OKAY/POWER_CONNECTED` |
| Pagos       | arquitectura    | Ver `app/src/main/java/com/alkapa/chiflido/triggers/scheduled/README.md` |

Toda la pipeline pasa por las abstracciones en `core/`:

```
[Trigger] → [TriggerEvent] → [AlertRule.evaluate] → [AlertEngine.fire / clear] → [AlertService + VibrationController]
```

Agregar una nueva categoría es: una `data class` en `TriggerEvent`, una
`AlertRule`, su productor (Receiver/Worker/listener) y la pantalla de
configuración. El resto del sistema no cambia.

## Build

Requisitos:
- Android Studio Hedgehog/Iguana (AGP 8.7+).
- JDK 17.
- Android SDK 34.

```
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Para instalar manualmente:
```
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Permisos

- `BIND_NOTIFICATION_LISTENER_SERVICE` — concedido desde
  `Settings > Notifications > Notification access` (la app abre el setting
  desde el banner de la pantalla principal).
- `POST_NOTIFICATIONS` (Android 13+) — pedido en runtime.
- `READ_CONTACTS` — pedido en runtime al usar el contact picker.
- `VIBRATE`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`,
  `RECEIVE_BOOT_COMPLETED` — declarados, sin prompt.

## Smoke manual

1. Abrir la app, conceder Notification Listener desde el banner.
2. Tab Mensajería → FAB → seleccionar contacto → afinar app/umbral/grupos.
3. Pedirle al contacto un mensaje con palabra urgente o "?" según umbral →
   vibra. Descartar la notif → vibración para. O usar el botón "Silenciar"
   en la notif persistente del foreground service.
4. Tab Batería → ajustar umbral, dejar enabled.
5. `adb shell dumpsys battery set level <N>` para forzar nivel; cuando el
   sistema dispara `BATTERY_LOW`, vibra. `adb shell dumpsys battery reset`
   o conectar cargador → vibración para.
6. Apagar el switch global del top bar → silencia todas las alertas activas
   y bloquea nuevas hasta encenderlo.

## Estructura

```
app/src/main/java/com/alkapa/chiflido/
├── ChiflidoApp.kt              ← Application, expone DB/settings/engine/monitor
├── MainActivity.kt
├── core/                       ← AlertId, TriggerEvent, AlertSpec, Rule, Engine
├── triggers/
│   ├── messaging/              ← NotifListener, MessageRule, TargetApp
│   ├── battery/                ← BatteryReceiver, BatteryMonitor, BatteryRule, BootReceiver
│   └── scheduled/              ← README con la receta para agregar pagos
├── classifier/                 ← MessageClassifier + HeuristicClassifier
├── service/                    ← AlertService (foreground) + VibrationController
├── data/                       ← Room (WatchedContact, BatterySettings) + DataStore
└── ui/                         ← Compose (home/messaging/battery/components/theme)
```

## Tests unitarios

```
./gradlew :app:testDebugUnitTest
```

- `HeuristicClassifierTest` — niveles, prioridad, acentos.
- `MessageRuleTest` — match contains ic, sufijo "(N mensajes)", grupos,
  score < umbral.
- `BatteryRuleTest` — charging/sobre-umbral/disabled descartan; severity
  por banda.
