package vietdp.elasticsearch.elastic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ElasticAdapter {

	@Value("classpath:elastic/createIndexTemplate.json")
	private Resource resourceCreateIndexTemplate;
	
	@Value("classpath:elastic/createPropertyFloat.json")
	private Resource resourceCreatePropertyFloat;

	@Value("classpath:elastic/createPropertyFullText.json")
	private Resource resourceCreatePropertyFullText;

	@Value("classpath:elastic/createPropertyNumber.json")
	private Resource resourceCreatePropertyNumber;

	@Value("classpath:elastic/createPropertyText.json")
	private Resource resourceCreatePropertyText;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public JSONObject createIndex(String table) throws Exception {
		this.sendDeleteRq(table);
		JSONObject createIndexTemplate = new JSONObject(this.readFromResource(resourceCreateIndexTemplate));

		List<String> columns = this.getColumnName(table);
		
		for(String i:columns) {
			log.info("typeeeeeee " + this.getDatatytpe(table, i).toString());
			switch(this.getDatatytpe(table, i)) {
			case "bigint":
			case "integer":
			case "smallint":
			case "timestamp without time zone":
				JSONObject createPropertyNumber = new JSONObject(this.readFromResource(resourceCreatePropertyNumber));
				createIndexTemplate.getJSONObject("mappings").getJSONObject("properties").put(i,
						createPropertyNumber);
				break;
			case "numeric":
				JSONObject createPropertyFloat = new JSONObject(this.readFromResource(resourceCreatePropertyFloat));
				createIndexTemplate.getJSONObject("mappings").getJSONObject("properties").put(i, createPropertyFloat);
				break;
			case "character varying":
				JSONObject createPropertyText = new JSONObject(this.readFromResource(resourceCreatePropertyText));
				createIndexTemplate.getJSONObject("mappings").getJSONObject("properties").put(i, createPropertyText);
				break;
				default:
					break;
			}
			
		}
		JSONObject result = this.sendPutRq(table, createIndexTemplate.toString());
		this.insertData(table);
		return result;
	}

	public void insertData(String tableName) throws Exception {
		long id = 0;
		while (true) {
			List<Map<String, Object>> data = this.getDataTabble(tableName, id);
			if (data.size() == 0) {
				break;
			}
			String query = buidElasticInsert(tableName, data);
			Object iden = data.get(data.size() - 1).get("id");
			id = Long.parseLong(iden.toString());
//			log.info(query.formatted(JSONObject.class));
			sendPostRq("/_bulk", query.toString());
		}
	}
	
	
	public Map<String,Object> searchData(String index,String field,String keyword,int limit,int offset) throws Exception{
		Map<String,Object> result = new HashMap<>();
		JSONObject elasticQuery = this.createBoolQuery();
		
		elasticQuery.put("size", limit);
		elasticQuery.put("from", offset);
		
 		JSONObject resultElastic = this.search(index, field, keyword, elasticQuery);
		JSONObject dataResult = resultElastic.getJSONObject("hits");
		JSONArray hits = dataResult.getJSONArray("hits"); 
		List<Map<String,Object>> data = this.toArray(hits);
		
		elasticQuery.put("item", dataResult);
		result.put("items", data);
		return result;
		
	}
	public JSONObject search(String index, String field, String keyword,JSONObject query) throws Exception {
		String _search = "/" + index + "/_search"+"?q="+field+":"+keyword;
		log.info(_search);
		return this.sendPostRq(_search, query.toString());

	}

	public JSONObject sendDeleteRq(String url) throws IOException {
		String deleteUrl = "http://localhost:9200/" + url;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
			HttpDelete request = new HttpDelete(deleteUrl);
			CloseableHttpResponse res = httpClient.execute(request);
			return new JSONObject(EntityUtils.toString(res.getEntity(), "UTF-8"));
		} catch (Exception ex) {
			return null;
		} finally {
			httpClient.close();
		}
	}

	public JSONObject sendPostRq(String url, String data) throws Exception {
		String urlPost = "http://localhost:9200/" + url;
		CredentialsProvider provider = buidCredentials();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		try {
			HttpEntityEnclosingRequestBase request = buildRequest(urlPost, data, HttpMethod.POST);
			CloseableHttpResponse res = httpClient.execute(request);
			return new JSONObject(EntityUtils.toString(res.getEntity(), "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			httpClient.close();
		}
	}

	public JSONObject sendPutRq(String url, String data) throws Exception {
		String urlPut = "http://localhost:9200/" + url;
		CredentialsProvider provider = buidCredentials();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		try {
			HttpEntityEnclosingRequestBase request = buildRequest(urlPut, data, HttpMethod.PUT);
			log.info(request.toString());
			CloseableHttpResponse res = httpClient.execute(request);
			return new JSONObject(EntityUtils.toString(res.getEntity(), "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			httpClient.close();
		}
	}

	public String readFromResource(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			String result = FileCopyUtils.copyToString(reader);
			return result;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private HttpEntityEnclosingRequestBase buildRequest(String url, String data, HttpMethod method) {
		HttpEntityEnclosingRequestBase request = null;

		switch (method) {
		case POST:
			request = new HttpPost(url);
			break;
		case PUT:
			request = new HttpPut(url);
			break;
		default:
			break;
		}
		StringEntity params = new StringEntity(data, "UTF-8");

		request.addHeader("Content-Type", "application/json; charset=UTF-8");
		request.addHeader("Content-Encoding", "UTF-8");
		request.setEntity(params);

		return request;
	}

	private CredentialsProvider buidCredentials() {
		CredentialsProvider provider = new BasicCredentialsProvider();

		return provider;
	}

	public List<String> getColumnName(String tableName) {
		MapSqlParameterSource parrams = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(
				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> columns = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		log.info("COLUMNSSSS    "+columns);
		List<String> listColumns = new ArrayList<String>();
		for (Map<String, Object> i : columns) {
			for(Map.Entry<String,Object> entry:i.entrySet()){
				listColumns.add(entry.getValue().toString());
			}
		}
//		log.info(listColumns.toString());
		return listColumns;
	}
	
	public String getDatatytpe(String tableName,String columnName) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		String type =null ;
		sql.append("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME   = :tableName AND COLUMN_NAME  = :columnName");
		params.addValue("columnName", columnName);
		params.addValue("tableName", tableName);
		List<Map<String,Object>> types = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
		Map<String,Object> dataTypeMap = types.get(0);
		
		for(Map.Entry<String,Object> entry:dataTypeMap.entrySet()){
			type = (entry.getValue().toString());
		}
		
		log.info(type);
		return type;
	}

	public List<Map<String, Object>> getDataTabble(String tableName, long id) {
		MapSqlParameterSource parrams = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT * FROM " + tableName + " WHERE ID > " + id + " ORDER BY ID");
		List<Map<String, Object>> data = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return data;
	}

	public String buidElasticInsert(String tableName, List<Map<String, Object>> value) throws Exception {
		if (ObjectUtils.isEmpty(value)) {
			return "";
		}
		StringBuilder query = new StringBuilder();
		for (int i = 0; i < value.size(); i++) {
			JSONObject data = new JSONObject(value.get(i));
			// convert dob kiểu Date sang Long 			
			Long date = this.getTime(data.getString("dob"));
			data.put("dob",date);
			Long id = Long.valueOf(data.get("id").toString());
			
			query.append("{\"index\":{\"_index\":\"" + tableName + "\",\"_id\":\"" + id + "\"}}\n");
			query.append(data.toString()).append("\n");

		}
		return query.toString();

	}

	public Object getTable(String tableName) throws Exception {
		MapSqlParameterSource parrams = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> listTable = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return listTable.get(0);
	}
	
	public JSONObject createBoolQuery() throws JSONException {
		return new JSONObject("{\"query\": { \"bool\": { \"must\": [ { \"match_all\": { }}],\"must_not\": [ ],\"should\": [ ]}}, \"from\": 0, \"size\": 10, \"sort\": [ ], \"aggs\": { }}");
	}
//    public JSONObject createBoolQuery() throws JSONException {
//        return new JSONObject(
//                "{\"query\":{\"bool\":{\"must\":[{\"terms\":{\"daxoa\":[0]}},{\"terms\":{\"ishoanthanh\":[1]}}],\"should\":[]}},\"size\":0,\"aggs\":{\"types_count\":{\"value_count\":{\"field\":\"daxoa\"}}}}");
//    }

	
	public List<Map<String,Object>> toArray(JSONArray hits) throws Exception{
	
			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(int i = 0;i<hits.length();i++) {
			Map<String,Object> temp = new HashMap<String, Object>();
			
				temp.put("id",hits.getJSONObject(i).getLong("_id"));
				JSONObject object = hits.getJSONObject(i).getJSONObject("_source");
				Iterator<?> its = object.keys();
				for(Iterator<?> it = its;it.hasNext();) {
					String key = (String)it.next();
					if("id".equals(key)) {
						continue;
					}
					if(object.isNull(key)) {
						temp.put(key, null);
						continue;
					}
					temp.put(key, object.get(key));
					
				}
				result.add(temp);
				
			
		}
		return result;
	}
	
	public Long getTime(String strDate) {
		Long date = 0L;
		DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dutyDay = null;
		try {
			dutyDay = (java.util.Date) simpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		date = dutyDay.getTime();
		return date;
	}
}
