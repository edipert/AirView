package com.android.airview.util

import com.android.airview.api.model.Flight
import com.android.airview.api.model.State
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

// Created a custom deserializer for json object. Since states array is heterogeneous array Gson couldn't parse to kotlin objects
class FlightDeserializer : JsonDeserializer<Flight> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Flight {
        val jsonObject = json?.asJsonObject
        val flight = Flight()

        jsonObject?.let {
            if (it.has("time")) {
                flight.time = it["time"].asLong
            }

            if (it.has("states") && it["states"].isJsonArray) {
                it["states"].asJsonArray.forEach { item ->
                    item.asJsonArray.let { list ->
                        flight.states.add(
                            State(
                                if (list[0].isJsonNull) null else list[0].asString,
                                if (list[1].isJsonNull) null else list[1].asString,
                                if (list[2].isJsonNull) null else list[2].asString,
                                if (list[3].isJsonNull) null else list[3].asInt,
                                if (list[4].isJsonNull) null else list[4].asInt,
                                if (list[5].isJsonNull) null else list[5].asDouble,
                                if (list[6].isJsonNull) null else list[6].asDouble,
                                if (list[7].isJsonNull) null else list[7].asFloat,
                                if (list[8].isJsonNull) null else list[8].asBoolean,
                                if (list[9].isJsonNull) null else list[9].asFloat,
                                if (list[10].isJsonNull) null else list[10].asFloat,
                                if (list[11].isJsonNull) null else list[11].asFloat,
                                if (list[12].isJsonNull) null else listOf(),
                                if (list[13].isJsonNull) null else list[13].asFloat,
                                if (list[14].isJsonNull) null else list[14].asString,
                                if (list[15].isJsonNull) null else list[15].asBoolean,
                                if (list[16].isJsonNull) null else list[16].asInt
                            )
                        )
                    }
                }
            }
        }

        return flight
    }
}