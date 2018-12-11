package com.example.dochubserver.Controller;

import com.example.dochubserver.async.AsyncTask;
import com.example.dochubserver.async.EventModel;
import com.example.dochubserver.async.EventProducer;
import com.example.dochubserver.utils.JedisAdapter;
import com.example.dochubserver.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;


@CrossOrigin
@Controller
public class LoginController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 登录
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public String login()
    {
        return "hah";
    }

    @PostMapping(value = "/logoutt")
    @ResponseBody
    public String logoutt(HttpServletRequest request)
    {
        System.out.println(request.getAttribute("userId"));
        return "hah";
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping(value = "/logout1")
    @ResponseBody
    public String logout(HttpServletRequest request)
    {
        System.out.println(request.getAttribute("userId"));
        Future<String> future = asyncTask.Logout(request);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
