package com.mounir.learn.qverse.data;

import java.util.List;
import java.util.Map;

/** <b>Strategy</b> for test-data sources (JSON, Excel, and later DB, CSV, synthetic/AI generated data). */
public interface TestDataReader {

    boolean supports(String source);

    List<Map<String, Object>> readRows(String source, String sheet);
}
