package vietdp.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import vietdp.elasticsearch.entity.Customer;
@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer, String>{
  
  List<Customer> findByName(String name);
}
