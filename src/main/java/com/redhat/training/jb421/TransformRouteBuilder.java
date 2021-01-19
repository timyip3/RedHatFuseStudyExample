package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;

import com.redhat.training.jb421.model.Order;

public class TransformRouteBuilder extends RouteBuilder {

	private static BindyCsvDataFormat csvDF = new BindyCsvDataFormat(Order.class);
	
	private static final String SRCURI = "file:orders/incoming?";
	private static final String SRCPARAM = "include=order.*csv";
	
	@Override
	public void configure() throws Exception {
		from(SRCURI + SRCPARAM)
			.transform(body().regexReplaceAll(",", "`"))
			.unmarshal(csvDF)
		.to("direct:orderLog");
		
		from("direct:orderLog")
			.split(body())
		.to("direct:updateDB");
		
		from("direct:updateDB")
		.to("sql: select * from test_data")
		.to("direct:DBResult");
		
		from("direct:DBResult")
			.split(body())
			.log("DBResult: ${body}")
		.to("mock:DBResultEnd");
	}

}
