package org.example;

import io.avaje.mason.JsonType;
import io.avaje.mason.Jsonb;
import io.avaje.mason.base.Types;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapTest {

  @Test
  void simpleMap() throws IOException {

    Jsonb jsonb = Jsonb.newBuilder().build();

    ParameterizedType mapTy = Types.newParameterizedType(Map.class, String.class, Integer.class);
    JsonType<Map<String,Integer>> adapter = jsonb.type(mapTy);

    Map<String,Integer> data = new LinkedHashMap<>();
    data.put("one", 11);
    data.put("two", 12);

    String asJson = adapter.toJson(data);
    assertThat(asJson).isEqualTo("{\"one\":11,\"two\":12}");

    Map<String, Integer> mapFromJson = adapter.fromJson(asJson);
    assertThat(mapFromJson).containsOnlyKeys("one", "two");
    assertThat(mapFromJson.get("one")).isEqualTo(11);
    assertThat(mapFromJson.get("two")).isEqualTo(12);

  }
}
