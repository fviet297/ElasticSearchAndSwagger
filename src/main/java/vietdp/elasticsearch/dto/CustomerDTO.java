package vietdp.elasticsearch.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CustomerDTO {
	private String name;
	private String address;
	private Integer elasticstatus = 0;
	private Date dob;

	public CustomerDTO(String name, String address,Date dob, Integer elasticstatus) {
		super();
		this.name = name;
		this.address = address;
		this.dob = dob;
		this.elasticstatus = elasticstatus;
	}

}