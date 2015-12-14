package com.sunspot.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunspot.dao.BaseDao;
import com.sunspot.pojo.Category;
import com.sunspot.pojoext.CookbookExt;
import com.sunspot.pojoext.CookbookIndexExt;
import com.sunspot.service.CategoryService;

/**
 * 商品分类业务层实现
 * 
 * @author scatlet
 * 
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	/**
	 * 持久层操作类
	 */
	@Autowired
	private BaseDao baseDao;

	// 统计所有
	private static final String COUNT_ALL_COOKBOOK = "select count(1) from cookbook a left join shop b on a.of_shop_id=b.shop_id left join area c on b.of_area_id = c.area_id";

	// 查询所有
	private static final String QUERY_ALL_COOKBOOK = "select a.cookbooks_id,a.logo,a.of_shop_id,a.cook_name,a.cook_type,a.marks,a.price,a.type_name,a.suggest,a.is_sale,a.sale_day,a.sale_price,a.is_dis,a.dis_price,a.begin_time,a.end_time,a.dis_num,a.order_num,a.is_shel,b.shop_name shopName,b.telphone,b.logo shopLogo,c.area_name areaName a.category_name from cookbook a left join shop b on a.of_shop_id=b.shop_id left join area c on b.of_area_id = c.area_id";

	/**
	 * 树状节点的特点： 1.每一个节点都有一个左右值。 2.如果右值-左值=1，则代表当前节点为叶子节点。
	 * 3.如果右值-左值>1，则代表当前节点有孩子节点，值在左右值之间的所有节点，即为当前结点的所有孩子节点。
	 */

	/**
	 * @author scatlet
	 */
	@Override
	public void remove(Category c) {
		String sql = "UPDATE category SET lft=lft-2 WHERE lft>?";
		baseDao.update(sql, new Object[] { c.getLft() });
		sql = "UPDATE category SET rht=rht-2 WHERE rht>?";
		baseDao.update(sql, new Object[] { c.getRgt() });
	}

	/**
	 * @author scatlet
	 */
	@Override
	public void modify(String preName, Category c) 
	{
		Category oldPre = getParent(c);
		Category newPre = getByName(preName);
		System.out.println("older--"+oldPre.toString());
		System.out.println("newPre--"+newPre.toString());
		if (newPre==null) 
		{
			baseDao.update(c);
		} 
		else 
		{
			c.setLft(newPre.getRgt());
			c.setRgt(c.getLft() + 1);
			String sql = "UPDATE category SET lft=lft+2,rgt=rgt+2 WHERE lft>? AND rgt<?";
			baseDao.update(sql,
					new Object[] { newPre.getRgt(), oldPre.getRgt() });
			newPre.setRgt(c.getRgt() + 1);
			baseDao.update(newPre);
			oldPre.setLft(oldPre.getLft()+2);
			baseDao.update(oldPre);
			baseDao.update(c);
		}

	}

	/**
	 * @author scatlet
	 */
	@Override
	public void add(String preId, Category c) {
		Category pre = baseDao.getByHibernate(Category.class, preId);

		String sql = "SELECT category_id,category_name,lft,rgt "
				+ "FROM Category "
				+ "WHERE lft>? AND rgt<?";
		c.setLft(pre.getRgt());
		c.setRgt(c.getLft()+1);
		sql = "UPDATE category SET lft=lft+2 WHERE lft>?";
		baseDao.update(sql, new Object[] { pre.getRgt() });
		sql = "UPDATE category SET rgt=rgt+2 WHERE rgt>?";
		baseDao.update(sql, new Object[] { pre.getRgt()});
		pre.setRgt(pre.getRgt()+2);
		baseDao.update(pre);
		baseDao.add(c);
	}

	/**
	 * @author scatlet
	 */
	@Override
	public Category getByName(String categoryName) {
		String sql = "SELECT category_id,category_name,lft,rgt "
				+ "FROM category "
				+ "WHERE category_name=?";
		Category category = baseDao.queryForObject(sql,
				new Object[] { categoryName }, Category.class);
		return category;
	}

	/**
	 * @author scatlet
	 */
	@Override
	public List<CookbookIndexExt> getGoodsIndex(String categoryName) {
		String querySql = QUERY_ALL_COOKBOOK + " AND category_name=?";
		List<CookbookIndexExt> list = baseDao.query(querySql,
				new Object[] { categoryName }, CookbookIndexExt.class);
		return list;
	}

	@Override
	public List<CookbookExt> getGoods(String categoryName) {

		return null;
	}

	/**
	 * @author scatlet
	 */
	@Override
	public List<Category> getCategory(String categoryName) {
		String sql = "SELECT * FROM category WHERE category_name=?";
		Category parent = baseDao.queryForObject(sql,
				new Object[] { categoryName }, Category.class);
		sql = "SELECT category_id,category_name,lft,rgt,COUNT(category_name) depth "
				+ "FROM category WHERE lft>=? AND rgt<=? "
				+ "GROUP BY(child.category_name) "
				+ "ORDER BY child.lft;";
		List<Category> list = baseDao.query(sql, new Object[] {
				parent.getLft(), parent.getRgt() }, Category.class);
		return list;
	}

	/**
	 * @author scatlet
	 */
	@Override
	public List<Category> getAll() {
		String sql = "SELECT child.category_id,child.category_name,child.lft,child.rgt,COUNT(child.category_name) depth "
				+ "FROM category parent,category child "
				+ "WHERE child.lft>=parent.lft AND child.rgt<=parent.rgt "
				+ "GROUP BY(child.category_name) "
				+ "ORDER BY child.lft;";
		return baseDao.query(sql, Category.class);
	}
	
	/**
	 * 得到父节点
	 * @param c 子节点
	 * @return
	 */
	public Category getParent(Category c)
	{
		String sql = "SELECT parent.category_id,parent.category_name,parent.lft,parent.rgt,COUNT(parent.category_name) depth "
				+ "FROM category parent,category child WHERE ?>parent.lft AND ?<parent.rgt "
				+ "GROUP BY(parent.category_name) "
				+ "ORDER BY parent.lft;";
		List<Category> list=baseDao.query(sql, new Object[]{c.getLft(),c.getRgt()}, Category.class);
		if(list.size()>0)
			return list.get(list.size()-1);
		return null;
	}
	
	/**
	 * 得到孩子节点
	 * @param parent 父节点
	 * @return 孩子节点集
	 */
	public List<Category> getChildNodes(Category parent)
	{
		int depth=0;	//第一个孩子的深度，初始为0
		List<Category> childNodes=new ArrayList<Category>();
		String sql="SELECT child.category_name,child.lft,child.rgt,COUNT(child.category_name) depth "
				+ "FROM category child,category parent "
				+ "WHERE child.lft>=parent.lft AND child.rgt<=parent.rgt AND child.lft>? AND child.rgt<? "
				+ "GROUP BY child.category_name "
				+ "ORDER BY child.lft;";
		List<Category> list=baseDao.query(sql, new Object[]{parent.getLft(),parent.getRgt()}, Category.class);
		if(list.size()>0)
		{
			depth=list.get(0).getDepth();
			for(Category c:list)
			{
				if(c.getDepth()==depth)
					childNodes.add(c);
			}
		}
		if(childNodes.size()>0)
			return childNodes;
		return null;
	}
}
