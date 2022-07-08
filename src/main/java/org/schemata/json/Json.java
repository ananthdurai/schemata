package org.schemata.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;


public final class Json {

  public static Boolean containsField(JsonObject obj, String element) {
    return obj.has(element) && !obj.get(element).isJsonNull();
  }

  public static String getAsString(JsonObject obj, String element) {
    return getAsStringCaseSensitive(obj, element).toLowerCase();
  }

  public static String getAsStringCaseSensitive(JsonObject obj, String element) {
    return containsField(obj, element) ? obj.get(element).getAsString() : "";
  }

  public static Long getAsLong(JsonObject obj, String element) {
    return NumberUtils.isParsable(getAsString(obj, element)) ?
        obj.get(element).getAsLong() : Long.MIN_VALUE;
  }

  public static boolean getAsBoolean(JsonObject obj, String element) {
    return BooleanUtils.toBoolean(getAsString(obj, element));
  }

  public static Optional<JsonObject> getAsJsonObject(JsonObject obj, String element) {
    return containsField(obj, element) && obj.isJsonObject() ?
        Optional.of(obj.get(element).getAsJsonObject()) : Optional.empty();
  }

  public static Optional<JsonArray> getAsJsonArray(JsonObject obj, String element) {
    return containsField(obj, element) ?
        Optional.of(obj.getAsJsonArray(element)) : Optional.empty();
  }
}
