package com.mounir.learn.qverse.core.ai;

/**
 * AI-ready SPI for <b>failure analysis</b>. Default is rule based; an LLM-backed analyzer can read the
 * exception, logs and screenshot to explain the root cause in business terms.
 */
public interface AiFailureAnalyzer {

    FailureInsight analyze(Throwable failure, String executionLog);

    record FailureInsight(String category, String probableCause, String recommendation) {
        @Override
        public String toString() {
            return "Category       : " + category + System.lineSeparator()
                    + "Probable cause : " + probableCause + System.lineSeparator()
                    + "Recommendation : " + recommendation;
        }
    }
}
