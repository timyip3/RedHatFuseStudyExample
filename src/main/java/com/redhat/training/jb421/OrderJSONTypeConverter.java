package com.redhat.training.jb421;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import com.google.gson.Gson;
import com.redhat.training.jb421.model.Order;

@Converter
public class OrderJSONTypeConverter implements TypeConverters {
	
	@Converter
	public static String convertToJSON(Order order) {
		Gson gson = new Gson();
		
		return gson.toJson(order);
	}
}
