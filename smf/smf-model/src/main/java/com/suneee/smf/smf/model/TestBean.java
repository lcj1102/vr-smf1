/**
 * Created By: XI
 * Created On: 2016-11-15 10:05:32
 *
 * Amendment History:
 * 
 * Amended By       Amended On      Amendment Description
 * ------------     -----------     ---------------------------------------------
 *
 **/
package com.suneee.smf.smf.model;



public class TestBean{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	/**
	  *	
	  */
	private Long menuId;

	/**
	  *	菜单名称
	  */
	private String menuname;

	/**
	  *	菜单url
	  */
	private String menuurl;

	/**
	  *	图标
	  */
	private String icon;

	/**
	  *	父菜单ID
	  */
	private Long parentid;

	/**
	  *	1 管理员  2 企业用户
	  */
	private Short usertype;

	/**
	  *	菜单排序序号
	  */
	private Integer positions;

	/**
	  *	添加人
	  */
	private String creater;

	/**
	  *	添加时间
	  */
	private String createtime;

	/**
	  *	更新人
	  */
	private String updater;

	/**
	  *	更新时间
	  */
	private String updatetime;

	public TestBean(){}

	public TestBean(Long menuId){
		this.menuId = menuId;
	}

	/**
	  *	
	  */
	public Long getMenuId()
	{
		return menuId;
	}

	/**
	  *
	  */
	public void setMenuId(Long menuId)
	{
		this.menuId = menuId;
	}

	/**
	  *	菜单名称
	  */
	public String getMenuname()
	{
		return menuname;
	}

	/**
	  *	菜单名称
	  */
	public void setMenuname(String menuname)
	{
		this.menuname = menuname;
	}

	/**
	  *	菜单url
	  */
	public String getMenuurl()
	{
		return menuurl;
	}

	/**
	  *	菜单url
	  */
	public void setMenuurl(String menuurl)
	{
		this.menuurl = menuurl;
	}

	/**
	  *	图标
	  */
	public String getIcon()
	{
		return icon;
	}

	/**
	  *	图标
	  */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	  *	父菜单ID
	  */
	public Long getParentid()
	{
		return parentid;
	}

	/**
	  *	父菜单ID
	  */
	public void setParentid(Long parentid)
	{
		this.parentid = parentid;
	}

	/**
	  *	1 管理员  2 企业用户
	  */
	public Short getUsertype()
	{
		return usertype;
	}

	/**
	  *	1 管理员  2 企业用户
	  */
	public void setUsertype(Short usertype)
	{
		this.usertype = usertype;
	}

	/**
	  *	菜单排序序号
	  */
	public Integer getPositions()
	{
		return positions;
	}

	/**
	  *	菜单排序序号
	  */
	public void setPositions(Integer positions)
	{
		this.positions = positions;
	}

	/**
	  *	添加人
	  */
	public String getCreater()
	{
		return creater;
	}

	/**
	  *	添加人
	  */
	public void setCreater(String creater)
	{
		this.creater = creater;
	}

	/**
	  *	添加时间
	  */
	public String getCreatetime()
	{
		return createtime;
	}

	/**
	  *	添加时间
	  */
	public void setCreatetime(String createtime)
	{
		this.createtime = createtime;
	}

	/**
	  *	更新人
	  */
	public String getUpdater()
	{
		return updater;
	}

	/**
	  *	更新人
	  */
	public void setUpdater(String updater)
	{
		this.updater = updater;
	}

	/**
	  *	更新时间
	  */
	public String getUpdatetime()
	{
		return updatetime;
	}

	/**
	  *	更新时间
	  */
	public void setUpdatetime(String updatetime)
	{
		this.updatetime = updatetime;
	}
	
	public String toString()
	{
		return "TestBean [" +
					"menuId=" + menuId + 
					", menuname=" + menuname + 
					", menuurl=" + menuurl + 
					", icon=" + icon + 
					", parentid=" + parentid + 
					", usertype=" + usertype + 
					", positions=" + positions +
					", creater=" + creater + 
					", createtime=" + createtime + 
					", updater=" + updater + 
					", updatetime=" + updatetime + 
				"]";
	}
}

