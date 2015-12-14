package com.sunspot.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

//商品分类
@Entity
@Table(name = "category", catalog = "limmai")
public class Category implements java.io.Serializable
{
	private String categoryId;//id
	private String categoryName;//分类名
	private int lft;//左值
	private int rgt;//右值
	private int depth;//节点深度  数据库表里不存放该字段
	
	/**
	 * default constructor
	 */
	public Category() 
	{
	}
	
	
	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName="
				+ categoryName + ", lft=" + lft + ", rgt=" + rgt + ", depth="
				+ depth + "]";
	}


	/**
	 * full fields constructor
	 */
	public Category(String categoryId, String categoryName, int lft, int rgt, int depth) 
	{

		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.lft = lft;
		this.rgt = rgt;
		this.depth = depth;
	}


	 @GenericGenerator(name = "generator", strategy = "uuid2")
	 @Id
	 @GeneratedValue(generator = "generator")
	 @Column(name = "CATEGORY_ID", unique = true, nullable = false, length = 36)
	public String getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	@Column(name="CATEGORY_NAME" ,length=40)
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	@Column(name="LFT")
	public int getLft() {
		return lft;
	}
	public void setLft(int lft) {
		this.lft = lft;
	}
	
	@Column(name="RGT")
	public int getRgt() {
		return rgt;
	}
	public void setRgt(int rgt) {
		this.rgt = rgt;
	}
	
	@Column(name="DEPTH")
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}     
	
}
