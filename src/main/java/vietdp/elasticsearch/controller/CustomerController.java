package vietdp.elasticsearch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vietdp.elasticsearch.dto.CustomerDTO;
import vietdp.elasticsearch.entity.Customer;
import vietdp.elasticsearch.entity.CustomerEntity;
import vietdp.elasticsearch.repository.CustomerEntityRepository;
import vietdp.elasticsearch.repository.CustomerRepository;
//@Aspect
@RestController
@RequestMapping("/")
public class CustomerController {

	@Autowired
	CustomerRepository Repository;

	@Autowired
	CustomerEntityRepository EntityRepository;
	
	
//	@Before("execution(* vietdp.elasticsearch.controller.*.*(..))")
	@PostMapping("/createEntity")
	public CustomerEntity createEntity(@RequestBody CustomerDTO DTO) throws Exception {
		CustomerEntity cus = new CustomerEntity(DTO.getName(), DTO.getAddress(),DTO.getDob(),0);
//		createIndex(DTO);//insert elastic
		return EntityRepository.save(cus);
	}

	@PostMapping("/createIndex")
	public Customer createIndex(@RequestBody CustomerDTO DTO) throws Exception {
		Customer  cus = new Customer(DTO.getName(), DTO.getAddress());
		return Repository.save(cus);
	}

	@DeleteMapping("/deleteIndex/{id}")
	public void deleteIndex(@PathVariable String id) {
		Optional<Customer> csCustomer = Repository.findById(id);
		if (csCustomer.isPresent()) {
			Repository.deleteById(id);
		}
	}

}
