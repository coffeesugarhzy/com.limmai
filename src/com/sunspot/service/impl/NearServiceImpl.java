package com.sunspot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sunspot.common.Utils;
import com.sunspot.dao.BaseDao;
import com.sunspot.pojo.Shop;
import com.sunspot.pojo.UserInfo;
import com.sunspot.pojoext.ShopExt;
import com.sunspot.service.NearService;

/**
 * 
 * 附近店铺服务层接口
 * 
 * @author LuoAnDong
 * 
 */
@Service
@Scope("prototype")
public class NearServiceImpl implements NearService
{ 
    /**
     * 持久层处理
     */
    @Resource
    private BaseDao baseDao;

    /**
     * 店铺所有信息,距离(distance)
     */
    String SHOP_ALL_SQL = "select * from (select a.shop_id,a.is_dis,a.dis_content,a.begin_time,a.end_time, a.type_name,a.is_full, a.of_user_id, a.link_man, a.telphone, a.open_time, a.cost, a.marks, a.logo , a.is_book, a.remark, a.address, a.add_date, a.update_date, a.shop_name ,a.online,a.is_today,a.is_weekly, sqrt(power(((? - a.longitude) * 3.1415926 * 6371229 * cos(((a.latitude + ?)/2)*3.1415926/180)/180),2) + power(((? - a.latitude) * 3.1415926 * 6371229 / 180),2))/1000 as distance from shop a) a ";

    /**
     * 店铺查询　(此处代码需优化)
     * 
     * @param currentSize
     *            当面条数
     * @param pageSize
     *            每页显示条数
     * @param longitude
     *            用户经度
     * @param latitude
     *            　用户纬度
     * @param filterType
     *            查询类型(如1->分类、2->距离、3->评价)
     * @param filter
     *            过滤条件
     * @return List
     * @author LuoAnDong
     */
    @Override
    public List<ShopExt> query(int 
            currentSize, 
            int pageSize, 
            String longitude,
            String latitude, 
            int filterType, 
            String filter)
    {  
        // 分类
         if (filterType == 1)
        {  
            if(StringUtils.isBlank(filter)) filter="%" ; 
            String sql = SHOP_ALL_SQL + "where a.type_name like ? order by a.distance asc limit ?,?"; 
            List<ShopExt> list = baseDao.query(sql, new Object[]
            { longitude, latitude, latitude, filter, currentSize, pageSize },
            ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(getShopStatus(list.get(i)))list.remove(i);
            }
            list=setMarks(list);
            return list ; 
        }
        //距离
        else if (filterType == 2)   
        { 
            if(StringUtils.isBlank(filter)) filter="0" ;  
            String sql = SHOP_ALL_SQL + " where a.distance > ?  order by a.distance asc limit ?,?";  
            List<ShopExt> list = baseDao.query(sql, new Object[]
                                              { longitude, latitude, latitude, filter, currentSize, pageSize },
                                              ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(getShopStatus(list.get(i)))list.remove(i);
            }
            list=setMarks(list);
            return list ; 
        }
        //评价
        else if (filterType == 3)
        {
            /**
             * 1 为desc（高到低） 
             * 0 为asc （低到高）
             */ 
            filter = (StringUtils.isBlank(filter))?"desc":(filter.equals("1")?"desc":"asc") ;  
            
            String sql = SHOP_ALL_SQL + "order by a.marks "+filter+" limit ?,?";  
            List<ShopExt> list = baseDao.query(sql, new Object[]
                                              { longitude, latitude, latitude, currentSize, pageSize },
                                              ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(getShopStatus(list.get(i)))list.remove(i);
            }
            list=setMarks(list);
            return list ; 
        }
        //搜索
        else if(filterType == 4){ 
            if(StringUtils.isBlank(filter)) filter="%" ; 
            String sql = SHOP_ALL_SQL + "where a.shop_name like ? limit ?,?"; 
            List<ShopExt> list = baseDao.query(sql, new Object[]
            { longitude, latitude, latitude, Utils.getKeyword(filter), currentSize, pageSize },
            ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(!getShopStatus(list.get(i)))
            		list.remove(i);
            }
            list=setMarks(list);
            return list ; 
        }else if(filterType == 5){
            if(StringUtils.isBlank(filter)) filter="%" ; 
            String sql = SHOP_ALL_SQL + "where a.type_name like ? and a.is_today=1 order by a.distance asc,a.online desc limit ?,?"; 
            List<ShopExt> list = baseDao.query(sql, new Object[]
            { longitude, latitude, latitude, filter, currentSize, pageSize },
            ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(getShopStatus(list.get(i)))list.remove(i);
            }
            list=setMarks(list);
            return list ; 	
        }else if(filterType == 6){
            if(StringUtils.isBlank(filter)) filter="%" ; 
            String sql = SHOP_ALL_SQL + "where a.type_name like ? and a.is_weekly=1 order by a.distance asc,a.online desc limit ?,?"; 
            List<ShopExt> list = baseDao.query(sql, new Object[]
            { longitude, latitude, latitude, filter, currentSize, pageSize },
            ShopExt.class);
            //剔除被屏蔽的商店
            for(int i=0;i<list.size();i++){
            	if(getShopStatus(list.get(i)))list.remove(i);
            }
            list=setMarks(list);
            return list ; 	
        }

        return null;

    }

