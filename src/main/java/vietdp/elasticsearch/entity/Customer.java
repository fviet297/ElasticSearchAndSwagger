package vietdp.elasticsearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Document(indexName = "")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
  @Id
  private String id;
  private String name;
  private String address;
  


public Customer(String name, String address) {
	super();
	this.name = name;
	this.address = address;
}
  
  
}