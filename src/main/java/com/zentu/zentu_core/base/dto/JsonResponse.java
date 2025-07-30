package com.zentu.zentu_core.base.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Data
public class JsonResponse {
    private String code;
    private String message;

    @Getter(AccessLevel.NONE)
    private Map<String, Object> extraFields = new HashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonAnySetter
    public void addExtraField(String key, Object value) {
        if (!"code".equals(key) && !"message".equals(key)) {
            this.extraFields.put(key, value);
        }
    }

    public <T> T getAdditionalData(String key, TypeReference<T> type) {
        Object raw = extraFields.get(key);
        return mapper.convertValue(raw, type);
    }
}
