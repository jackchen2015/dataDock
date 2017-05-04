package com.cw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	
	private static Log logger = LogFactory.getLog(MyBatisUtil.class);

    private static final Map<DataSourceEnvironment, SqlSessionFactory> SQLSESSIONFACTORYS   
    = new HashMap<DataSourceEnvironment, SqlSessionFactory>(); 	
    /**
     * 获取SqlSessionFactory
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory(DataSourceEnvironment environment) {
    	SqlSessionFactory sqlSessionFactory = SQLSESSIONFACTORYS.get(environment);
        if (sqlSessionFactory!=null)  
            return sqlSessionFactory;  
        else {  
            InputStream inputStream = null;  
            try {  
                inputStream = Resources.getResourceAsStream("conf.xml");  
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment.name());  
                  
//                logger.info("Get {} SqlSessionFactory successfully.", environment.name());  
            } catch (IOException e) {  
//                logger.warn("Get {} SqlSessionFactory error.", environment.name());  
//                logger.error(e.getMessage(), e);  
            }  
            finally {  
                IOUtils.closeQuietly(inputStream);  
            }  
              
            SQLSESSIONFACTORYS.put(environment, sqlSessionFactory);  
            return sqlSessionFactory;  
        }  
    }
    
    /**
     * 获取SqlSession
     * @return SqlSession
     */
//    public static SqlSession getSqlSession() {
//        return getSqlSessionFactory().openSession();
//    }
    
    /**
     * 获取SqlSession
     * @param isAutoCommit 
     *         true 表示创建的SqlSession对象在执行完SQL之后会自动提交事务
     *         false 表示创建的SqlSession对象在执行完SQL之后不会自动提交事务，这时就需要我们手动调用sqlSession.commit()提交事务
     * @return SqlSession
     */
//    public static SqlSession getSqlSession(boolean isAutoCommit) {
//        return getSqlSessionFactory().openSession(isAutoCommit);
//    }

    /** 
     * 配置到Configuration.xml文件中的数据源的environment的枚举描述 
     * @author boyce 
     * @version 2014-3-27 
     */  
    public static enum DataSourceEnvironment {  
        MSSQL,  
        MYSQL;  
    }  

}