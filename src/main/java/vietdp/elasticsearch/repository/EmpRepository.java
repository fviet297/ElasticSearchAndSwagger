package vietdp.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import vietdp.elasticsearch.entity.Emp;
@Repository
public interface EmpRepository extends ElasticsearchRepository<Emp, String>{
  
  List<Emp> findByName(String name);
}
