package com.zentu.zentu_core.base.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JsonResponse {
    private String code;
    private String message;
    private Map<String, Object> extraFields = new HashMap<>();

    @JsonAnySetter
    public void addExtraField(String key, Object value) {
        if (!"code".equals(key) && !"message".equals(key)) {
            this.extraFields.put(key, value);
        }
    }
}
