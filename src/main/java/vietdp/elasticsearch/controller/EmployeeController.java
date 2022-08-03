package vietdp.elasticsearch.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vietdp.elasticsearch.dto.EmpDTO;
import vietdp.elasticsearch.entity.Emp;
import vietdp.elasticsearch.entity.EmpEntity;
import vietdp.elasticsearch.repository.EmpEntityRepository;
import vietdp.elasticsearch.repository.EmpRepository;

@RestController
@RequestMapping("/")
public class EmployeeController {
	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	EmpRepository empRepository;

	@Autowired
	EmpEntityRepository empEntityRepository;

	@PostMapping("/createEntity")
	public EmpEntity createEntity(@RequestBody EmpDTO empDTO) {
		EmpEntity emp = new EmpEntity(empDTO.getName(), empDTO.getAddress(), empDTO.getPosition(), 0);
		return empEntityRepository.save(emp);
	}

	@PostMapping("/createIndex")
	public Emp createIndex(@RequestBody EmpDTO empDTO) {
		Emp emp = new Emp(empDTO.getName(), empDTO.getAddress(), empDTO.getPosition());
		return empRepository.save(emp);
	}

	@PostMapping("/insertData/{id}")
	public void insertData(@PathVariable Integer id) {
		Optional<EmpEntity> empEntity = empEntityRepository.findById(id);
		if (empEntity.isPresent()) {

			List<EmpDTO> listEmps = empEntity.stream().map(emp -> modelMapper.map(emp, EmpDTO.class))
					.collect(Collectors.toList());
			listEmps.forEach(emp -> {
				if (emp.getElasticstatus().equals(0)) {
					this.createIndex(emp);
				}
			});
			if (empEntity.get().getElasticstatus().equals(0)) {
			empEntity.get().setElasticstatus(1);
			empEntityRepository.save(empEntity.get());
			}
		}
	}

	@DeleteMapping("/deleteIndex/{id}")
	public void deleteIndex(@PathVariable String id) {
		Optional<Emp> csemp = empRepository.findById(id);
		if (csemp.isPresent()) {
			empRepository.deleteById(id);
		}
	}

}
