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
				.when(method("approvalPredicate", "isFromOrlyCompany"))
					.to("file:orders/output/Orly")
				.when(method("approvalPredicate", "isLessThan100"))
					.to("file:orders/output/Less100")
				.when(method("approvalPredicate", "isGreaterThan100"))
					.to("file:orders/output/Greater100")
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
