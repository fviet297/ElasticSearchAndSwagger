package vn.unitech.elasticsearch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.unitech.elasticsearch.dto.CustomerDTO;
import vn.unitech.elasticsearch.entity.Customer;
import vn.unitech.elasticsearch.entity.CustomerEntity;
import vn.unitech.elasticsearch.repository.CustomerEntityRepository;
import vn.unitech.elasticsearch.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerEntityRepository customerEntityRepository;
	
	
	
	@PostMapping("/createEntity")
	public CustomerEntity createEntity(@RequestBody CustomerDTO customerDTO) throws Exception {
		CustomerEntity customer = new CustomerEntity(customerDTO.getName(), customerDTO.getAddress(),0);
//		createIndex(customerDTO);//insert elastic
		return customerEntityRepository.save(customer);
	}

	@PostMapping("/createIndex")
	public Customer createIndex(@RequestBody CustomerDTO customerDTO) throws Exception {
		Customer customer = new Customer(customerDTO.getName(), customerDTO.getAddress());
		return customerRepository.save(customer);
	}

	@DeleteMapping("/deleteIndex/{id}")
	public void deleteIndex(@PathVariable String id) {
		Optional<Customer> csCustomer = customerRepository.findById(id);
		if (csCustomer.isPresent()) {
			customerRepository.deleteById(id);
		}
	}

}
