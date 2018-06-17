package org.dddjava.jig.presentation.view.poi.report;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportRow {

    List<String> list;

    private ReportRow(List<String> list) {
        this.list = list;
    }

    public List<String> list() {
        return list;
    }

    public static ReportRow of(ItemConverter[] items, Function<ItemConverter, String> itemToString) {
        return new ReportRow(Arrays.stream(items).map(itemToString).collect(Collectors.toList()));
    }
}
