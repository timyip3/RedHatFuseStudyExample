package com.redhat.training.jb421;

import org.apache.camel.builder.RouteBuilder;

public class ErrorHandlingRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		errorHandler(
				deadLetterChannel("file:orders/problems")
				.disableRedelivery());
		
		from("file:orders/incoming")
			.routeId("process")
			.choice()
				.when(xpath("/order/customer/shippingAddress/state/text() = 'AK'"))
					.to("file:orders/output/AK")
				.when(xpath("/order/customer/shippingAddress/state/text() = 'MA'"))
					.to("file:orders/output/MA")
				.otherwise()
		.to("direct:auditing");
		
		from("direct:auditing")
			.routeId("auditing")
			.doTry()
				.process(new ValueHeaderProcessor())
				.to("file:orders/root/dest")
			.doCatch(NumberFormatException.class)
				.to("file:orders/trash")
			.endDoTry();
	}

}
