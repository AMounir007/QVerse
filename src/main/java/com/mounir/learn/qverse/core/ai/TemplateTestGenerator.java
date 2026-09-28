package com.mounir.learn.qverse.core.ai;

import java.util.List;

/** Offline generator that converts manual steps into a ready-to-fill QVerse test skeleton. */
public final class TemplateTestGenerator implements AiTestGenerator {

    @Override
    public String generate(String testName, List<String> businessSteps) {
        String method = testName.replaceAll("[^A-Za-z0-9]+", " ").trim();
        StringBuilder sb = new StringBuilder()
                .append("@Test(description = \"").append(testName).append("\")\n")
                .append("public void ").append(toCamel(method)).append("() {\n");
        for (int i = 0; i < businessSteps.size(); i++) {
            sb.append("    // Step ").append(i + 1).append(": ").append(businessSteps.get(i)).append('\n');
        }
        return sb.append("}\n").toString();
    }

    private static String toCamel(String words) {
        String[] parts = words.split(" ");
        StringBuilder sb = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return sb.toString();
    }
}
