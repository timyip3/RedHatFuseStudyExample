package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;

public class TransformRouteBuilder extends RouteBuilder {

	@SuppressWarnings("deprecation")
	private static XmlJsonDataFormat xmlJsonDf = new XmlJsonDataFormat();
	
	private static final String SRCURI = "";
	private static final String SRCPARAM = "include=order.*xml";
	
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		from(SRCURI + SRCPARAM)
			//.marshal().jaxb()
			.log("XML Body: ${body}")
			.marshal(xmlJsonDf)
			.log("JSON Body: ${body}")
			.filter().jsonpath("$[?(@.delivered !='true')]")
			.wireTap("direct:jsonOrderLog")
		.to("mock:fufillmentSystem");
		
		from("direct:jsonOrderLog")
			.log("Order received: ${body}")
		.to("mock:orderLog");
		
		
	}

}
