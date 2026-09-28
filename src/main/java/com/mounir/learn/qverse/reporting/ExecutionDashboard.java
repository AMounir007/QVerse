package com.mounir.learn.qverse.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mounir.learn.qverse.config.ConfigManager;
import com.mounir.learn.qverse.core.logging.QVerseLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight execution dashboard (target/qverse-dashboard/index.html + summary.json).
 * Gives managers a one-glance KPI view without opening Allure, and a machine-readable
 * summary for CI quality gates, Slack/Teams notifications or AI trend analysis.
 */
public final class ExecutionDashboard {

    private static final QVerseLogger LOG = QVerseLogger.getLogger(ExecutionDashboard.class);
    private static final List<TestRecord> RECORDS = Collections.synchronizedList(new ArrayList<>());

    public record TestRecord(String name, String status, long durationMs, String failureCategory) {
    }

    private ExecutionDashboard() {
    }

    public static void record(String name, String status, long durationMs, String failureCategory) {
        RECORDS.add(new TestRecord(name, status, durationMs, failureCategory));
    }

    public static void publish() {
        List<TestRecord> snapshot;
        synchronized (RECORDS) {
            snapshot = List.copyOf(RECORDS);
        }
        long passed = count(snapshot, "PASSED");
        long failed = count(snapshot, "FAILED");
        long skipped = count(snapshot, "SKIPPED");
        long retried = count(snapshot, "RETRIED");
        long executed = passed + failed;
        double passRate = executed == 0 ? 0 : (passed * 100.0) / executed;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("generatedAt", LocalDateTime.now().toString());
        summary.put("environment", ConfigManager.config().env());
        summary.put("total", snapshot.size());
        summary.put("passed", passed);
        summary.put("failed", failed);
        summary.put("skipped", skipped);
        summary.put("retried", retried);
        summary.put("passRate", Math.round(passRate * 100) / 100.0);
        summary.put("tests", snapshot);

        Path dir = Path.of("target", "qverse-dashboard");
        try {
            Files.createDirectories(dir);
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValue(dir.resolve("summary.json").toFile(), summary);
            Files.writeString(dir.resolve("index.html"), html(snapshot, passed, failed, skipped, retried, passRate));
            LOG.info("QVerse dashboard: {} passed, {} failed, {} skipped, pass rate {}% -> {}",
                    passed, failed, skipped, String.format("%.1f", passRate), dir.toAbsolutePath());
        } catch (IOException e) {
            LOG.warn("Dashboard could not be written: {}", e.getMessage());
        }
    }

    private static long count(List<TestRecord> records, String status) {
        return records.stream().filter(r -> r.status().equals(status)).count();
    }

    private static String html(List<TestRecord> records, long p, long f, long s, long r, double rate) {
        StringBuilder rows = new StringBuilder();
        for (TestRecord t : records) {
            rows.append("<tr class='").append(t.status()).append("'><td>").append(escape(t.name()))
                    .append("</td><td>").append(t.status()).append("</td><td>").append(t.durationMs())
                    .append(" ms</td><td>").append(t.failureCategory() == null ? "" : t.failureCategory())
                    .append("</td></tr>");
        }
        return """
                <!DOCTYPE html><html><head><meta charset="utf-8"><title>QVerse Execution Dashboard</title>
                <style>
                 body{font-family:Segoe UI,Arial;margin:32px;background:#0f172a;color:#e2e8f0}
                 .kpi{display:inline-block;padding:16px 24px;margin:8px;border-radius:12px;background:#1e293b;min-width:120px}
                 .kpi b{display:block;font-size:28px}
                 table{border-collapse:collapse;width:100%%;margin-top:24px}
                 td,th{padding:8px;border-bottom:1px solid #334155;text-align:left}
                 .PASSED td:nth-child(2){color:#22c55e}.FAILED td:nth-child(2){color:#ef4444}
                 .SKIPPED td:nth-child(2),.RETRIED td:nth-child(2){color:#eab308}
                </style></head><body>
                <h1>QVerse Execution Dashboard</h1>
                <div class="kpi">Pass rate<b>%.1f%%</b></div><div class="kpi">Passed<b>%d</b></div>
                <div class="kpi">Failed<b>%d</b></div><div class="kpi">Skipped<b>%d</b></div>
                <div class="kpi">Retried<b>%d</b></div>
                <table><tr><th>Test</th><th>Status</th><th>Duration</th><th>Failure category</th></tr>%s</table>
                <p>Full details: run <code>mvn allure:serve</code></p></body></html>
                """.formatted(rate, p, f, s, r, rows);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
