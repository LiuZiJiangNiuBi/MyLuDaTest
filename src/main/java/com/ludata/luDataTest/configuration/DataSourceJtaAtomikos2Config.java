package com.ludata.luDataTest.configuration;

import com.mysql.cj.jdbc.MysqlXADataSource;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/*================================================================
说明：配置(jta+atomikos)分布式事务数据源2

作者          		时间              注释
刘梓江       	 2019.6.25	          创建
==================================================================*/
@SpringBootConfiguration
@ConfigurationProperties(prefix = "spring.datasource.druid2") //配置所需要的数据源参数前缀
@MapperScan(basePackages = "com.ludata.luDataTest.dataSource2.mapper", sqlSessionFactoryRef = "test2SqlSessionFactory")  //提示：basePackages用于扫描mapper接口对本数据库中数据操作的包
@Data //使用@Data包含（@Getter/@Setter注解），此注解在对应的属性上，可以为相应的属性自动生成Getter/Setter方法 (提示：该开发工具要配置lombok组件)
public class DataSourceJtaAtomikos2Config {
	
	//配置数据源基础参数
	private String url;
	
	private String driverClassName;
	
	private String username;
	
	private String password;
	
	/**
	 *  详情：配置当前数据源
	 */
	@Bean(name = "test2DataSource")
	public DataSource testDataSource() throws  Exception {
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
		
		MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
		mysqlXaDataSource.setUrl(url);
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		mysqlXaDataSource.setPassword(password);
		mysqlXaDataSource.setUser(username);
		mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);

		// 将本地事务注册到创 Atomikos全局事务
		AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
		xaDataSource.setXaDataSource(mysqlXaDataSource);
		xaDataSource.setUniqueResourceName("test2DataSource");
		return xaDataSource;
	}
	
	/**
	 * 建立当前数据源的会话工厂
	 */
	@Bean(name = "test2SqlSessionFactory")
	public SqlSessionFactory testSqlSessionFactory(@Qualifier("test2DataSource") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		//bean.setMapperLocations(
		//new
		//PathMatchingResourcePatternResolver().getResources("classpath:mybatis/dataSource2Mapper-XML/*.xml"));  //提示：如果XML文件包含在Mapper接口包中,就不需要设置 如果在resources根目录下设置
		return bean.getObject();
	}

	/**
	 * 建立事务管理器
	 */
//	@Bean(name = "transactionManager2")
//	public DataSourceTransactionManager testTransactionManager(@Qualifier("test2DataSource") DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
}
