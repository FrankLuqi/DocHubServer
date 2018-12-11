package com.example.dochubserver.Controller;

import com.example.dochubserver.async.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;

@Controller
@CrossOrigin
public class SystemController {

    @Autowired
    AsyncTask asyncTask;

    @PostMapping(value = "/system/getUsableFunction")
    @ResponseBody
    public String getUsableFunction(HttpServletRequest request)
    {
        String userId = (String) request.getAttribute("userId");
        Future<String> future = asyncTask.getUsableFunction(userId);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }
}
