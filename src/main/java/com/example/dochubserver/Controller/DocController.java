package com.example.dochubserver.Controller;

import com.example.dochubserver.async.AsyncTask;
import com.example.dochubserver.service.DocService;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.concurrent.Future;

@Controller
@CrossOrigin
public class DocController {

    @Value("${com.docDir}")
    private String docDir;

    @Autowired
    AsyncTask asyncTask;

    @Autowired
    DocService docService;

    /**
     * 上传文件
     * @param request
     * @return
     */
    @PostMapping(value = "/uploadDoc")
    @ResponseBody
    public String uploadDoc(HttpServletRequest request)
    {
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        String[] powers = requestParameterMap.get("powerList");
        Long docId = Long.parseLong(requestParameterMap.get("docCategory")[0]);
        String userId = (String)request.getAttribute("userId");
        Future<String> future = asyncTask.uploadDoc(request,Long.parseLong(userId),docId,powers);
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

    /**
     *  下载文件
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/downloadDoc")
    @ResponseBody
    public String downloadDoc(HttpServletRequest request,HttpServletResponse response)
    {
        Long userId = Long.parseLong((String)request.getAttribute("userId"));
        String docId = request.getParameter("docId");
        if (docId!=null&&userId!=null)
        {
            Future<String> future = asyncTask.downloadDoc(response,Long.parseLong(docId),userId);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        else
            return null;
    }

    /**
     * 预览文件
     * @param name
     * @param response
     */
    @RequestMapping("/preview")
    public void getImage(@RequestParam("name") String name, HttpServletResponse response)
    {
        try{
            int dotpos = name.lastIndexOf(".");
            String fileext = name.substring(dotpos+1).toLowerCase();
            String fileType = UsuallyUtil.isFileAllowed(fileext);
            if (fileType == "图片")
                response.setContentType("image/jpeg");
            else if (fileType == "pdf文档")
                response.setContentType("application/pdf");
            else if (fileType == "视频")
                response.setContentType("video/mpeg4");
            File file = new File(docDir+name);
            StreamUtils.copy(new FileInputStream(file),response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
