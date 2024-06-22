/**
 * This is a kingdee cosmic template project that is automatically generated by the Kingdee cosmic development assistant plugin. 
 * If there are any issues during the use process, you can provide feedback to the kingdee developer community website.
 * Website: https://developer.kingdee.com/developer?productLineId=29
 * Author: liebin.zheng
 * Generate Date: 2024-05-14 10:48:49
 */
package kd.cosmic.debug.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import kd.bos.config.client.util.ConfigUtils;
import kd.bos.service.bootstrap.Booter;

/**
 * cosmic服务启动器，含默认配置，如需更改请在DebugApplication中设置。
 * 注：本工具类由开发助手自动生成，请不用直接修改本工具类。
 */
public final class CosmicLauncher {
	
	/**
	 * 苍穹安装目录的环境变量名称（即苍穹依赖包和静态资源目录的上级目录）
	 */
	private static final String COSMIC_HOME = "COSMIC_HOME";
	
	/**
	 * Gradle变量-Cosmic Home
	 */
	private static final String GRADLE_PROPERTIES_COSMIC_HOME = "systemProp.cosmic_home";
	
	/**
     * 苍穹Gradle模板默认变量 - 默认的苍穹资源目录
     */
    private static final String DEFAULT_COSMIT_HOME_PATH = System.getProperty("user.home").replaceAll("\\\\", "/") + "/cosmic/home";
	
	private static final String PROJECT_HOME = "D:/SoftwareCup/smart_school";
	
	private static final String LOCAL_IP = "127.0.0.1";
	
	public static String localHostName;
	
    private boolean setConfigUrl = false;

    private int cosmicPort = 8881;
    
    private String cosmicUrl = "http://127.0.0.1:" + cosmicPort + "/ierp";
    
    /**
     * 是否优先使用MC服务端的配置替代本地调试模板工程的部分默认配置
     */
    private boolean useMcServiceConfigFirst = false;
    
    public CosmicLauncher() {
        setDefault();
    }
    
    /**
     * @param useMcServiceConfigFirst 是否优先使用MC服务端的配置替代本地调试模板工程的部分默认配置
     */
    public CosmicLauncher(boolean useMcServiceConfigFirst) {
    	this.useMcServiceConfigFirst = useMcServiceConfigFirst;
        setDefault();
    }

    public void setDefault() {
    	
        set("configAppName", "mservice,web");
        set("webmserviceinone", "true");
        set("file.encoding", "utf-8");
        set("MONITOR_HTTP_PORT", "9998");
        set("JMX_HTTP_PORT", "9091");
        set("appSplit", "false");
        set("tenant.code.type", "config");
        
        localHostName = getLocalHostName();
        setClusterNumber("cosmic");
        setTenantNumber("ierp");
        setAppName("kdcosmic-" + localHostName + "-" + cosmicPort);
        
        setXdbEnable(false);
        setSqlOut(true, true);
        
        setCosmicWepPort(cosmicPort);
        setWebResPath(getCosmicHome() + "/static-file-service");
        
        //是否优先使用MC服务端的配置替代本地调试模板工程的部分默认配置
		if (!useMcServiceConfigFirst) {
			//当本地开发且连接轻量级环境时，可使用以下默认配置
	        setConfigUrl("127.0.0.1:2181");
	        setMcServerUrl("http://127.0.0.1:8090");
	        setFsServerUrl("127.0.0.1", 8100);
	        setImageServerUrl("127.0.0.1", 8100);
	        
//	        setEnableLightWeightDeploy(true);
	        setDubboHostConfig(LOCAL_IP, 28888, 30880);
//	        set("login.type", "STANDALONE");
	        setMqConsumerRegister(false, null);
		} else {
			//当需要连接项目开发环境（即非轻量级环境）时，应以MC服务器上面的配置优先
//			setEnableLightWeightDeploy(false);
			setMqConsumerRegister(true, localHostName);
		}
		
		setStartWithQing(false);
        
		//是否以轻量级环境启动苍穹服务
        setEnableLightWeightDeploy(true);
        
        //本地日志配置
        setLogConfig(false);
        
        //Dubbo服务注册配置
        setDubboConfig(false, true, true);
        
    }

    public void start() {
//    	LOG.info("Cosmic Service starting! Please check url: {}", getCosmicUrl());
    	Booter.main(null);
    }

    public void set(String key, String value) {
        System.setProperty(key, value);
    }

    public String get(String key) {
        return System.getProperty(key);
    }

    /**
     * 设置苍穹服务器IP地址（包括MC、ZK、文件及图片服务）
     * 不推荐使用该方法，项目开发时应该分别配置这几个服务地址，或者优先使用MC服务器上的配置。
     */
    @Deprecated
    public void setServerIp(String ip) {
        setMcServerUrl("http://" + ip + ":8090");
        if (!setConfigUrl) {
            setConfigUrl(ip + ":2181");
        }
        setFsServerUrl(ip, 8100);
        setImageServerUrl(ip, 8100);
    }

