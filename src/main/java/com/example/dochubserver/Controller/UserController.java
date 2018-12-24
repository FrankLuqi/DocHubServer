package com.example.dochubserver.Controller;
import com.example.dochubserver.async.AsyncTask;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Future;


@Controller
@CrossOrigin
public class UserController {

    @Autowired
    AsyncTask asyncTask;

    @Value("${com.DOMAIN}")
    private String DOMAIN;


    @Value("${com.docDir}")
    private String docDir;

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

    @PostMapping(value = "/getUsers")
    @ResponseBody
    public String getUser(HttpServletRequest request)
    {
        Future<String> future = asyncTask.getUser();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

    @PostMapping(value = "/addOwendRole")
    @ResponseBody
    public String addOwendRole(HttpServletRequest request)
    {
        String userId = request.getParameter("userId");
        String departmentId = request.getParameter("departmentId");
        String roleId = request.getParameter("roleId");
        Future<String> future = asyncTask.addOwnedRole(userId,departmentId,roleId);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

    @PostMapping(value = "/deleteOwendRole")
    @ResponseBody
    public String deleteOwendRole(HttpServletRequest request)
    {
        String owendRoleId = request.getParameter("owendRoleId");
        Future<String> future = asyncTask.deleteOwnedRole(owendRoleId);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

    @PostMapping(value = "/deleteUser")
    @ResponseBody
    public String deleteUser(HttpServletRequest request)
    {
        String userId = request.getParameter("userId");
        Future<String> future = asyncTask.deleteUser(userId);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

    /**
     * 修改用户头像
     * @param request
     * @return
     */
    @PostMapping(value = "/uploadUserface")
    @ResponseBody
    public String uploadUserface(HttpServletRequest request)
    {
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        Future<String> future = asyncTask.uoloadUserface(request,userId);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }


    @RequestMapping("/userface")
    public void getImage(@RequestParam("name") String name, HttpServletResponse response)
    {
        try{
            int dotpos = name.lastIndexOf(".");
            String fileext = name.substring(dotpos+1).toLowerCase();
            File file = new File(docDir+name);
            StreamUtils.copy(new FileInputStream(file),response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
