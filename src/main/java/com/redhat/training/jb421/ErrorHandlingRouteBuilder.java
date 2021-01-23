package com.redhat.training.jb421;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class ErrorHandlingRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		errorHandler(
				deadLetterChannel("file:orders/problems")
				.disableRedelivery());
		
		onException(NumberFormatException.class)
			.handled(true)
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
			.setBody().constant("Data error finding shipping address with ID!");
		
		restConfiguration()
			.component("spark-rest")
			.port(8082);
		
		rest("/orders")
			.get("/shipAddress/{id}")
				.to("direct:shipAddress")
			.get("/orderTotal/{id}")
				.to("direct:orderTotal")
			.get("/itemList/{id}")
				.to("direct:itemList");
			
		from("direct:shipAddress")
			.log("shipAddress")
		.to("mock:shipAddressMock");
		
		from("direct:orderTotal")
			.log("orderTotal")
		.to("mock:orderTotalMock");
		
		from("direct:itemList")
			.log("itemList")
		.to("mock:itemListMock");
	}

}
