package com.example.dochubserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.example.dochubserver.bean.*;
import com.example.dochubserver.repository.DocCategoryRepository;
import com.example.dochubserver.repository.DocPermissionRepository;
import com.example.dochubserver.repository.DocRepository;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class DocService {

    @Autowired
    DocRepository docRepository;

    @Autowired
    DocPermissionRepository docPermissionRepository;

    @Autowired
    DocPermissionService docPermissionService;

    @Autowired
    OwendRoleService owendRoleService;

    @Autowired
    UserService userService;

    @Autowired
    DocCategoryRepository docCategoryRepository;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    RoleService roleService;


    public List<Doc> findDocsByUploadUserId(Long userId)
    {
        return docRepository.findByUploadUserid(userId);
    }

    public void deleteDocById(Long docId)
    {
        docRepository.deleteById(docId);
    }
    @Value("${com.DOMAIN}")
    private String DOMAIN;


    @Value("${com.docDir}")
    private String docDir;

    /**
     * 上传文件
     * @param request
     * @param userId
     * @param docCategoryId
     * @param powers
     * @return
     */
    public Map<String,Object> uploadDoc(HttpServletRequest request, Long userId, Long docCategoryId, String[] powers)
    {
        Map<String,Object> map = new HashMap<>();
        Part part = null;
        try{
            part = request.getPart("file");
        }catch (Exception e)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","文件上传失败");
            return map;
        }
        Doc doc = new Doc();
        //得到上传的文件名找到图片后缀名点的位置
        int dotpos = part.getSubmittedFileName().lastIndexOf(".");
        if (dotpos<=0)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","文件类型不合法");
            return map;
        }
        //得到后缀名并将大写转换为小写
        String fileext = part.getSubmittedFileName().substring(dotpos+1).toLowerCase();

        String filename = UUID.randomUUID().toString().replaceAll("-","")+"."+fileext;
        try{
            //保存文件到本地
            Files.copy(part.getInputStream(),new File(docDir+filename).toPath(),StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","上传失败");
            return map;
        }
        doc.setDownloadUrl(filename);//设置下载地址（设置文件名，下载地址就是读取本机文件存储地址加上文件名）
        doc.setType(UsuallyUtil.isFileAllowed(fileext));
        if (doc.getType()!="其他文件") //说明该文件类型可以预览
        {
            if (doc.getType()=="office文档") //将doc、ppt文件转pdf给前端提供预览
            {
                try{
                    String filename2 = UUID.randomUUID().toString().replaceAll("-","")+".pdf";
                    File outputFile = new File(docDir+filename2);
                    OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1",8100);
                    connection.connect();
                    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
                    converter.convert(new File(docDir+filename),outputFile);
                    doc.setPreviewUrl(filename2);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
//                doc.setPreviewUrl(DOMAIN+"preview?name="+filename);
                doc.setPreviewUrl(filename);
            }
        }

        doc.setDocName(part.getSubmittedFileName());
        doc.setCategoryId(docCategoryId);
        doc.setCategory(docCategoryRepository.findById(docCategoryId).get().getName());
        doc.setDate(new Date());
        doc.setDownloads(Long.parseLong("0"));
        doc.setUploadUserid(userId);
        doc.setUploadUser(userService.findByUserId(userId).getUsername());
        if (powers.length==1&&powers[0].equals(""))
            doc.setOpen(1); // 文件公开
        else
            doc.setOpen(0); //文件特定部门角色才能访问

        doc = docRepository.save(doc);

        if (doc.getOpen()==0)
        {
            StringBuffer stringBuffer = new StringBuffer();
            for (String p:powers)
            {
                Power power = JSON.parseObject(p,Power.class);
                if (!power.departmentId.equals(""))
                    stringBuffer.append(departmentsService.findDepartmentById(Long.parseLong(power.departmentId)).getName());
                if (!power.roleId.equals(""))
                    stringBuffer.append(roleService.findRoleById(Long.parseLong(power.roleId)).getName());
                stringBuffer.append("  ");
                docPermissionService.addDocPermission(doc.getId(),!power.roleId.equals("")?Long.parseLong(power.roleId):null,!power.departmentId.equals("")?Long.parseLong(power.departmentId):null,power.powerName);
            }
            doc.setPermission(stringBuffer.toString());
        }
        docRepository.save(doc);
        map.put("code",ResponseType.Success);
        map.put("msg","上传成功");
        return map;
     }


    /**
     * 文件下载
     * @param response
     * @param docId
     * @param userId
     * @return
     */
     public Map<String,Object> downloadDoc(HttpServletResponse response,Long docId,Long userId)
     {
        Map<String,Object> map = new HashMap<>();
        Doc doc = docRepository.findById(docId).get();
        if (doc==null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该文件不存在");
            return map;
        }
        if (doc.getOpen()==0&&doc.getUploadUserid()!=userId)
        {
            Boolean hasPermisson = false;
            List<DocPermission> docPermissions = docPermissionService.findByDocId(docId);
            List<OwnedRole> ownedRoleList = owendRoleService.findRoleByUserId(userId);
            if (docPermissions!=null)
            {
                if (ownedRoleList!=null)
                {
                    for (DocPermission docPermission:docPermissions)
                    {
                        for (OwnedRole ownedRole : ownedRoleList)
                        {
                            if (UsuallyUtil.hasPower(ownedRole.getDepartmentRoleId(),docPermission.getDepartmentRoleId()))
                            {
                                hasPermisson = true;
                                break;
                            }
                        }
                        if (hasPermisson == true)
                            break;
                    }
                }
            }
            else
                hasPermisson = true;

            if (hasPermisson == false)
            {
                map.put("code",ResponseType.Error);
                map.put("msg","没有该文件的权限");
                return map;
            }
        }
        String downloadFilePath = docDir+doc.getDownloadUrl();
        String fileName = doc.getDocName();
        doc.setDownloads(doc.getDownloads()+1);
        docRepository.save(doc);

        File file = new File(downloadFilePath);
        if (file.exists())
        {
            response.setContentType("application/force-download");//设置强制下载不打开
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try{
                response.addHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(fileName, "UTF-8"));//设置下载文件名
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                map.put("code",ResponseType.Success);
                map.put("msg","文件下载成功");
                return map;
            }catch (Exception e)
            {
                map.put("code",ResponseType.Error);
                map.put("msg","文件下载出错");
                return map;
            }finally {
                if (bis != null) {
                    try{
                        bis.close();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (fis !=null)
                {
                    try{
                        fis.close();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            map.put("code",ResponseType.Error);
            map.put("msg","文件不存在");
            return map;
        }
     }

    /**
     * 获取文件列表
     * @param userId
     * @param himself 是否是获取他所上传的文件列表
     * @return
     */
     public String getDocList(Long userId,Boolean himself,String token)
     {
         List<Doc> docListVisual = new ArrayList<>();
         if (himself==true)//如果是查自己上传的文件
         {
            docListVisual = docRepository.findByUploadUserid(userId);
         }
         else
         {
             List<OwnedRole> ownedRoles = owendRoleService.findRoleByUserId(userId);
             List<Doc> docList = docRepository.findAll();
             for (Doc doc:docList)
             {
                 if (doc.getOpen()==1 || doc.getUploadUserid()==userId)
                     docListVisual.add(doc);
                 else if (doc.getOpen()==0)
                 {
                     List<DocPermission> permissions = docPermissionRepository.findByDocId(doc.getId());
                     boolean contin = true;
                     for (DocPermission docPermission:permissions)
                     {
                         for (OwnedRole ownedRole:ownedRoles)
                         {
                             if (UsuallyUtil.hasPower(ownedRole.getDepartmentRoleId(),docPermission.getDepartmentRoleId()))
                             {
                                 docListVisual.add(doc);
                                 contin = false;
                                 break;
                             }
                         }
                         if (contin == false)
                             break;
                     }
                 }
             }
         }

         String strDateFormat = "yyyy-MM-dd HH:mm:ss";
         SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
         JSONArray array = new JSONArray();
         if (docListVisual.size()!=0)
         {
            for (int i = 0; i < docListVisual.size(); i++)
            {
                Doc doc = docListVisual.get(i);
                doc.setPreviewUrl("http://127.0.0.1:8082/preview?token="+token+"&name="+doc.getPreviewUrl());
                doc.setDownloadUrl("http://127.0.0.1:8082/downloadDoc?token="+token+"&docId="+doc.getId());
                doc.setUploadDate(sdf.format(doc.getDate()));
                array.add(JSON.toJSON(doc));
            }
             return array.toJSONString();
         }
         else
             return null;
     }

    /**
     * 删除文件
     * @param docId
     * @param userId
     * @return
     */
     public Map<String,Object> deleteDoc(Long docId,Long userId)
     {
         Map<String,Object> map = new HashMap<>();
        Doc doc = docRepository.findById(docId).get();
        if (doc.getUploadUserid()!=userId)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","无删除该文件的权限");
            return map;
        }
        File file = new File(docDir+doc.getDownloadUrl());
        if (file.exists())
            file.delete();
        if (doc.getType().equals("office文档"))
        {
            File file1 = new File(docDir+doc.getPreviewUrl());
            if (file1.exists())
                file1.delete();
        }
        docRepository.delete(doc);
         map.put("code",ResponseType.Success);
         map.put("msg","删除成功");
         return map;
     }

    /**
     * 根据文件类别进行文件统计
     * @return
     */
     public String getDocReportByCategory()
     {
         Map<String,Object> map = new HashMap<>();
         List<DocCategory> categories = docCategoryRepository.findAll();
         JSONArray array = new JSONArray();
         for (int i=0;i<categories.size();i++)
         {
             JSONObject object = new JSONObject();
             DocCategory docCategory = categories.get(i);
             List<Doc> docs = docRepository.findByCategoryId(docCategory.getId());
             int downloads = 0;
             for (int j=0;j<docs.size();j++)
             {
                 downloads += docs.get(j).getDownloads();
             }
             object.put("name",docCategory.getName());
             object.put("downloads",downloads);
             object.put("count",docs.size());
             array.add(object);
         }
         return array.toJSONString();
     }

}
