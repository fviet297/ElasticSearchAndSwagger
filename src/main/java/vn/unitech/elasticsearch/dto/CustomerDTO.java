package vn.unitech.elasticsearch.dto;

import lombok.Data;

@Data
public class CustomerDTO {
	private String name;
	private String address;
	private Integer elasticstatus = 0;

	public CustomerDTO(String name, String address, Integer elasticstatus) {
		super();
		this.name = name;
		this.address = address;
		this.elasticstatus = elasticstatus;
	}

}