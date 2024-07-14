package com.jpabook.jpashop.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.jpabook.jpashop.repository")
public class XAJpaConfig {

    private static Logger logger = LoggerFactory.getLogger(XAJpaConfig.class);

    @SuppressWarnings("unchecked")
    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource dataSource() {
        try {
            AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
            dataSource.setUniqueResourceName("DB_JPASHOP_SEOUL");
            dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
            dataSource.setXaProperties(xaProperties());
            dataSource.setPoolSize(500);
            dataSource.setBorrowConnectionTimeout(6000);
            return dataSource;
        } catch (Exception e) {
            logger.error("AtomikosDataSourceBean 생성 실패", e);
            return null;
        }
    }

    private Properties xaProperties() {
        Properties xaProperties = new Properties();
        xaProperties.setProperty("URL", "jdbc:postgresql://localhost:5432/db_jpashop_seoul");
        xaProperties.setProperty("databaseName", "db_jpashop_seoul");
        xaProperties.setProperty("user", "postgres");
        xaProperties.setProperty("password", "1234");
        return xaProperties;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.transaction.factory_class", "org.hibernate.transaction.JTATransactionFactory");
        properties.put("hibernate.transaction.jta.platform", "com.atomikos.icatch.jta.hibernate4.AtomikosPlatform");
        properties.put("hibernate.transaction.coordinator_class", "jta");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL82Dialect");

        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.max_fetch_depth", 3);
        properties.put("hibernate.jdbc.batch_size", 10);
        properties.put("hibernate.jdbc.fetch_size", 50);

        return properties;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("com.jpabook.jpashop");
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setPersistenceUnitName("db_jpashop_seoul");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}