package com.sunspot.service;

import java.util.List;

import com.sunspot.pojo.Category;
import com.sunspot.pojoext.CookbookExt;
import com.sunspot.pojoext.CookbookIndexExt;

/**
 * 商品分类业务接口
 * @author scatlet
 *
 */
public interface CategoryService {
	
	/**
	 * 删除一个分类
	 * @param c 要删除节点的对象
	 */
	public void remove(Category c);
	
	/**
	 * 修改一个分类
	 * @param preId 前一个节点的标识
	 * @param c 要修改节点的对象
	 */
	public void modify(String preId,Category c);
	
	/**
	 * 添加一个分类
	 * @param preId 前一个节点的标识
	 * @param c 要添加节点的对象
	 */
	public void add(String preId,Category c);
	
	/**
	 * 通过节点名查询节点
	 * @param categoryName 节点名
	 * @return
	 */
	public Category getByName(String categoryName);
	/**
	 *  获取某一节点及其子节点下的所有商品
	 * @param categoryName 节点名
	 * @return
	 */
	public List<CookbookIndexExt> getGoodsIndex(String categoryName);
	/**
	 * 获取某一节点及其子节点下的所有商品
	 * @param categoryName 节点名
	 * @return CookbookExt
	 */
	public List<CookbookExt> getGoods(String categoryName);
	
	/**
	 * 某一节点之下的全部分类
	 * @param categoryName 父节点名
	 * @return Category
	 */
	public List<Category> getCategory(String categoryName);
	
	/***
	 * 获取全部分类
	 * @return Category
	 */
	public List<Category> getAll();
	
	/**
	 * 得到孩子节点
	 * @param parent 父节点
	 * @return 孩子节点集
	 */
	public List<Category> getChildNodes(Category parent);
	
	/**
	 * 得到父节点
	 * @param c 子节点
	 * @return
	 */
	public Category getParent(Category c);
}
