package com.tommytao.a5steak.gson;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.tommytao.a5steak.common.Foundation;

public class GsonManager extends Foundation{

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

	@Deprecated
	public boolean init(Context context) {
		return super.init(context);
	}

	@Deprecated
	public boolean init(Context context, RequestQueue requestQueue) {
		return super.init(context, requestQueue);
	}

	@Deprecated
	public boolean isInitialized() {
		return super.isInitialized();
	}

	@Override
	public Gson getDefaultGson() {
		return super.getDefaultGson();
	}



//	public static class LocationSerializer implements JsonSerializer<Location> {
//
//		@Override
//		public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
//
//			JsonObject jObj = new JsonObject();
//			jObj.add("latitude", context.serialize(location.getLatitude()));
//			jObj.add("longitude", context.serialize(location.getLongitude()));
//
//			return jObj;
//
//		}
//
//	}
//
//	public static class BooleanAsIntAdapter extends TypeAdapter<Boolean> {
//
//		@Override
//		public void write(JsonWriter out, Boolean value) throws IOException {
//			if (value == null) {
//				out.nullValue();
//			} else {
//				out.value(value);
//			}
//		}
//
//		@Override
//		public Boolean read(JsonReader in) throws IOException {
//			JsonToken peek = in.peek();
//			switch (peek) {
//			case BOOLEAN:
//				return in.nextBoolean();
//			case NULL:
//				in.nextNull();
//				return null;
//			case NUMBER:
//				return in.nextInt() != 0;
//			case STRING:
//				return Boolean.parseBoolean(in.nextString());
//			default:
//				throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
//			}
//		}
//
//	}
//
//	public static class LocationDeserializer implements JsonDeserializer<Location> {
//
//		@Override
//		public Location deserialize(JsonElement jElement, Type type, JsonDeserializationContext context) throws JsonParseException {
//			double lat = Double.NaN;
//			try {
//				lat = jElement.getAsJsonObject().get("latitude").getAsDouble();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			double lng = Double.NaN;
//			try {
//				lng = jElement.getAsJsonObject().get("longitude").getAsDouble();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			if (Double.isNaN(lat) || Double.isNaN(lng))
//				return null;
//
//			Location location = new Location("");
//			location.setLatitude(lat);
//			location.setLongitude(lng);
//
//			return location;
//		}
//
//	}
//
//	private Gson defaultGson;
//
//	public Gson getDefaultGson() {
//
//		if (defaultGson == null) {
//
//			BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
//
//			defaultGson = new GsonBuilder().registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
//					.registerTypeAdapter(boolean.class, booleanAsIntAdapter).registerTypeAdapter(Location.class, new LocationSerializer())
//					.registerTypeAdapter(Location.class, new LocationDeserializer())
//					.serializeNulls()
//					.create();
//
//
//        }
//
//		return defaultGson;
//
//
//	}






}
