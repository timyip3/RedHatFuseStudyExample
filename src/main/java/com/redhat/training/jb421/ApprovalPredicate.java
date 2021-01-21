package com.redhat.training.jb421;

import java.math.BigDecimal;

import org.apache.camel.language.XPath;

public class ApprovalPredicate {

	private static final BigDecimal HUNDRED = new BigDecimal("100");
	
	public boolean isFromOrlyCompany(@XPath("/order/company") String company, 
			@XPath("/order/totalInvoice") String totalInvoice) {
		return "Orly".equalsIgnoreCase(company)  && 
				new BigDecimal(totalInvoice).compareTo(BigDecimal.ZERO ) > 0; 
	}
	 
	public boolean isLessThan100(@XPath("/order/totalInvoice") String totalInvoice){
		return new BigDecimal(totalInvoice).compareTo(HUNDRED) < 0  && 
				new BigDecimal(totalInvoice).compareTo(BigDecimal.ZERO ) > 0;  
	}
	 
	public boolean isGreaterThan100(@XPath("/order/totalInvoice") String totalInvoice) {
		return new BigDecimal(totalInvoice).compareTo(HUNDRED) >= 0;  
	}

}
