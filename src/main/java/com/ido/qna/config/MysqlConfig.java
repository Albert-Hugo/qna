package com.ido.qna.config;


import com.ido.qna.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ido
 * Date: 2018/1/8
 **/
@Configuration
@EnableJpaRepositories(
        basePackages = "com.ido.qna.repo",
        entityManagerFactoryRef = "mysqlManager",
        transactionManagerRef = "mysqlTransactionManager"
)
@Slf4j
public class MysqlConfig {

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>(3);
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        props.put("spring.jpa.hibernate.ddl-auto",
               "update");
        return props;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean mysqlManager(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Question.class)
                .persistenceUnit("mysql")
                .properties(jpaProperties())
                .build();

    }



    @Primary
    @Bean
    public PlatformTransactionManager mysqlTransactionManager(EntityManagerFactoryBuilder builder, DataSource dataSource) {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                mysqlManager(builder,dataSource).getObject());
        return transactionManager;
    }


    @Primary
    @Bean
    @Profile("test")
    public DataSource mysqlTestDataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        System.setProperty("test","true");

        return dataSource;
    }

    @Primary
    @Bean
    @Profile("pc")
    public DataSource mysqlMyPcDataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/qna?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("teamoshary520");

        return dataSource;
    }

    @Primary
    @Bean
    @Profile("work")
    public DataSource mysqlDevDataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://192.168.1.27:3326/testBak?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("my-secret-pw");



        return dataSource;
    }

    @Primary
    @Bean
    @Profile("local")
    public DataSource mysqlLocalDataSource() {
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/qna?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("mellamoido123");



        return dataSource;
    }


    @Primary
    @Bean
    @Profile("pro")
    public DataSource mysqlProDataSource() {
        log.info("loading production mysql config");
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/qna?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("ido");
        dataSource.setPassword("mellamoido123");

        return dataSource;
    }
}
