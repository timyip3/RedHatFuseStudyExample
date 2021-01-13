package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;

import com.redhat.training.jb421.model.Order;

public class TransformRouteBuilder extends RouteBuilder {

	private static BindyCsvDataFormat csvDF = new BindyCsvDataFormat(Order.class);
	
	private static final String SRCURI = "";
	private static final String SRCPARAM = "include=order.*csv";
	
	@Override
	public void configure() throws Exception {
		from(SRCURI + SRCPARAM)
			.transform(body().regexReplaceAll(",", "`"))
			.unmarshal(csvDF)
		.to("direct:orderLog");
		
		from("direct:orderLog")
			.log("Order received before split: ${body}")
			.split(body())
			.log("order after split: ${body}")
		.to("mock:orderLoggingSystem");
		
		
	}

}
