package com.tommytao.a5steak.util;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class GsonManager {

	private static GsonManager instance;

	public static GsonManager getInstance() {

		if (instance == null)
			instance = new GsonManager();

		return instance;
	}

	private GsonManager() {
		// do nothing
	}

	// --
	
	public static class LocationSerializer implements JsonSerializer<Location> {

		@Override
		public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {

			JsonObject jObj = new JsonObject();
			jObj.add("latitude", context.serialize(location.getLatitude()));
			jObj.add("longitude", context.serialize(location.getLongitude()));

			return jObj;

		}

	}

	public static class BooleanAsIntAdapter extends TypeAdapter<Boolean> {

		@Override
		public void write(JsonWriter out, Boolean value) throws IOException {
			if (value == null) {
				out.nullValue();
			} else {
				out.value(value);
			}
		}

		@Override
		public Boolean read(JsonReader in) throws IOException {
			JsonToken peek = in.peek();
			switch (peek) {
			case BOOLEAN:
				return in.nextBoolean();
			case NULL:
				in.nextNull();
				return null;
			case NUMBER:
				return in.nextInt() != 0;
			case STRING:
				return Boolean.parseBoolean(in.nextString());
			default:
				throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
			}
		}

	}

	public static class LocationDeserializer implements JsonDeserializer<Location> {

		@Override
		public Location deserialize(JsonElement jElement, Type type, JsonDeserializationContext context) throws JsonParseException {
			double lat = Double.NaN;
			try {
				lat = jElement.getAsJsonObject().get("latitude").getAsDouble();
			} catch (Exception e) {
				e.printStackTrace();
			}

			double lng = Double.NaN;
			try {
				lng = jElement.getAsJsonObject().get("longitude").getAsDouble();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (Double.isNaN(lat) || Double.isNaN(lng))
				return null;

			Location location = new Location("");
			location.setLatitude(lat);
			location.setLongitude(lng);

			return location;
		}

	}

	private Gson defaultGson;

	public Gson getDefaultGson() {

		if (defaultGson == null) {

			BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();

			defaultGson = new GsonBuilder().registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
					.registerTypeAdapter(boolean.class, booleanAsIntAdapter).registerTypeAdapter(Location.class, new LocationSerializer())
					.registerTypeAdapter(Location.class, new LocationDeserializer())
					.serializeNulls()
					.create();


        }

		return defaultGson; 
		

	}

	private TypeAdapter<Boolean> booleanAsIntAdapter = null;



}
