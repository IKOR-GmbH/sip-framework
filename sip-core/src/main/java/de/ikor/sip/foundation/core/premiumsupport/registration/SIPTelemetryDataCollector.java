package de.ikor.sip.foundation.core.premiumsupport.registration;

/** Collects telemetry data of an adapter instance. */
public interface SIPTelemetryDataCollector {
    /**
     * Collect current(dynamic) telemetry data of this application.
     *
     * @return the collected data as {@link TelemetryData}
     */
    TelemetryData collectData();
}
