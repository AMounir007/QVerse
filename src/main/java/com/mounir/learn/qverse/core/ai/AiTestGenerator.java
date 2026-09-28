package com.mounir.learn.qverse.core.ai;

import java.util.List;

/**
 * AI-ready SPI for <b>test generation</b>: turns business steps (manual test case, user story)
 * into QVerse DSL code. Default implementation produces a template; an LLM implementation
 * can be registered via ServiceLoader.
 */
public interface AiTestGenerator {

    String generate(String testName, List<String> businessSteps);
}
