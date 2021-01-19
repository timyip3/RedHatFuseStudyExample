package com.redhat.training.camel.error;

import java.util.concurrent.TimeUnit;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ErrorHandlingTest extends CamelSpringTestSupport {

	@EndpointInject(uri = "mock:MA")
	private MockEndpoint mockMa;
	
	@EndpointInject(uri = "mock:NA")
	private MockEndpoint mockNa;
	
	@Produce(uri = "file:orders/incoming")
	private ProducerTemplate fileOrders;
	
	private String maStateContent = "<order>\n" +
			" <orderId>1</orderId>\n" +
			" <orderDate>2016-12-10T12:01:00-05:00</orderDate>\n" +
			" <totalInvoice>123</totalInvoice>\n" +
			"<customer><shippingAddress><state>MA</state></shippingAddress>\n" + 
			"</customer>\n" +
			" </order>";
	
	private String noStateContent = "<order>\n" +
			" <orderId>2</orderId>\n" +
			" <orderDate>2018-12-10T12:01:00-05:00</orderDate>\n" +
			" <totalInvoice>NA</totalInvoice>\n" +
			"<customer><shippingAddress><state></state></shippingAddress>\n" + 
			"</customer>\n" +
			" </order>";
	
	@Override
	public boolean isUseAdviceWith() {
		return true;
	}
	
	@Before
	public void before() throws Exception {
		AdviceWithRouteBuilder adviceWithRouteBuilder = new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("file:orders/output/MA")
				.skipSendToOriginalEndpoint()
				.to(mockMa);
			}
		};
		
		AdviceWithRouteBuilder adviceWithRouteBuilderNA = new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("file:orders/trash")
				.skipSendToOriginalEndpoint()
				.to(mockNa);
			}
		};
		
		context.getRouteDefinition("process")
		.adviceWith(context, adviceWithRouteBuilder);
		context.getRouteDefinition("auditing")
		.adviceWith(context, adviceWithRouteBuilderNA);
		context.start();
	}

	@After
	public void after() throws Exception {
		context.stop();
		assertMockEndpointsSatisfied();
	}
	
	@Test
	public void testFileWithNATotalInvoiceRoute() throws Exception {
		NotifyBuilder notifyBuilder = new NotifyBuilder(context).whenDone(1).create();
		notifyBuilder.matches(2, TimeUnit.SECONDS);
		
		fileOrders.sendBodyAndHeader(noStateContent, Exchange.FILE_NAME, "file_na.xml");
		mockNa.expectedMessageCount(1);
		mockNa.expectedHeaderReceived("totalInvoice", "NA");
	}

	@Test
	public void testFileWithMARoute() throws Exception {
		NotifyBuilder notifyBuilder = new NotifyBuilder(context).whenDone(1).create();
		notifyBuilder.matches(2, TimeUnit.SECONDS);
		
		fileOrders.sendBodyAndHeader(maStateContent, Exchange.FILE_NAME, "file.xml");
		mockMa.expectedMessageCount(1);
		mockMa.expectedHeaderReceived("totalInvoice", "123");
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("spring/camel-context.xml");
	}

}
