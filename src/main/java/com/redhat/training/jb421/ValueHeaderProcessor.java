package com.redhat.training.jb421;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.xml.XPathBuilder;

public class ValueHeaderProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String bodyStr = exchange.getIn().getBody(String.class);
		
		String totalInvoice = XPathBuilder.xpath("/order/totalInvoice/text()")
				.evaluate(exchange.getContext(), bodyStr);
		exchange.getIn().setHeader("totalInvoice", Integer.parseInt(totalInvoice));
	}

}
