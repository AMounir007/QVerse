package com.mounir.learn.qverse.core.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Plug-in registry for AI capabilities (Java ServiceLoader). Third-party / in-house AI modules are
 * discovered from the classpath; built-in offline implementations are used as defaults.
 */
public final class AiExtensions {

    private static final List<LocatorHealer> HEALERS = loadHealers();
    private static final AiFailureAnalyzer ANALYZER =
            ServiceLoader.load(AiFailureAnalyzer.class).findFirst().orElseGet(RuleBasedFailureAnalyzer::new);
    private static final AiTestGenerator GENERATOR =
            ServiceLoader.load(AiTestGenerator.class).findFirst().orElseGet(TemplateTestGenerator::new);

    private AiExtensions() {
    }

    private static List<LocatorHealer> loadHealers() {
        List<LocatorHealer> list = new ArrayList<>();
        list.add(new FallbackLocatorHealer());
        ServiceLoader.load(LocatorHealer.class).forEach(list::add);
        return List.copyOf(list);
    }

    public static List<LocatorHealer> healers() {
        return HEALERS;
    }

    public static AiFailureAnalyzer failureAnalyzer() {
        return ANALYZER;
    }

    public static AiTestGenerator testGenerator() {
        return GENERATOR;
    }
}
