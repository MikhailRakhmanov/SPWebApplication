package ru.raticate.spWebApplication.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
class SPDatasourceConfiguration {
    @Bean
    fun dataSource(): DataSource {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    fun sPJdbcTemplate(@Qualifier("dataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.sp")
    fun dataSourceProperties(): DataSourceProperties {
        return DataSourceProperties();
    }
    
}