    /**
     * 设置MC服务地址
     *
     * @param mcServerUrl
     */
    public void setMcServerUrl(String mcServerUrl) {
        set("mc.server.url", mcServerUrl);
    }

    /**
     * @param configUrl 配置服务地址
     */
    public void setConfigUrl(String configUrl) {
        set(ConfigUtils.CONFIG_URL_KEY, configUrl);
        setConfigUrl = true;
    }

    /**
     * 配置服务地址
     *
     * @param connectString zookeeper链接URL，如 127.0.0.1:2181
     * @param user          用户
     * @param password      密码
     */
    public void setConfigUrl(String connectString, String user, String password) {
        if (user != null && password != null) {
            setConfigUrl(connectString + "?user=" + user + "&password=" + password);
        } else {
            setConfigUrl(connectString);
        }
    }

    /**
     * @param clusterNumber 集群编码
     */
    public void setClusterNumber(String clusterNumber) {
        set(ConfigUtils.CLUSTER_NAME_KEY, clusterNumber);
    }


    /**
     * @param appName 本节点服务名称
     */
    public void setAppName(String appName) {
        setAppName(appName, true);
    }

    public void setAppName(String appName, boolean alsoSetQueueTag) {
        set(ConfigUtils.APP_NAME_KEY, appName);
        if (alsoSetQueueTag) {
            setQueueTag(appName);
        }
    }

    public void setStartWithQing(boolean b) {
        set("bos.app.special.deployalone.ids", b ? " " : "qing");
    }

    /**
     * @param tenantNumber 租户编码
     */
    public void setTenantNumber(String tenantNumber) {
        set("domain.tenantCode", tenantNumber);
    }

    /**
     * @param enable 是否开启水平分表服务
     */
    public void setXdbEnable(boolean enable) {
        set("xdb.enable", String.valueOf(enable));
    }

    /**
     * @param tag 队列标记
     */
    public void setQueueTag(String tag) {
        set("mq.debug.queue.tag", tag);
    }

    /**
     * @param path web静态资源路径
     */
    public void setWebResPath(String path) {
        set("JETTY_WEBRES_PATH", path);
    }
    
    /**
     * 控制台输出SQL开关
     *
     * @param outSql        是否输出SQL
     * @param withParameter 是否输出参数
     */
    public void setSqlOut(boolean outSql, boolean withParameter) {
        set("db.sql.out", String.valueOf(outSql));
        set("db.sql.out.withParameter", String.valueOf(withParameter));
    }

    /**
     * 设置苍穹服务端口
     * @param port
     */
    public void setCosmicWepPort(int port) {
    	this.cosmicPort = port;
    	this.cosmicUrl = "http://127.0.0.1:" + cosmicPort + "/ierp";
        set("JETTY_WEB_PORT", String.valueOf(cosmicPort));
        set("domain.contextUrl", cosmicUrl);
    }
    
    /**
     * 设置是否注册为MQ消费者
     * @param registerOnMq
     * @param debbugTopic
     */
    public void setMqConsumerRegister(boolean registerOnMq, String debbugTopic) {
    	set("mq.consumer.register", String.valueOf(registerOnMq));
    	if(StringUtils.isNotBlank(debbugTopic)) {
    		set("mq.debug.queue.tag", debbugTopic);
    	}
    }
    
    /**
     * 设置文件服务地址
     * @param ip
     * @param port
     */
    public void setFsServerUrl(String ip, int port) {
    	set("fileserver", "http://" + ip + ":" + port + "/fileserver/");
    	set("attachmentServer.url", "http://" + ip + ":" + port + "/fileserver/");
    	set("attachmentServer.inner.url", "http://" + ip + ":" + port + "/fileserver/");
    }
    
    /**
     * 设置图片服务地址
     * @param ip
     * @param port
     */
    public void setImageServerUrl(String ip, int port) {
        set("imageServer.url", "http://" + ip + ":" + port + "/fileserver/");
        set("imageServer.inner.url", "http://" + ip + ":" + port + "/fileserver/");
    }

    /**
     * 获取苍穹服务URL
     */
	public String getCosmicUrl() {
		return cosmicUrl;
	}
	
	/**
     * 设置webapp配置所在的目录
     */
	public void setWebAppPath(String path) {
		set("JETTY_WEBAPP_PATH", path);
	}
	
	/**
	 * 是否以轻量级环境启动苍穹服务
	 */
	public void setEnableLightWeightDeploy(boolean enable) {
		set("lightweightdeploy", String.valueOf(enable));
		set("lightweightdeploy.services", "");
	}

	/**
	 * Redis配置
	 */
	public void setRedisConfig(String redisUrl) {
		set("redis.serversForCache", redisUrl);
		set("redis.serversForSession", redisUrl);
		set("algo.storage.redis.url", redisUrl);
		set("redismodelcache.enablelua", String.valueOf(true));
	}
	
