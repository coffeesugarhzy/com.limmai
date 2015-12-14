package com.sunspot.controller.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunspot.pojo.Category;
import com.sunspot.pojoext.DataGridModel;
import com.sunspot.service.CategoryService;

@Controller
@RequestMapping("manager/category")
public class CategoryController 
{	
	/**
     * 注入业务层管理
     */
    @Autowired
	private CategoryService categoryService;
    
    /**
     * 进入增加页面
     */
    @RequestMapping(value = "addCategory", method = RequestMethod.GET)
    public void addCategory(String preId,ModelMap modelMap)
    {
    	modelMap.addAttribute("preId",preId);
    	List<Category> list=categoryService.getAll();
    	modelMap.addAttribute("list", list);
    }
    /**
     * 添加一个实体
     * @param request
     * @param c
     * @param modelMap
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(String preName,Category c,ModelMap modelMap)
    {
    	Integer resultCode = 1;
        try
        {
        	Category pre=categoryService.getByName(preName);
            categoryService.add(pre.getCategoryId(), c);
        }
        catch (Exception e)
        {
            resultCode = 0;
        }
        modelMap.addAttribute("resultCode", resultCode);
        return "/rsp/submitrsp";
    }
    
    /**
     * 进入修改页面
     */
    @RequestMapping(value = "modifyCategory", method = RequestMethod.GET)
    public void toModify(String preId,Category c,ModelMap modelMap)
    {
    	modelMap.addAttribute("preId", preId);
    	modelMap.addAttribute("category", c);
    	List<Category> list=categoryService.getAll();
    	modelMap.addAttribute("list", list);
    }
    
    /**
     * 修改一个实体
     * @param preId
     * @param c
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "modify", method = RequestMethod.POST)
    public String modify(String preName,Category c,ModelMap modelMap)
    {
    	Integer resultCode = 1;
        try
        {	
        	System.out.println("name---"+c.getCategoryName());
        	c=categoryService.getByName(c.getCategoryName());
        	System.out.println(c.toString());
            categoryService.modify(preName, c);
        }
        catch (Exception e)
        {
            resultCode = 0;
        }
        modelMap.addAttribute("resultCode", resultCode);
        return "/rsp/submitrsp";
    }
    
    
    @RequestMapping(value = "listCategory", method = RequestMethod.GET)
    public void getAll(ModelMap modelMap)
    {
    	List<Category> list=categoryService.getAll();
    	modelMap.addAttribute("list", list);
    }
    /**
     * 查询节点
     * @param request
     * @param keyword 0:查询全部  1：某一节点下的全部节点
     * @param categoryName
     * @return
     */
    @ResponseBody
    @RequestMapping("listJson")
    public DataGridModel<Category> listJson(HttpServletRequest request,DataGridModel<Category> list,
    		String keyword, String categoryName)
    {
    	
        if (keyword.equals("0"))
        {
            list.setContent(categoryService.getAll());
        }
        else
        {
        	list.setContent(categoryService.getCategory(categoryName));
        }
        return list;

    }
    
    /**
     * 获取子节点集
     * @param request
     * @param parentName 父节点名
     * @return
     */
    @ResponseBody
    @RequestMapping("childNodesJson")
    public List<Category> getChildNodes(HttpServletRequest request,String parentName)
    {
    	Category parent=categoryService.getByName(parentName);
    	return categoryService.getChildNodes(parent);
    }
}
