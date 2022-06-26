package vn.unitech.elasticsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.unitech.elasticsearch.entity.CustomerEntity;
@Repository
public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, String>{
  
  List<CustomerEntity> findByName(String name);
}
