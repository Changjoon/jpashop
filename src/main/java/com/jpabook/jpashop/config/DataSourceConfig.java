package com.jpabook.jpashop.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
        basePackages = "com.jpabook.jpashop",
        entityManagerFactoryRef = "entityManager",
        transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {
    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;

    @Primary
    @Bean("dataSource")
    public DataSource dataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setLocalTransactionMode(true);
        ds.setUniqueResourceName("postgresDB");
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");

        Properties xaProperties = new Properties();
        xaProperties.setProperty("URL", "jdbc:postgresql://localhost:5432/db_jpashop_seoul");
        xaProperties.setProperty("user", "baek");
        xaProperties.setProperty("password", "1234");
        ds.setXaProperties(xaProperties);
        ds.setPoolSize(500);
        ds.setBorrowConnectionTimeout(6000);

        return ds;
    }

    @Primary
    @Bean(name = "entityManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManager() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(dataSource());
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        entityManager.setPackagesToScan("com.jpabook.jpashop");
        entityManager.setPersistenceUnitName("seoulPersistenceUnit");
        entityManager.setJpaPropertyMap(properties);

        return entityManager;
    }
}