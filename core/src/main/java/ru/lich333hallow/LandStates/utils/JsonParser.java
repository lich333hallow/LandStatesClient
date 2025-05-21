package ru.lich333hallow.LandStates.utils;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

public class JsonParser {
    private static final Json json = new Json();
    private static final JsonReader reader = new JsonReader();

    static {
        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);
    }

    /**
     * Сериализация объекта в JSON строку
     */
    public static String toJson(Object object) {
        try {
            return json.toJson(object);
        } catch (SerializationException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Десериализация JSON строки в объект
     */
    public static <T> T fromJson(String jsonString, Class<T> type) {
        try {
            return json.fromJson(type, jsonString);
        } catch (SerializationException e) {
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    /**
     * Парсинг JSON строки в JsonValue (дерево значений)
     */
    public static JsonValue parse(String jsonString) {
        try {
            return reader.parse(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("JSON parsing failed", e);
        }
    }

    /**
     * Создание нового Json объекта
     */
    public static JsonValue newJsonObject() {
        return new JsonValue(JsonValue.ValueType.object);
    }

    /**
     * Создание нового Json массива
     */
    public static JsonValue newJsonArray() {
        return new JsonValue(JsonValue.ValueType.array);
    }

    /**
     * Добавление поля в Json объект
     */
    public static void addField(JsonValue jsonObject, String name, Object value) {
        if (value instanceof String) {
            jsonObject.addChild(name, new JsonValue((String) value));
        } else if (value instanceof Integer) {
            jsonObject.addChild(name, new JsonValue((int) value));
        } else if (value instanceof Float) {
            jsonObject.addChild(name, new JsonValue((float) value));
        } else if (value instanceof Boolean) {
            jsonObject.addChild(name, new JsonValue((boolean) value));
        } else if (value instanceof JsonValue) {
            jsonObject.addChild(name, (JsonValue) value);
        } else {
            jsonObject.addChild(name, parse(toJson(value)));
        }
    }
}
