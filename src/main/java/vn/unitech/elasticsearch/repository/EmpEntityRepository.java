package vn.unitech.elasticsearch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.unitech.elasticsearch.entity.EmpEntity;
@Repository
public interface EmpEntityRepository extends JpaRepository<EmpEntity, Integer>{
  
  List<EmpEntity> findByName(String name);
  
  Optional<EmpEntity> findById(Integer id);
}
