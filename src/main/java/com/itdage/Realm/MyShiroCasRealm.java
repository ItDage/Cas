package com.itdage.Realm;

import com.itdage.Dao.UserDao;
import com.itdage.configuration.ShiroCasConfiguration;
import com.itdage.entity.User;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by huayu on 2018/10/24.
 * 安全数据源
 */
@Component
public class MyShiroCasRealm extends CasRealm{

    private static final Logger logger = LoggerFactory.getLogger(MyShiroCasRealm.class);

//    @Autowired
//    private UserDao userDao;

    @PostConstruct
    public void initProperty(){
        setCasServerUrlPrefix(ShiroCasConfiguration.CAS_SERVER_URL_PREFIX);
        //客户端回调地址
        setCasService(ShiroCasConfiguration.SHIRO_SERVER_URL_PREFIX + ShiroCasConfiguration.CAS_FILTER_URL_PATTERN);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection){
        logger.info("############执行shiro权限验证#############");
        //获取当前登录输入的用户名
        String loginName = (String) super.getAvailablePrincipal(principalCollection);
        System.out.println(loginName);
        //到数据库查询是否有这个对象
//        User user = userDao.getByName(loginName);
        User user = new User();
        user.setName("s");
        if(user!=null){
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
            //给用户添加角色（让shiro去验证）
            Set<String> roleNames = new HashSet<>();
            if(user.getName().equals("boy5")){
                roleNames.add("admin");
            }
            info.setRoles(roleNames);

            if(user.getName().equals("李四")){
                //给用户添加权限（让shiro去验证）
                info.addStringPermission("user:delete");
            }

            // 或者按下面这样添加
            //添加一个角色,不是配置意义上的添加,而是证明该用户拥有admin角色
//            simpleAuthorInfo.addRole("admin");
            //添加权限
//            simpleAuthorInfo.addStringPermission("admin:manage");
//            logger.info("已为用户[mike]赋予了[admin]角色和[admin:manage]权限");
            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }
}
