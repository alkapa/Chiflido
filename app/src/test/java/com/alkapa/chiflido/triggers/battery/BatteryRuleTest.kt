package com.alkapa.chiflido.triggers.battery

import com.alkapa.chiflido.core.TriggerEvent
import com.alkapa.chiflido.core.TriggerSource
import com.alkapa.chiflido.data.BatterySettings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BatteryRuleTest {

    private fun rule(settings: BatterySettings) = BatteryRule { settings }

    private fun ev(level: Int, charging: Boolean = false) =
        TriggerEvent.Battery(level = level, charging = charging)

    @Test fun `cargando descarta`() = runTest {
        val r = rule(BatterySettings(thresholdPct = 20))
        assertNull(r.evaluate(ev(level = 5, charging = true)))
    }

    @Test fun `nivel sobre umbral descarta`() = runTest {
        val r = rule(BatterySettings(thresholdPct = 20))
        assertNull(r.evaluate(ev(level = 25)))
    }

    @Test fun `deshabilitado descarta`() = runTest {
        val r = rule(BatterySettings(thresholdPct = 20, enabled = false))
        assertNull(r.evaluate(ev(level = 5)))
    }

    @Test fun `nivel bajo dispara con severity 5`() = runTest {
        val r = rule(BatterySettings(thresholdPct = 20))
        val spec = r.evaluate(ev(level = 4))
        assertNotNull(spec)
        assertEquals(5, spec!!.severity)
        assertEquals(TriggerSource.BATTERY, spec.source)
    }

    @Test fun `severity por nivel`() {
        assertEquals(5, BatteryRule.severityFor(3))
        assertEquals(4, BatteryRule.severityFor(10))
        assertEquals(3, BatteryRule.severityFor(15))
        assertEquals(2, BatteryRule.severityFor(20))
        assertEquals(1, BatteryRule.severityFor(35))
    }

    @Test fun `umbral custom permite disparar a 30`() = runTest {
        val r = rule(BatterySettings(thresholdPct = 30))
        val spec = r.evaluate(ev(level = 28))
        assertNotNull(spec)
        // sev=1 porque 28 > 20, pero igual pasa el filtro de threshold
        assertEquals(1, spec!!.severity)
    }
}
