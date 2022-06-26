package vn.unitech.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import vn.unitech.elasticsearch.entity.Emp;
@Repository
public interface EmpRepository extends ElasticsearchRepository<Emp, String>{
  
  List<Emp> findByName(String name);
}
