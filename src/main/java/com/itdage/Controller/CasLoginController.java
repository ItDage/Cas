package com.itdage.Controller;

import com.itdage.configuration.ShiroCasConfiguration;
import com.itdage.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by huayu on 2018/10/24.
 */
@Controller
@RequestMapping("")
public class CasLoginController {
    /**
     * 一般用不到
     * @param model
     * @return
     */
    @RequestMapping(value="/login",method= RequestMethod.GET)
    public String loginForm(Model model){
        model.addAttribute("user", new User());
//      return "login";
        return "redirect:" + ShiroCasConfiguration.LOGIN_URL;
    }


    @RequestMapping(value = "logout", method = { RequestMethod.GET,
            RequestMethod.POST })
    public String loginout(HttpSession session)
    {
        return "redirect:"+ ShiroCasConfiguration.LOGOUT_URL;
    }
}
