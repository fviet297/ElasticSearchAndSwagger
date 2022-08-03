package vietdp.elasticsearch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vietdp.elasticsearch.elastic.ElasticAdapter;
import vietdp.elasticsearch.util.APIResult;

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

		if (checkTableExist(tableName)) {
			elasticAdapter.createIndex(tableName);
		}
		result.setMessage(checkTableExist(tableName) ? "Đồng bộ dữ liệu Elastic bảng " + tableName + " thành công"
				: "Bảng không tồn tại");
		return result;
	}

	@GetMapping("/getColumn")
	public APIResult getColumn(String tableName) throws Exception {
		APIResult re = new APIResult();
		if (checkTableExist(tableName)) {
			re.setMessage("Thành công!");
			re.setData(elasticAdapter.getColumnName(tableName));

		} else {
			re.setMessage("Bảng không tồn tại");
		}
		System.out.println(re.getData());
		return re;
	}

	@GetMapping("/getDataType")
	public APIResult getDataType(String tableName, String columnName) throws Exception {
		APIResult re = new APIResult();
		if (checkTableExist(tableName)) {
			if (elasticAdapter.getColumnName(tableName).contains(columnName)) {
				re.setMessage("Thành công!");
				re.setData(elasticAdapter.getDatatytpe(tableName, columnName));
			} else {
				re.setMessage("Trường đã nhập không tồn tại");
			}
		} else {
			re.setMessage("Bảng đã nhập không tồn tại");
		}
		return re;
	}

	@GetMapping("/searchData")
	public APIResult searchData(@RequestParam(required = true) String index,
			@RequestParam(required = true) String field, @RequestParam(required = true) String keyword,
			@RequestParam(required = true) int limit, @RequestParam(required = true) int offset) throws Exception {
		APIResult result = new APIResult();

		Map<String, Object> items = elasticAdapter.searchData(index, field, keyword, limit, offset);
		result.setData(items);
		return result;
	}

	public boolean checkTableExist(String tableName) throws Exception {
		MapSqlParameterSource parrams = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> listTable = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return listTable.isEmpty() ? false : true;
	}
}
