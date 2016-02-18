package com.wb.ibatis.common.jdbc;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月17日
 * 
 * ScriptRunner是iBATIS的SQL语句执行程序
 * 
 */

public class ScriptRunner {

	private static final String DEFAULT_DELIMITER = ";";
	
	// 数据库连接参数
	private Connection connection;
	private String driver;
	private String url;
	private String username;
	private String password;
	
	// 是否忽略SQL执行错误
	private boolean stopOnError;
	// 是否自动提交
	private boolean autoCommit;
	
	// 日志信息输出，默认输出到标准输出流
	private PrintWriter logWriter = new PrintWriter(System.out);
	// 错误信息输出，默认输出到标准错误输出流
	private PrintWriter errorLogWriter = new PrintWriter(System.err);
	
	// SQL语句执行中的分行标志
	private String delimiter = DEFAULT_DELIMITER;
	// SQL语句执行中是否分行，默认不分行(可以兼容分行和不分行，不用设置是否分行，故注释掉)
	// private boolean fullLineDelimiter = false;
	
	/**
	 * 推荐用的构造方法，使用数据库连接的构造方法
	 */
	public ScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
		this.connection = connection;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}
	
	/**
	 * 使用连接参数的构造方法
	 */
	public ScriptRunner(String driver, String url, String username, String password, boolean autoCommit, boolean stopOnError) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}
	
	/**
	 * 设置SQL语句执行中的分行标志
	 */
	public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
		this.delimiter = delimiter;
		// 可以兼容分行和不分行，不用设置是否分行，故注释掉
		// this.fullLineDelimiter = fullLineDelimiter;
	}
	
	/**
	 * 设置日志信息输出流
	 */
	public void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}
	
	/**
	 * 设置错误信息输出流
	 */
	public void setErrorLogWriter(PrintWriter errorLogWriter) {
		this.errorLogWriter = errorLogWriter;
	}
	
	/**
	 * 从给定的输入流中读取SQL脚本并执行
	 */
	public void runScript(Reader reader) throws IOException, SQLException {
		try {
			if (connection == null) {
				DriverManager.registerDriver((Driver) Resources.instantiate(driver));
				Connection conn = DriverManager.getConnection(url, username, password);
				try {
					if (conn.getAutoCommit() != autoCommit) {
						conn.setAutoCommit(autoCommit);
					}
					runScript(conn, reader);
				} finally {
					conn.close();
				}
			} else {
				boolean originalAutoCommit = connection.getAutoCommit();
				try {
					if (originalAutoCommit != autoCommit) {
						connection.setAutoCommit(autoCommit);
					}
					runScript(connection, reader);
				} finally {
					connection.setAutoCommit(originalAutoCommit);
				}
			}
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error running script. Cause: " + e, e);
		}
	}
	
	/**
	 * 从给定的输入流中读取SQL脚本，并使用给定的数据库连接执行该脚本
	 */
	private void runScript(Connection conn, Reader reader) throws IOException, SQLException {
		StringBuilder command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuilder();
				}
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					println(trimmedLine);
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
					// do nothing
				} else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
					// do nothing
//				} else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter())
//						|| fullLineDelimiter && trimmedLine.equals(getDelimiter())) {
				} else if (trimmedLine.endsWith(getDelimiter())) {
					command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
					command.append(" ");
					println(command);

					Statement statement = conn.createStatement();
					boolean hasResults = false;
					if (stopOnError) {
						hasResults = statement.execute(command.toString());
					} else {
						try {
							hasResults = statement.execute(command.toString());
						} catch (SQLException e) {
							e.fillInStackTrace();
							printlnError("Error executing: " + command);
							printlnError(e);
						}
					}
					
					if (autoCommit && !conn.getAutoCommit()) {
						conn.commit();
					}
					
					ResultSet rs = statement.getResultSet();
					if (hasResults && rs != null) {
						ResultSetMetaData md = rs.getMetaData();
						int cols = md.getColumnCount();
						for (int i = 1; i <= cols; i++) {
							String name = md.getColumnLabel(i);
							print(name + '\t');
						}
						println("");
						while (rs.next()) {
							for (int i = 1; i <= cols; i++) {
								String value = rs.getString(i);
								print(value + '\t');
							}
							println("");
						}
					}
					
					command = null;
					try {
						statement.close();
					} catch (Exception e) {
						// Jakarta DBCP 有个bug，所以这里忽略
					}
					Thread.yield();
				} else {
					command.append(line);
					command.append(" ");
				}
			}
			if (!autoCommit) {
				conn.commit();
			}
		} catch (SQLException | IOException e) {
			e.fillInStackTrace();
			printlnError("Error executing: " + command);
			printlnError(e);
			throw e;
		} finally {
			if (!autoCommit) {
				conn.rollback();
			}
			flush();
		}
	}
	
	/**
	 * 获取SQL语句的分行标志
	 */
	private String getDelimiter() {
		return delimiter;
	}
	
	private void print(Object o) {
		if (logWriter != null) {
			logWriter.print(o);
		}
	}
	
	private void println(Object o) {
		if (logWriter != null) {
			logWriter.println(o);
		}
	}
	
	private void printlnError(Object o) {
		if (errorLogWriter != null) {
			errorLogWriter.println(o);
		}
	}
	
	private void flush() {
		if (logWriter != null) {
			logWriter.flush();
		}
		if (errorLogWriter != null) {
			errorLogWriter.flush();
		}
	}

}
