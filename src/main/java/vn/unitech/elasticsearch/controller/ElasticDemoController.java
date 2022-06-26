package vn.unitech.elasticsearch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.unitech.elasticsearch.APIResult;
import vn.unitech.elasticsearch.elastic.ElasticAdapter;
@RestController
public class ElasticDemoController {
	@Autowired
    @Lazy  
    private ElasticAdapter elasticAdapter;

	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	@GetMapping("/syncData")
	public APIResult syncData(@RequestParam(required = true) String tableName) throws Exception {
		APIResult result = new APIResult();
		
		if(checkTableExist(tableName)) {
		elasticAdapter.createIndex(tableName);	
		}
		result.setMessage(checkTableExist(tableName)?"Đồng bộ dữ liệu Elastic bảng "+tableName+" thành công":"Bảng không tồn tại");
		return result;
	}
	
	public boolean checkTableExist(String tableName) throws Exception {
		MapSqlParameterSource parrams = new MapSqlParameterSource();		
		StringBuilder sql = new StringBuilder();	
		sql.append("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> listTable = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return listTable.isEmpty()?false:true;
	}
}
