package com.scmt.healthy.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * @Author: dengjie
 * @Description: 调用mysql的.sql文件，执行mysql语句
 * @Date: 2022/1/12
 */
@Component
public class ExecuteSqlUtil {

	@Value("${spring.datasource.dynamic.datasource.master.url}")
	private String DB_URL;
	@Value("${spring.datasource.dynamic.datasource.master.username}")
	private String DB_USERNAME;
	@Value("${spring.datasource.dynamic.datasource.master.password}")
	private String DB_PWD;

	/**
	 * 执行sql 脚本（加载的SQL文件不在class下）
	 *
	 * @param sqlPath 文件名（完整路径）
	 * @throws SQLException           sql 执行异常
	 * @throws ClassNotFoundException 文件异常
	 */
	public Connection executeSql(String sqlPath) throws SQLException, ClassNotFoundException, IOException {
		Connection connection = null;
		try {
			String driverClassName = "com.mysql.cj.jdbc.Driver";
//            String DB_URL = "jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&useSSL=true&rewriteBatchedStatements=true";
//            String DB_USERNAME = "root";
//            String DB_PWD = "123456";

			Class.forName(driverClassName);
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PWD);

			//加载的SQL文件不在class下
			FileSystemResource rc = new FileSystemResource(sqlPath);
			//ClassPathResource rc = new ClassPathResource(sqlFileName);
			EncodedResource er = new EncodedResource(rc, "utf-8");
			//ScriptUtils.executeSqlScript(connection, er, true, true, "#", null, "/*", "*/");
			//connection.close();
			//SQL脚本分解执行 一条一条执行
			batchExecuteSql(connection,er,1);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * SQL脚本分解执行
	 * @param resource SQL脚本资源
	 * @param batchNumber  每多少条SQL执行一次
	 * @throws SQLException sql异常
	 * @throws IOException 输入输出异常
	 */
	public void batchExecuteSql(Connection connection,EncodedResource resource, int batchNumber) throws SQLException, IOException {
		Statement statement = connection.createStatement();
		BufferedReader bufferedReader = null;
		try {
			//获取字符缓冲流
			bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			int l;
			int i = 0;
			StringBuilder sql = new StringBuilder();
			while ((l = bufferedReader.read()) != -1) {
				char read = (char) l;
				sql.append(read);
				// 一个完整的SQL语句
				if (read == ';') {
					i ++;
					statement.addBatch(sql.toString());
					if (i % batchNumber == 0) {
						try {
							statement.executeBatch();
							statement.clearBatch();
						}
						//报错肯定是主键重复，继续执行即可
						catch (BatchUpdateException e ){
							statement.clearBatch();
							sql.delete(0, sql.length());
							continue;
						}

					}
					//清除StringBuilder中的SQL语句
					sql.delete(0, sql.length());
				}
			}
			//执行最后不足 batchNumber 的语句
			if (i % batchNumber != 0) {
				statement.executeBatch();
				statement.clearBatch();
			}
		}catch (Exception e){
			throw e;
		}
		finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
	}
	/**
	 * 执行sql 脚本（加载的SQL文件在class下）
	 *
	 * @param sqlFileName 文件名
	 * @throws SQLException           sql 执行异常
	 * @throws ClassNotFoundException 文件异常
	 */
	public Connection executeSqlClass(String sqlFileName) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		try {
			String driverClassName = "com.mysql.cj.jdbc.Driver";
			Class.forName(driverClassName);
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PWD);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		ClassPathResource rc = new ClassPathResource(sqlFileName);
		EncodedResource er = new EncodedResource(rc, "utf-8");
		ScriptUtils.executeSqlScript(connection, er);
		return connection;
	}
}
