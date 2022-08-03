package vietdp.elasticsearch.entity;

import java.util.Date;

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
@Table(name = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerEntity {
	@Id
	@GeneratedValue(generator = "bigid")
	@GenericGenerator(name = "bigid", strategy = "vietdp.elasticsearch.util.IDGenerator")
	private Long id;
	@Column
	private String name;
	@Column
	private String address;
	@Column
	private Integer elasticstatus = 0;
	@Column
	private Date dob;

	public CustomerEntity(String name, String address,Date dob, Integer elasticstatus) {
		super();
		this.name = name;
		this.address = address;
		this.dob = dob;
		this.elasticstatus = elasticstatus;
	}

}