	/**
	 * MQ配置
	 */
	public void setMqHostConfig(String mqHost, String mqPort, String mqUser, String mqPassword, String mqVhost) {
		String line = System.lineSeparator();
		StringBuffer builder = new StringBuffer();
		builder.append("type=rabbitmq").append(line).append("host=").append(mqHost).append(line).append("port=")
				.append(mqPort).append(line).append("user=").append(mqUser).append(line).append("password=")
				.append(mqPassword).append(line).append("vhost=").append(mqVhost);
		set("mq.server", builder.toString());
	}
	
	/**
	 * 是否启用监控中心日志配置（是否将日志通过kafka上传到日志中心）</br>
	 * 注：如需要启用，请先确保elk、kafka等服务已可用
	 */
	public void setLogConfig(boolean useMonitorLog) {
//		String logConfigXmlContent = null;
		String path = null;
		if(useMonitorLog) {
			//日志通过kafka上传到日志中心
			path = "logback-kafka.xml";
//			logConfigXmlContent = FileUtil.readUtf8String("classpath:logback-kafka.xml");
		} else {
			//本地日志配置
			path = "logback.xml";
//			logConfigXmlContent = FileUtil.readUtf8String("classpath:logback.xml");
		}
		String logConfigXmlContent;
		try {
			logConfigXmlContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), "utf-8");
//			logConfigXmlContent = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(path).toURI())), "UTF-8");
			set("log.config", logConfigXmlContent);
			set("dubbo.application.logger", "slf4j");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Dubbo服务配置
	 * 
	 * @param registerProvider   是否向此注册中心注册服务，如果设为false，将只订阅，不注册
	 * @param registerComsumer   是否向此注册中心订阅服务，如果设为false，将只注册，不订阅
	 * @param lookupLocal   是否使用本地服务查找
	 */
	public void setDubboConfig(boolean registerProvider, boolean registerComsumer, boolean lookupLocal) {
//		set("dubbo.registry.group", group);
		set("dubbo.registry.register", String.valueOf(registerProvider));
		set("dubbo.registry.subscribe", String.valueOf(registerComsumer));
		//dubbo官方资料上没有支持lookupLocal配置，可能是平台扩展的功能，也可能是无效的配置
        set("dubbo.service.lookup.local", String.valueOf(lookupLocal));
//        set("dubbo.registry.protocol", "zookeeper");
//        set("dubbo.registry.address", "");
	}
	
	/**
	 * Dubbo网络配置 
	 * @param ip 默认127.0.0.1
	 * @param port 默认28888
	 * @param qingPort 默认30880
	 */
	public void setDubboHostConfig(String ip, int port, int qingPort) {
		port = getAvailablePort(port);
		qingPort = getAvailablePort(qingPort);
		set("dubbo.protocol.port", String.valueOf(port));
        set("dubbo.consumer.url", "dubbo://" + ip + ":" + port);
        set("dubbo.consumer.url.qing", "dubbo://" + ip + ":" + qingPort);
	}
	
    public int getAvailablePort(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return port;
        } catch (IOException e) {
            return getAvailablePort(port+1);
        }
    }
	
//	/**
//	 * 设置苍穹服务的中间件类型
//	 * @param serverType
//	 */
//    public void setCosmicServerType(CosmicServerType serverType) {
//    	if(serverType == CosmicServerType.springboot) {
//    		set("mservice.booter.type", serverType.name());
//    	} else {
//    		set("webserver.type", serverType.name());
//    	}
//    }
//    
//    enum CosmicServerType{
//    	jetty,tomcat,aas,springboot;
//    }
    
	private static String getLocalHostName() {
		InetAddress localhost = null;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return "UnknownHost";
		}
		return localhost.getHostName();
	}
	
	public static String getCosmicHome(){
        return getCosmicHome(PROJECT_HOME);
    }
	
    public static String getCosmicHome(String projectPath){
    	String cosmicHome = getCosmicGradleProp(projectPath, GRADLE_PROPERTIES_COSMIC_HOME);
    	if(StringUtils.isBlank(cosmicHome)) {
    		cosmicHome = System.getenv(COSMIC_HOME);
    	}
    	if(StringUtils.isBlank(cosmicHome)) {
    		cosmicHome = DEFAULT_COSMIT_HOME_PATH;
    	}
        return cosmicHome;
    }
	
    private static String getCosmicGradleProp(String projectPath, String key) {
    	String gradleConfigPath = projectPath + "/gradle.properties";
    	Properties prop = new Properties();
    	try {
    		File configFile = new File(gradleConfigPath);
    		if(configFile.exists()) {
    			FileReader fr = new FileReader(configFile);
    			prop.load(fr);
    			fr.close();
    			return prop.getProperty(key);
    		} else {
    			return null;
    		}
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}
    }
}