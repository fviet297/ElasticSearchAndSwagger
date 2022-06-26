package vn.unitech.elasticsearch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zzemployee")
public class EmpEntity {
	@Id
	@GeneratedValue(generator = "bigid")
	@GenericGenerator(name = "bigid", strategy = "vn.unitech.elasticsearch.IDGenerator")
	private Long id;
	@Column
	private String name;
	@Column
	private String address;
	@Column
	private String position;
	@Column
	private Integer elasticstatus = 0;

	public EmpEntity(String name, String address, String position, Integer elasticstatus) {
		super();
		this.name = name;
		this.address = address;
		this.position = position;
		this.elasticstatus = elasticstatus;
	}

}