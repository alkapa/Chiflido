# Trigger de alertas programadas (FUTURO)

No implementado en el MVP. Esta carpeta queda como placeholder con la guía
para agregar recordatorios de pagos / vencimientos sin tocar el resto del
sistema.

## Pasos para implementarlo

1. **Entidad Room** `ScheduledRule` en `data/`:
   ```kotlin
   @Entity(tableName = "scheduled_rules")
   data class ScheduledRule(
       @PrimaryKey(autoGenerate = true) val id: Long = 0,
       val label: String,                  // "Pago internet"
       val dayOfMonth: Int,                // 1..31; -1 si usamos cron
       val severity: Int,                  // 1..5
       val enabled: Boolean = true,
   )
   ```
   Agregarla al `@Database` y subir la versión.

2. **Variante en `TriggerEvent`**:
   ```kotlin
   data class Scheduled(
       override val alertId: AlertId,      // AlertId.forScheduled(id)
       val ruleId: Long,
       val label: String,
   ) : TriggerEvent
   ```

3. **`ScheduledRule` en `core/AlertRule.kt`** (nueva implementación).
   Por ejemplo, severity tomado directo de la regla.

4. **Worker** `ScheduledTriggerWorker` (WorkManager) o
   `AlarmManager.setExactAndAllowWhileIdle` registrado por cada regla:
   - En `doWork()` carga la regla, construye `TriggerEvent.Scheduled`,
     evalúa la regla, llama `engine.fire(id, spec)`.
   - Re-encola la siguiente ejecución (próximo mes).

5. **UI**: nueva pantalla `ScheduledTab` con CRUD; reemplaza el placeholder
   "próximamente" en `HomeScreen`.

6. **Permisos extra**: `SCHEDULE_EXACT_ALARM` (Android 12+, requiere prompt
   en runtime para la mayoría de devices).

7. **Cancelación**: la alerta se descarta cuando el usuario marca la regla
   como pagada (botón en la notif del foreground service) → emite
   `engine.clear(AlertId.forScheduled(id))`.

Toda la integración con AlertEngine, AlertService y VibrationController se
mantiene exactamente igual: el pipeline genérico ya está preparado.
