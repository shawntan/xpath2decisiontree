package httpserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import utils.Utils;

public class HttpServer implements Container{
	private Map<String,Method> actionsMap;
	private String controllerPackage;
	private Map<String,Object> instanceMap;
	private int port;
	private Logger logger;
	
	public HttpServer(int port, String controllerPackage) {
		super();
		this.port = port;
		this.controllerPackage = controllerPackage;
		this.actionsMap = new HashMap<String, Method>();
		this.instanceMap= new HashMap<String, Object>();
		this.logger = Utils.createLogger("httpserver");
		
		
		try {
			loadClasses(controllerPackage);
			startHttpServer();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.warning("Server I/O issues?");
		}
	}
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void findClasses(File directory, String packageName,String routeName) throws ClassNotFoundException {
		if (!directory.exists()) return;
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				assert !fileName.contains(".");
				findClasses(file, packageName + "." + fileName,routeName+"/"+fileName);
			} else if (fileName.endsWith(".class") && !fileName.contains("$")) {
				Class _class;
				String className = fileName.substring(0, fileName.length() - 6);
				try {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					// happen, for example, in classes, which depend on 
					// Spring to inject some beans, and which fail, 
					// if dependency is not fulfilled
					_class = Class.forName(
							packageName + '.' + className,
							false,
							Thread.currentThread().getContextClassLoader()
					);
				}
				try {
					String controllerRoute = routeName+"/"+className;
					Object instance =  _class.newInstance();
					Method[] methods = _class.getMethods();
					for(Method m: methods) {
						if(isValidAction(m)) {
							String fullroute = (controllerRoute+"/"+m.getName()).toLowerCase();
							actionsMap.put(fullroute, m);
							instanceMap.put(fullroute, instance);
							logger.info("Added to route: "+fullroute);
						}
					}
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
				//classes.add(_class);
			}
		}
	}

	public String getControllerPackage() {
		return controllerPackage;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void handle(Request request, Response response) {
		try {
			String actionTarget = request.getPath().toString().toLowerCase();
			logger.info(request.getAddress().toString());
			Map query = request.getQuery();
			Object controller = instanceMap.get(actionTarget);
			if(controller == null) {
				response.setCode(404);
				return;
			}
			Method action = actionsMap.get(actionTarget);
			PrintStream out = response.getPrintStream();
			try {
				action.invoke(controller,query,out);
				out.close();
			} catch(NullPointerException e){
				response.setCode(404);
				logger.info("Couldn't find appropriate class to map to.");
			} catch (Exception e){
				logger.info("An error has occured in routing.");
			}

		} catch (IOException e1) {
		} finally {
			try {
				response.getPrintStream().close();
			} catch (IOException e) {
			}
		}
	}
	private boolean isValidAction(Method m) {
		Class[] parameters = m.getParameterTypes();
		
		return  parameters.length == 2 &&
				parameters[0].equals(Map.class) &&
				parameters[1].equals(PrintStream.class) &&
				m.getDeclaringClass()!=Object.class;
	}
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void loadClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");;
			findClasses(new File(fileNameDecoded),packageName,"");
		}
	}
	private void startHttpServer() throws IOException {
		Connection connection = new SocketConnection(this);
		SocketAddress address = new InetSocketAddress(getPort());
		connection.connect(address);
		logger.info("Httpserver started.");
	}
}
