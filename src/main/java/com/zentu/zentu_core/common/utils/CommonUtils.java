package com.zentu.zentu_core.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonUtils {

    private CommonUtils() {
        // Prevent instantiation
    }

    public static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        if (list == null || chunkSize <= 0) {
            return Collections.emptyList();
        }

        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }
}
