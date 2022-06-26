package vn.unitech.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpDTO {
  private Integer id;
  private String name;
  private String address;
  private String position;
  private Integer elasticstatus = 0;

	public EmpDTO(String name, String address, String position,Integer elasticstatus) {
		super();
		this.name = name;
		this.address = address;
		this.position = position;
		this.elasticstatus = elasticstatus;
	}
  
  
}