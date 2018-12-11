package com.example.dochubserver.Controller;
import com.example.dochubserver.async.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;


@Controller
@CrossOrigin
public class UserController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 注册
     * @return
     * @RequestParam("username") String username,@RequestParam("password") String password
     */
    @RequestMapping(value = "/user" ,method = {RequestMethod.POST})
    @ResponseBody
    public String reg(HttpServletRequest request)
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Future<String> future = asyncTask.Register(username,password);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }


    /**
     * 提供接口检查用户名是否已被注册或者存在敏感词
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkUsername",method = {RequestMethod.POST})
    @ResponseBody
    public String checkUsername(HttpServletRequest request)
    {
        String username = request.getParameter("username");
        Future<String> future = asyncTask.checkSensitiveWord(username);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