    /**
     * 根据id查询店铺所有信息
     * @param shopId 店铺id
     * @return 
     * @author LuoAnDong
     */
    @Override
    public ShopExt query(String longitude , String latitude ,String shopId)
    {
    	ShopExt shopExt=baseDao.queryForObject(
                SHOP_ALL_SQL+" where a.shop_id=?", 
                new Object[]{ longitude, latitude, latitude,shopId}, 
                ShopExt.class) ;
    	//if(getShopStatus(shopExt))map
        return shopExt; 
    }

    /**
     * 通过店铺id查询地图信息
     * @param shopId
     * @return
     * @author LuoAnDong
     */
    @Override
    public ShopExt queryMap(String shopId)
    { 
        return baseDao.queryForObject("select shop_name,telphone,address,longitude,latitude from shop where shop_id=?", 
                new Object[]{shopId}, 
                ShopExt.class);
    }
    
    /**
     * 查询商店是否已被屏蔽或被注销
     * @param shopExt
     */
    public boolean getShopStatus(ShopExt shopExt){
    	UserInfo userInfo=baseDao.getByHibernate(UserInfo.class,shopExt.getOfUserId());
    	if(userInfo.getStatus()==0){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * 根据商店id查询商店是否已被屏蔽或被注销
     * @param shopExt
     * 0正常 1 屏蔽 2 禁言 3 注销
     */
    public boolean getShopStatus(String shopId){
    	Shop shop=baseDao.getByHibernate(Shop.class, shopId);
    	UserInfo userInfo=baseDao.getByHibernate(UserInfo.class,shop.getOfUserId());
    	if(userInfo.getStatus()==0) return true;
    	return false;
    }
    
    /**
     * 从订单里找出商品的评分并计算总分
     * @param shopId
     * @return 平均分 最高5分
     */
   public int getMarks(String shopId)
   {
	   String sql="SELECT marks FROM comments,orders WHERE of_orders_id=ORDER_ID ORDER BY marks AND OF_SHOP_ID=?";
	   List<String> list=baseDao.query(sql, new Object[]{shopId}, String.class);
	   int count=0;
	   count=list.size();
	   
	   return count;
   }
   
   /**
    * 将查询出的商店列表填上评分
    * @param list 已经查询出的商店列表
    * @return
    */
   public List<ShopExt> setMarks(List<ShopExt> list)
   {
	   for(int i=0;i<list.size();i++)
	   {
		   list.get(i).setMarks(getMarks(list.get(i).getShopId()));
	   }
	   return list;
   }
}
