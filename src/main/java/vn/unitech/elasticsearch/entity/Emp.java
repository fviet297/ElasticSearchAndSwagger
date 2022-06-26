package vn.unitech.elasticsearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Emp {
	@Id
	private String id;
	private String name;
	private String address;
	private String position;

	public Emp(String name, String address, String position) {
		super();
		this.name = name;
		this.address = address;
		this.position = position;
	}

}