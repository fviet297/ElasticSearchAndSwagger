package vietdp.elasticsearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vietdp.elasticsearch.entity.CustomerEntity;

@Repository
public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, String>{
  
  List<CustomerEntity> findByName(String name);
}
