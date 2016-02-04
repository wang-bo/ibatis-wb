package com.wb.ibatis.common.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

import com.wb.ibatis.common.beans.ClassInfo;

/**
 * @author www
 * @date 2016年1月31日
 * 
 * 简化获取resources的方法(通过classloader)
 * 
 */

public class Resources {

	// 默认classloader
	private static ClassLoader defaultClassLoader;
	
	// 调用getResourceAsReader方法时使用的字符集，如果为null，使用系统的默认字符集
	private static Charset charset;
	
	private Resources() {
		
	}

	/**
	 * 获取默认classloader，可能为null
	 * @return
	 */
	public static ClassLoader getDefaultClassLoader() {
		return defaultClassLoader;
	}

	/**
	 * 设置默认classloader
	 * @param defaultClassLoader
	 */
	public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
		Resources.defaultClassLoader = defaultClassLoader;
	}

	/**
	 * 获取调用getResourceAsReader方法时使用的字符集，可能为null
	 * @return
	 */
	public static Charset getCharset() {
		return charset;
	}

	/**
	 * 设置调用getResourceAsReader方法时使用的字符集
	 * @param charset
	 */
	public static void setCharset(Charset charset) {
		Resources.charset = charset;
	}
	
	/**
	 * 获取默认classloader，如果为null，则返回当前线程的上下文classloader，
	 * 当前线程的上下文classloader由线程创建者提供，供运行于该线程的代码在加载类和资源时使用。
	 * @return
	 */
	private static ClassLoader getClassLoader() {
		if (defaultClassLoader != null) {
			return defaultClassLoader;
		} else {
			return Thread.currentThread().getContextClassLoader();
		}
	}
	
	/**
	 * 返回classpath上指定资源的字节输入流
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceAsStream(String resource) throws IOException {
		return getResourceAsStream(getClassLoader(), resource);
	}
	
	/**
	 * 返回classpath上指定资源的字节输入流
	 * @param loader
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
		InputStream in = null;
		if (loader != null) {
			in = loader.getResourceAsStream(resource);
		}
		if (in == null) {
			in = ClassLoader.getSystemResourceAsStream(resource); // 通过系统类的加载器来加载资源
		}
		if (in == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return in;
	}
	
	/**
	 * 返回classpath上指定资源的字符读取流
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Reader getResourceAsReader(String resource) throws IOException {
		return getResourceAsReader(getClassLoader(), resource);
	}
	
	/**
	 * 返回classpath上指定资源的字符读取流
	 * @param loader
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
		Reader reader = null;
		if (charset == null) {
			reader = new InputStreamReader(getResourceAsStream(loader, resource));
		} else {
			reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
		}
		return reader;
	}
	
	/**
	 * 返回classpath上指定资源的URL
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static URL getResourceURL(String resource) throws IOException {
		return getResourceURL(getClassLoader(), resource);
	}
	
	/**
	 * 返回classpath上指定资源的URL
	 * @param loader
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
		URL url = null;
		if (loader != null) {
			url = loader.getResource(resource);
		}
		if (url == null) {
			url = ClassLoader.getSystemResource(resource);
		}
		if (url == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return url;
	}
	
	/**
	 * 解析classpath上指定资源为Properties
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Properties getResourceAsProperties(String resource) throws IOException {
		return getResourceAsProperties(getClassLoader(), resource);
	}
	
	/**
	 * 解析classpath上指定资源为Properties
	 * @param loader
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
		Properties props = new Properties();
		InputStream in = getResourceAsStream(loader, resource);
		props.load(in);
		in.close();
		return props;
	}
	
	/**
	 * 返回classpath上指定资源的File(可读写)
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File getResourceAsFile(String resource) throws IOException {
		return getResourceAsFile(getClassLoader(), resource);
	}
	
	/**
	 * 返回classpath上指定资源的File(可读写)
	 * @param loader
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
		return new File(getResourceURL(loader, resource).getFile());
	}
	
	/**
	 * 返回url资源的字节输入流
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static InputStream getUrlAsStream(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		return connection.getInputStream();
	}
	
	/**
	 * 返回url资源的字符读取流
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static Reader getUrlAsReader(String urlString) throws IOException {
		return new InputStreamReader(getUrlAsStream(urlString));
	}
	
	/**
	 * 返回url资源的字符读取流
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static Properties getUrlAsProperties(String urlString) throws IOException {
		Properties props = new Properties();
		InputStream in = null;
		in = getUrlAsStream(urlString);
		props.load(in);
		in.close();
		return props;
	}
	
	/**
	 * 加载一个类
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> classForName(String className) throws ClassNotFoundException {
		Class<?> clazz = null;
		try {
			clazz = getClassLoader().loadClass(className);
		} catch (Exception e) {
			// 不处理，接着执行下面的代码
		}
		if (clazz == null) {
			clazz = Class.forName(className);
		}
		return clazz;
	}
	
	/**
	 * 创建一个类的实例
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object instantiate(String className)
		      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return instantiate(classForName(className));
	}
	
	/**
	 * 创建一个类的实例
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object instantiate(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		try {
			return ClassInfo.getInstance(clazz).instantiateClass();
		} catch (Exception e) {
			// 再尝试一下，理论上会因为相同的原因而失败
			// 但在有些情况下，却能成功(有一个奇怪的安全管理器，具体咋回事不清楚...)
			return clazz.newInstance();
		}
	}
}
