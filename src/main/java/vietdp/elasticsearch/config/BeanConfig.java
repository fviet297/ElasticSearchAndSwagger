package vietdp.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
@SuppressWarnings("deprecation")
@Configuration
@EnableElasticsearchRepositories(basePackages = "vietdp.elasticsearch.repository")
@ComponentScan(basePackages = "vietdp.elasticsearch.*")
public class BeanConfig extends AbstractElasticsearchConfiguration {
//	@SuppressWarnings("deprecation")
	@Override
	@Bean
	
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200")
				.build();
		return RestClients.create(clientConfiguration).rest();
	}
	
	
}