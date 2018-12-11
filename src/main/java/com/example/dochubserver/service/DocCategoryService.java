package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Departments;
import com.example.dochubserver.bean.Doc;
import com.example.dochubserver.bean.DocCategory;
import com.example.dochubserver.repository.DocCategoryRepository;
import com.example.dochubserver.repository.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocCategoryService {

    @Autowired
    DocCategoryRepository docCategoryRepository;

    @Autowired
    DocRepository docRepository;

    public DocCategory save(DocCategory docCategory)
    {
        return docCategoryRepository.save(docCategory);
    }

    public void delete(long docCategoryId)
    {

        docCategoryRepository.deleteById(docCategoryId);
    }

    /**
     * 生成文件类别编码
     * @param parentCode
     * @param DocCategoryId
     * @return
     */
    public String generateDocCategoryCode(String parentCode, long DocCategoryId)
    {
        if (parentCode == "")
            return String.valueOf(DocCategoryId)+"D";
        else
            return parentCode+String.valueOf(DocCategoryId)+"D";
    }


    /**
     * 添加文件类别
     * @param docCategoryName
     * @param parentCode
     * @return
     */
    public Map<String,Object> addDocCategory(String docCategoryName, String parentCode)
    {
        DocCategory docCategory = new DocCategory();
        docCategory.setName(docCategoryName);
        docCategory = save(docCategory);
        long id = docCategory.getId();
        String code = generateDocCategoryCode(parentCode,id);//生成部门编码
        docCategory.setCode(code);
        save(docCategory);

        Map<String,Object> map = new HashMap<>();
        map.put("id",String.valueOf(id));
        map.put("code",code);
        map.put("name",docCategoryName);
        return map;
    }

    /**
     * 普通文件类不能删除
     * 删除文件类别，由于文件不能没有文件类别，相关文件的类别需要更改为普通文件
     * @param id
     * @return
     */
    public Map<String,Object> deleteDocCategory(long id)
    {
        Map<String,Object> map = new HashMap<>();
        try{
            if (id==1)//为了防止出现所有文件类别均被删除导致文件不存在类别，普通文件类不能删，并且文件原有类别被删除后改文件类别更改为普通文件类
            {
                map.put("state","Error");
                map.put("msg","该类别不能删除");
            }
            delete(id);

            List<Doc> docs1 = docRepository.findByCategoryId(id);
            for (Doc doc: docs1)
            {
                doc.setCategoryId(Long.parseLong("1"));
                docRepository.save(doc);
            }

            List<DocCategory> list = docCategoryRepository.findAll();
            StringBuffer childrenName = new StringBuffer();//存储其被级联删除的子节点名称
            for (DocCategory docCategory:list)
            {
                String code = docCategory.getCode();
                String[] codes = code.split(("D"));
                for (String s:codes)
                {
                    if (s.equals(String.valueOf(id)))
                    {
                        childrenName.append(docCategory.getName()+"");
                        //更改相关文件文件类别为普通文件（文件类别id=1）
                        List<Doc> docs = docRepository.findByCategoryId(docCategory.getId());
                        for (Doc doc : docs)
                        {
                            doc.setCategoryId(Long.parseLong("1"));
                            docRepository.save(doc);
                        }
                        delete(docCategory.getId());//级联删除
                        break;
                    }
                }
            }

            map.put("status","Success");
            if (childrenName.toString().equals(""))
                map.put("msg","删除成功，相关文件的文件类别更改为普通文件");
            else
                map.put("msg","删除成功，相关文件的文件类别更改为普通文件,以下子节点也被删除 "+childrenName);
        }catch (Exception e)
        {
            map.put("status","Error");
            map.put("msg","删除失败");
        }
        return map;
    }

    /**
     * 获取文件类别树形信息
     * @return
     */
    public String getDocCategoryInfo()
    {
        //用hashmap构建文件类别前缀树
        Map<String,List<DocCategory>> nodeMap = new HashMap<>();//map存储父节点，该节点下的子节点列表

        List<DocCategory> docCategoryList = docCategoryRepository.findAll();
        for (DocCategory docCategory : docCategoryList)
        {
            String code = docCategory.getCode();
            String[] codes = code.split("D");
            if (codes.length==1)//说明这个文件类别没有父节点
            {
                if (nodeMap.get("0") == null)
                {
                    List<DocCategory> nodelist = new ArrayList<>();
                    nodelist.add(docCategory);
                    nodeMap.put("0",nodelist);
                }
                else
                {
                    nodeMap.get("0").add(docCategory);
                }
            }
            else if (codes.length > 1)
            {
                String parentid = codes[codes.length-2];
                if (nodeMap.get(parentid) == null)
                {
                    List<DocCategory> nodelist = new ArrayList<>();
                    nodelist.add(docCategory);
                    nodeMap.put(parentid,nodelist);
                }
                else
                {
                    nodeMap.get(parentid).add(docCategory);
                }
            }
        }


        return getTreeByParent("0",nodeMap).toJSONString();
    }

    /**
     * 获取部门信息
     * @param parentId 父节点id 如果要获取整棵树父节点id输入0
     * @param map 部门前缀树
     * @return
     */
    private JSONArray getTreeByParent(String parentId, Map<String,List<DocCategory>> map){
        JSONArray array = new JSONArray();
        List<DocCategory> docCategoryList = map.get(parentId);
        for (DocCategory docCategory: docCategoryList)
        {
            JSONObject obj = new JSONObject();
            obj.put("value",docCategory.getId());
            obj.put("label",docCategory.getName());
            if (map.get(String.valueOf(docCategory.getId())) != null)
                obj.put("children",getTreeByParent(String.valueOf(docCategory.getId()),map));
            obj.put("code",docCategory.getCode());
            array.add(obj);
        }
        return array;
    }

}
