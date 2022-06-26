package vn.unitech.elasticsearch.elastic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public JSONObject createIndex(String table) throws Exception {
		this.sendDeleteRq(table);
		JSONObject createIndexTemplate = new JSONObject(this.readFromResource(resourceCreateIndexTemplate));
		JSONObject result = this.sendPutRq(table, createIndexTemplate.toString());
		this.insertData(table);
		return result;
	}
	
	public void insertData(String tableName) throws Exception{
		long id = 0;
		while(true) {
			List<Map<String,Object>> data = this.getDataTabble(tableName,id);
			if (data.size() == 0) {
				break;
			}			
			String query = buidElasticInsert(tableName,data);			
			Object iden = data.get(data.size() - 1).get("id");
			id = Long.parseLong(iden.toString());
			log.info(query.toString());
		sendPostRq("/_bulk", query.toString());
	}
	}

	public JSONObject sendDeleteRq(String url) throws IOException {
		String deleteUrl = "http://localhost:9200/" + url;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		try {
			HttpDelete request = new HttpDelete(deleteUrl);
			CloseableHttpResponse res = httpClient.execute(request);
			return new JSONObject(EntityUtils.toString(res.getEntity(), "UTF-8"));
		} catch (Exception  ex) {
			return null;
		} finally {
			httpClient.close();
		}
	}
	
	public JSONObject sendPostRq(String url,String data) throws Exception{
		String urlPost = "http://localhost:9200/"+url;		
		CredentialsProvider provider = buidCredentials();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		
		try {
			HttpEntityEnclosingRequestBase request = buildRequest(urlPost, data, HttpMethod.POST);
			CloseableHttpResponse res = httpClient.execute(request);
			return new JSONObject(EntityUtils.toString(res.getEntity(),"UTF-8"));
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			httpClient.close();
		}
	}
	public JSONObject sendPutRq(String url, String data) throws Exception {
		String urlPut = "http://localhost:9200/"+url;		
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
		
		sql.append("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> columns = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		List<String> listColumns = new ArrayList<String>();
		for(Map<String,Object> i:columns) {
			listColumns.add(i.values().toString());
		}
		
		return listColumns;
	}
	
	public List<Map<String,Object>> getDataTabble(String tableName,long id){
		MapSqlParameterSource parrams = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT * FROM " + tableName +" WHERE ID > " + id + " ORDER BY ID");
		List<Map<String,Object>> data = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return data;
	}
	
	public String buidElasticInsert(String tableName,List<Map<String,Object>> value) throws Exception {
		if(ObjectUtils.isEmpty(value)) {
			return "";
		}
//		 List<String> columns = this.getColumnName(tableName);
//		 Object table = this.getTable(tableName);
		 StringBuilder query = new StringBuilder();
		 for(int i = 0;i<value.size();i++) {
//			 Object item = value.get(i);
			 JSONObject data = new JSONObject(value.get(i));
			 Long id = Long.valueOf(data.get("id").toString());
			 query.append("{\"index\":{\"_index\":\"" + tableName + "\",\"_id\":\"" + id + "\"}}\n");
			 
			 query.append(data.toString()).append("\n");
			 
		 }
		 return query.toString();
		 
	}
	
	public Object getTable(String tableName) throws Exception {
		MapSqlParameterSource parrams = new MapSqlParameterSource();		
		StringBuilder sql = new StringBuilder();	
		sql.append("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'public' AND TABLE_NAME = :tableName LIMIT 1");
		parrams.addValue("tableName", tableName);
		List<Map<String, Object>> listTable = namedParameterJdbcTemplate.queryForList(sql.toString(), parrams);
		return listTable.get(0);
	}

}
