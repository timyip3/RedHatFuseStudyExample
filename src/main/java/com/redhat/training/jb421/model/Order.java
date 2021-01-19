package com.redhat.training.jb421.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@CsvRecord(separator="`")
public class Order implements Serializable {

	private static final long serialVersionUID = -1989792853277610375L;

	 @DataField(pos=1)  
	 private Integer id;
	 @DataField(pos=2, pattern="MM-dd-yyyy")   
	 private Date orderDate = new Date();  
	 @DataField(pos=3, pattern="##.##")   
	 private BigDecimal discount;  
	 @DataField(pos=4)  
	 private Boolean delivered=false;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public Boolean getDelivered() {
		return delivered;
	}
	public void setDelivered(Boolean delivered) {
		this.delivered = delivered;
	}

}
