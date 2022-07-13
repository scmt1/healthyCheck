package com.scmt.healthy.utils;

import ch.qos.logback.classic.db.names.ColumnName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.security.util.Password;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengjie
 * 功能描述：导出sql 文件
 */
public class ImportInsertUtil {
	private static Connection conn=null;
	private static Statement sm = null;
	private static String schema="FJSTL";//模式名
	private static String select="SELECT * FROM";//查询sql
	private static String insert="INSERT INTO";//插入sql
	private static String values="VALUES";//values关键字
	private static String []table={"t_group_order","t_group_person","t_group_unit","t_order_group","t_order_group_item","t_order_group_item_project"};//table数组

	public static String basePath = UploadFileUtils.basePath + "importsql/";

	/**
	 * 导出数据库表
	 * @param orderId
	 * @throws SQLException
	 */
	public static String importSql(String orderId) throws Exception {
		List<String> listSQL=new ArrayList<String>();
		List<String> insertList=new ArrayList<String>();
		String driverClassName = "com.mysql.cj.jdbc.Driver";
		String DB_URL = "jdbc:mysql://127.0.0.1:3306/healthy_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
		String DB_USERNAME = "develop";
		String DB_PWD = "123456";
		//连接数据库
		connectSQL(driverClassName, DB_URL, DB_USERNAME, DB_PWD);
		//创建查询语句
		listSQL=createSql(orderId);
		//执行sql并拼装
		executeSQL(conn,sm,listSQL, insertList);
		String classPath = DocUtil. getClassPath();
		String file = classPath.split(":")[0] + ":" + basePath + "insertSql.sql";
		//创建文件
		createFile(file,insertList);
		return  file;
	}
	/**
	 * 创建insertsql.txt并导出数据
	 */
	private static void createFile(String filePath,List<String> insertList) {
		File file=new File(filePath);
		try {
			if(!file.exists()){
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			else{
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			System.out.println("创建文件名失败！！");
			e.printStackTrace();
		}
		FileWriter fw=null;
		BufferedWriter bw=null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			if(insertList.size()>0){
				for(int i=0;i<insertList.size();i++){
					bw.append(insertList.get(i));
					bw.append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 拼装查询语句
	 * @return 返回select集合
	 */
	private static List<String> createSql(String orderId) {
		List<String> listSQL=new ArrayList<String>();
		for(int i=0;i<table.length;i++){
			StringBuffer sb=new StringBuffer();
			sb.append(select).append(" ").append(table[i]);
			if(table[i].equals("t_group_order")){
				sb.append(" Where id = '").append(orderId).append("'");
			}
			else if(table[i].equals("t_group_person")){
				sb.append(" Where order_id = '").append(orderId).append("'");
			}
			else if(table[i].equals("t_group_unit")){
				sb.append(" Where id in (").append("select group_unit_id from t_group_order where id = '"+orderId+"' ").append(")");
			}
			else if(table[i].equals("t_order_group")){
				sb.append(" Where group_order_id = '").append(orderId).append("'");
			}
			else if(table[i].equals("t_order_group_item")){
				sb.append(" Where group_order_id = '").append(orderId).append("'");
			}
			else if(table[i].equals("t_order_group_item_project")){
				sb.append(" Where group_order_id = '").append(orderId).append("'");
			}
			listSQL.add(sb.toString());
		}
		return listSQL;
	}
	/**
	 * 连接数据库 创建statement对象
	 * @param driver
	 * @param url
	 * @param UserName
	 * @param Password
	 */
	public static void connectSQL(String driver,String url,String UserName,String Password){
		try{
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, UserName, Password);
			sm=conn.createStatement();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 执行sql并返回插入sql
	 * @param conn
	 * @param sm
	 * @param listSQL
	 * @throws SQLException
	 */
	public static void executeSQL(Connection conn,Statement sm,List listSQL,List<String> insertList) throws SQLException{
		List<String> insertSQL=new ArrayList<String>();
		ResultSet rs=null;
		try {
			rs = getColumnNameAndColumeValue(sm, listSQL, rs,insertList);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			rs.close();
			sm.close();
			conn.close();
		}
	}
	/**
	 * 获取列名和列值
	 * @param sm
	 * @param listSQL
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static ResultSet getColumnNameAndColumeValue(Statement sm,
														 List listSQL, ResultSet rs,List<String> insertList) throws SQLException {
		try {
			if(listSQL.size()>0){
				for(int j=0;j<listSQL.size();j++){
					String sql=String.valueOf(listSQL.get(j));
					rs=sm.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					while(rs.next()){
						StringBuffer ColumnName=new StringBuffer();
						StringBuffer ColumnValue=new StringBuffer();
						for(int i=1;i<=columnCount;i++){
							if(rs.getString(i) ==null){
								continue;
							}
							String value=rs.getString(i).trim();
							if("".equals(value)){
								value=" ";
							}
							if(i==1){
								ColumnName.append(rsmd.getColumnName(i));
								if(Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i)
										|| Types.LONGVARCHAR == rsmd.getColumnType(i)){
									ColumnValue.append("'").append(value).append("',");
								}else if(Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i)
										|| Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i)
										|| Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i)
										|| Types.DECIMAL == rsmd.getColumnType(i)){
									ColumnValue.append(value).append(",");
								}else if(Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i)
										|| Types.TIMESTAMP == rsmd.getColumnType(i)){
									ColumnValue.append("'").append(value).append("'").append(",");
								}else if(Types.LONGVARBINARY == rsmd.getColumnType(i) || Types.VARBINARY == rsmd.getColumnType(i)){
									ColumnValue.append("null").append(",");
								}
								else{
									ColumnValue.append(value).append(",");
								}
							}else{
								ColumnName.append(","+rsmd.getColumnName(i));
								if(Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i)
										|| Types.LONGVARCHAR == rsmd.getColumnType(i)){
									ColumnValue.append("'").append(value).append("'").append(",");
								}else if(Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i)
										|| Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i)
										|| Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i)
										|| Types.DECIMAL == rsmd.getColumnType(i)){
									ColumnValue.append(value).append(",");
								}else if(Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i)
										|| Types.TIMESTAMP == rsmd.getColumnType(i)){
									ColumnValue.append("'").append(value).append("'").append(",");
								}else if(Types.LONGVARBINARY == rsmd.getColumnType(i) || Types.VARBINARY == rsmd.getColumnType(i)){
									ColumnValue.append("null").append(",");
								}
								else{
									ColumnValue.append(value).append(",");
								}
							}
						}
						//System.out.println(ColumnName.toString());
						//System.out.println(ColumnValue.toString());

						insertSQL(ColumnName, ColumnValue,j,insertList);
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return rs;
	}
	/**
	 * 拼装insertsql 放到全局list里面
	 * @param ColumnName
	 * @param ColumnValue
	 */
	private static void insertSQL(StringBuffer ColumnName,
								  StringBuffer ColumnValue,int j,List<String> insertList) {

			StringBuffer insertSQL=new StringBuffer();
			insertSQL.append(insert).append(" ")
					.append(table[j]).append("(").append(ColumnName.toString())
					.append(")").append(values).append("(").append(ColumnValue.toString().substring(0, ColumnValue.toString().length()-1)).append(");");
			insertList.add(insertSQL.toString());
			//System.out.println(insertSQL.toString());

	}
}
