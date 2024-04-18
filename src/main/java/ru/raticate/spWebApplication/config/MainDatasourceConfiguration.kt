package ru.raticate.spWebApplication.config;

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
class MainDatasourceConfiguration {
    @Bean
    fun mainDataSource(): DataSource {
        return mainDataSourceProperties()
                .initializeDataSourceBuilder()
                .build()
    }

    @Bean
    @ConfigurationProperties("spring.datasource.main")
    fun mainDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun mainJdbcTemplate(@Qualifier("mainDataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource);
    }
    
}