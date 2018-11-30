package com.itdage.Controller;

import com.itdage.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by huayu on 2018/10/24.
 */
@Controller
public class HomeController {
//    @Autowired
//    UserService userService;

    @RequestMapping("/home")
    public String index(HttpSession session, ModelMap map, HttpServletRequest request){
//        User user = (User) session.getAttribute("user");

        System.out.println(request.getUserPrincipal().getName());
        System.out.println(SecurityUtils.getSubject().getPrincipal());

//        User loginUser = userService.getLoginUser();
//        System.out.println(JSONObject.toJSONString(loginUser));

//        map.put("user",loginUser);
        return "home";
    }
}
