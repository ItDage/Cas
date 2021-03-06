package com.itdage.configuration;

import com.itdage.Dao.UserDao;
import com.itdage.Realm.MyShiroCasRealm;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ShiroCasConfiguration 
 * @Description: shiro + cas配置
 * @author: scy
 * @date: 2018年10月22日 下午2:33:50
 */
@Configuration
public class ShiroCasConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroCasConfiguration.class);

	//cas server地址
	public static final String CAS_SERVER_URL_PREFIX = "https://localhost:8443/cas";
	// cas 登录页面地址
	public static final String CAS_LOGIN_URL = CAS_SERVER_URL_PREFIX + "/login";
	//cas登出页面地址
	public static final  String CAS_LOGOUT_URL = CAS_SERVER_URL_PREFIX + "/logout";
	//当前工程对外提供的服务地址
	public static final String SHIRO_SERVER_URL_PREFIX = "http://localhost:9090";
	//casFilter UrlPattern
	public static final String CAS_FILTER_URL_PATTERN = "/cas";
	//登录地址  ---http://localhost:8080/cas/login?service=http://localhost:9090/cas
	public static final String LOGIN_URL = CAS_LOGIN_URL + "?service=" + SHIRO_SERVER_URL_PREFIX + CAS_FILTER_URL_PATTERN;
	//登出地址   ---http://localhost:8080/cas/logout?service=http://localhost:9090(casserver启用service跳转功能,需要在webapps\cas\WEB-INF\cas.properties文件中启用cas.logout.followServiceRedirects=true)
	public static final String LOGOUT_URL = CAS_LOGOUT_URL + "?service=" + SHIRO_SERVER_URL_PREFIX;
	//登录成功地址
	public static final String LOGIN_SUCCESS = "/home";
	//权限认证失败跳转地址
	public static final String UN_AUTHORIZED_URL = "/error/403.html";


	@Bean
	public EhCacheManager getEhCacheManager(){
		EhCacheManager ehCacheManager = new EhCacheManager();
		ehCacheManager.setCacheManagerConfigFile("classpath:encache-shiro.xml");
		return ehCacheManager;
	}

	@Bean(name = "myShiroCasRealm")
	public MyShiroCasRealm myShiroCasRealm(EhCacheManager cacheManager) {
		MyShiroCasRealm realm = new MyShiroCasRealm();
		realm.setCacheManager(cacheManager);
		//realm.setCasServerUrlPrefix(ShiroCasConfiguration.casServerUrlPrefix);
		// 客户端回调地址
		//realm.setCasService(ShiroCasConfiguration.shiroServerUrlPrefix + ShiroCasConfiguration.casFilterUrlPattern);
		return realm;
	}

	/**
	 * 注册单点登出listener
	 * @return
	 */
	@Bean
	public ServletListenerRegistrationBean singleSignOutHttpSessionListener(){
		ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
		bean.setListener(new SingleSignOutHttpSessionListener());
//        bean.setName(""); //默认为bean name
		bean.setEnabled(true);
		//bean.setOrder(Ordered.HIGHEST_PRECEDENCE); //设置优先级
		return bean;
	}

	/**
	 * 注册单点登出filter
	 * @return
	 */
	@Bean
	public FilterRegistrationBean singleSignOutFilter(){
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setName("singleSignOutFilter");
		bean.setFilter(new SingleSignOutFilter());
		bean.addUrlPatterns("/*");
		bean.setEnabled(true);
		//bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	/**
	 * 注册DelegatingFilterProxy（Shiro）
	 *
	 * @return
	 */
	@Bean
	public FilterRegistrationBean delegatingFilterProxy() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		//  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
		filterRegistration.addInitParameter("targetFilterLifecycle", "true");
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroCasRealm myShiroCasRealm) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		dwsm.setRealm(myShiroCasRealm);
//      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 -->
		dwsm.setCacheManager(getEhCacheManager());
		// 指定 SubjectFactory
		dwsm.setSubjectFactory(new CasSubjectFactory());
		return dwsm;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	/**
	 * CAS过滤器
	 *
	 * @return
	 * @author SHANHY
	 * @create  2016年1月17日
	 */
	@Bean(name = "casFilter")
	public CasFilter getCasFilter() {
		CasFilter casFilter = new CasFilter();
		casFilter.setName("casFilter");
		casFilter.setEnabled(true);
		// 登录失败后跳转的URL，也就是 Shiro 执行 CasRealm 的 doGetAuthenticationInfo 方法向CasServer验证tiket
		casFilter.setSuccessUrl(SHIRO_SERVER_URL_PREFIX  + LOGIN_SUCCESS);
		casFilter.setFailureUrl(LOGIN_URL);// 我们选择认证失败后再打开登录页面
		return casFilter;
	}

	/**
	 * ShiroFilter<br/>
	 * 注意这里参数中的 StudentService 和 IScoreDao 只是一个例子，因为我们在这里可以用这样的方式获取到相关访问数据库的对象，
	 * 然后读取数据库相关配置，配置到 shiroFilterFactoryBean 的访问规则中。实际项目中，请使用自己的Service来处理业务逻辑。
	 *
	 * @param securityManager
	 * @param casFilter
	 * @return
	 * @author SHANHY
	 * @create  2016年1月14日
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager, CasFilter casFilter) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl(LOGIN_URL);
		// 登录成功后要跳转的连接
		shiroFilterFactoryBean.setSuccessUrl(LOGIN_SUCCESS);
		shiroFilterFactoryBean.setUnauthorizedUrl(UN_AUTHORIZED_URL);
		// 添加casFilter到shiroFilter中
		Map<String, Filter> filters = new HashMap<>();
		filters.put("casFilter", casFilter);
//		filters.put("logout",logoutFilter());
		shiroFilterFactoryBean.setFilters(filters);

		loadShiroFilterChain(shiroFilterFactoryBean);
		return shiroFilterFactoryBean;
	}

	/**
	 * 加载shiroFilter权限控制规则（从数据库读取然后配置）,角色/权限信息由MyShiroCasRealm对象提供doGetAuthorizationInfo实现获取来的
	 *
	 * @author SHANHY
	 * @create  2016年1月14日
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean){
		/////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

		// authc：该过滤器下的页面必须登录后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
		// anon: 可以理解为不拦截
		// user: 登录了就不拦截
		// roles["admin"] 用户拥有admin角色
		// perms["permission1"] 用户拥有permission1权限
		// filter顺序按照定义顺序匹配，匹配到就验证，验证完毕结束。
		// url匹配通配符支持：? * **,分别表示匹配1个，匹配0-n个（不含子路径），匹配下级所有路径

		//1.shiro集成cas后，首先添加该规则
		filterChainDefinitionMap.put(CAS_FILTER_URL_PATTERN, "casFilter");
		//filterChainDefinitionMap.put("/logout","logout"); //logut请求采用logout filter

		//2.不拦截的请求
		filterChainDefinitionMap.put("/css/**","anon");
		filterChainDefinitionMap.put("/js/**","anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/logout","anon");
		filterChainDefinitionMap.put("/error","anon");
		//3.拦截的请求（从本地数据库获取或者从casserver获取(webservice,http等远程方式)，看你的角色权限配置在哪里）
		filterChainDefinitionMap.put("/user", "authc"); //需要登录
		filterChainDefinitionMap.put("/user/add/**", "authc,roles[admin]"); //需要登录，且用户角色为admin
		filterChainDefinitionMap.put("/user/delete/**", "authc,perms[\"user:delete\"]"); //需要登录，且用户有权限为user:delete
		filterChainDefinitionMap.put("/cas", "casFilter"); //需要登录，且用户有权限为user:delete

		//4.登录过的不拦截
		filterChainDefinitionMap.put("/**", "user");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}


}
