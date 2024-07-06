package com.jpabook.jpashop.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    String dbUrl = "jdbc:mysql://localhost:3306/db_jpashop_seoul?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&pinGlobalTxToPhysicalConnection=true";
    String username = "root";
    String passport = "1234";

    @Bean("dataSourceProperties")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("dataSourceProperties") DataSourceProperties dataSourceProperties) {
        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
        mysqlXaDataSource.setUrl(dbUrl);
        mysqlXaDataSource.setUser(username);
        mysqlXaDataSource.setPassword(passport);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXaDataSource);
        xaDataSource.setUniqueResourceName("db_jpashop_seoul");
        xaDataSource.setMaxPoolSize(30);
        return xaDataSource;
    }

}