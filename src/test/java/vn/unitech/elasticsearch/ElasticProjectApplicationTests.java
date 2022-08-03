//package vn.unitech.elasticsearch;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//
//import lombok.extern.slf4j.Slf4j;
//import vietdp.elasticsearch.elastic.ElasticAdapter;
//
//@Slf4j
//@SpringBootTest(classes = ElasticAdapter.class)
//class ElasticProjectApplicationTests {
//	@Autowired
//	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//	private ModelMapper modelMapper = new ModelMapper();
//
//	@Test
//	void getColumnName() {
//		MapSqlParameterSource parrams = new MapSqlParameterSource();
//		String tableName = "";
//		StringBuilder sql = new StringBuilder();
//		sql.append(
//				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName");
//		parrams.addValue("tableName", tableName);
//		List<Map<String, Object>> columns = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
//		List<String> columsString = new ArrayList<>();
//		for (Map<String, Object> i : columns) {
//			columsString.add(i.values().toString());
//		}
//		log.info("LIST COLUMNS =====>>" + columsString.toString());
//	}
//
//	@Test
//	void getData() {
//		String tableName = "";
//		MapSqlParameterSource parrams = new MapSqlParameterSource();
//		StringBuilder sql = new StringBuilder();
//
//		sql.append("SELECT * FROM " + tableName);
//		log.info(sql.toString());
//		List<Map<String, Object>> data = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
//		System.out.println(data.toString());
//	}
//	@Test
//	public void getColumnNamec() {
//		String tableName = "";
//		MapSqlParameterSource parrams = new MapSqlParameterSource();
//		StringBuilder sql = new StringBuilder();
//
//		sql.append(
//				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
//		parrams.addValue("tableName", tableName);
//		List<Map<String, Object>> columns = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
//		List<String> listColumns = new ArrayList<String>();
//		for (Map<String, Object> i : columns) {
//			listColumns.add(i.values().toString());
//		}
//
//		System.out.println(listColumns.toString());
//	}
//}
