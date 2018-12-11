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
public class DocCategoryManageController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 添加文件类别
     * @param request
     * @return
     */
    @PostMapping(value = "/DocCategoryManage/addDocCategory")
    @ResponseBody
    public String addDocCategory(HttpServletRequest request)
    {
        String docCategoryName = request.getParameter("docCategoryName");
        String parentCode = request.getParameter("parentCode");
        if (docCategoryName!=null&&parentCode!=null)
        {
            Future<String> future = asyncTask.addDocCategory(docCategoryName,parentCode);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        return null;
    }

    /**
     * 删除文件类别
     * @param request
     * @return
     */
    @PostMapping(value = "/DocCategoryManage/deleteDocCategory")
    @ResponseBody
    public String deleteDocCategory(HttpServletRequest request)
    {
        String docCategoryId = request.getParameter("docCategoryId");
        if (docCategoryId!=null)
        {
            Future<String> future = asyncTask.deleteDocCategory(docCategoryId);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        return null;
    }

    /**
     * 获取文件类别树形结构
     * @return
     */
    @PostMapping(value = "/getDocCategoryInfo")
    @ResponseBody
    public String getDocCategoryInfo()
    {
        Future<String> future = asyncTask.getDocCategoryInfo();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
