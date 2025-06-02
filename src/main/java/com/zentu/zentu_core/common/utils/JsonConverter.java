package com.zentu.zentu_core.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import java.util.Map;

public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
