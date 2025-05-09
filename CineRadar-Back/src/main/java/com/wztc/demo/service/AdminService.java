package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.common.RoleEnum;
import com.wztc.demo.entity.Admin;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.AdminMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 管理员 Service层
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 根据主键查询
     */
    public Admin selectById(Integer id){
        return adminMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Admin> selectByPage(Admin admin, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.select(admin); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<Admin> select(Admin admin) {
        return adminMapper.select(admin);
    }

    /**
     * 新增数据
     */
    public void add(Admin admin) {
        //用户名不能重复
        if(ObjectUtil.isNotEmpty(admin.getUserName())){
            Admin adminQuery = new Admin();
            adminQuery.setUserName(admin.getUserName());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(ObjectUtil.isNotEmpty(adminQuery)){
                throw new ServiceException("用户名已经存在");
            }
        }

        //手机号不能重复
        if(ObjectUtil.isNotEmpty(admin.getPhone())){
            Admin adminQuery = new Admin();
            adminQuery.setPhone(admin.getPhone());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(ObjectUtil.isNotEmpty(adminQuery)){
                throw new ServiceException("这个手机号已经被其他用户使用, 请换个手机号!!!");
            }
        }

        //邮箱不能重复
        if(ObjectUtil.isNotEmpty(admin.getEmail())){
            Admin adminQuery = new Admin();
            adminQuery.setEmail(admin.getEmail());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(ObjectUtil.isNotEmpty(adminQuery)){
                throw new ServiceException("这个邮箱已经被其他用户使用, 请换个邮箱!!!");
            }
        }

        admin.setRole(RoleEnum.SUB_ADMIN.code);
        admin.setCreateTime(DateUtils.date2ShortString(new Date()));
        adminMapper.insert(admin);
    }

    /**
     * 修改数据
     */
    public void update(Admin admin) {
        //用户名不能重复
        if(ObjectUtil.isNotEmpty(admin.getUserName())){
            Admin adminQuery = new Admin();
            adminQuery.setUserName(admin.getUserName());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(null != adminQuery && admin.getId().intValue() != adminQuery.getId().intValue()){
                throw new ServiceException("用户名已经存在");
            }
        }

        //手机号不能重复
        if(ObjectUtil.isNotEmpty(admin.getPhone())){
            Admin adminQuery = new Admin();
            adminQuery.setPhone(admin.getPhone());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(null != adminQuery && admin.getId().intValue() != adminQuery.getId().intValue()){
                throw new ServiceException("这个手机号已经被其他用户使用, 请换个手机号!!!");
            }
        }

        //邮箱不能重复
        if(ObjectUtil.isNotEmpty(admin.getEmail())){
            Admin adminQuery = new Admin();
            adminQuery.setEmail(admin.getEmail());
            List<Admin> lstAdmin = adminMapper.select(adminQuery);
            adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);
            if(null != adminQuery && admin.getId().intValue() != adminQuery.getId().intValue()){
                throw new ServiceException("这个邮箱已经被其他用户使用, 请换个邮箱!!!");
            }
        }

        adminMapper.update(admin);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        Admin admin = adminMapper.selectById(id);
        if(RoleEnum.ADMIN.code.equals(admin.getRole())){
            throw new ServiceException("超级管理员不能删除");
        }
        adminMapper.deleteById(id);
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        ids.forEach(item->{
            Admin admin = adminMapper.selectById(item);
            if(RoleEnum.ADMIN.code.equals(admin.getRole())){
                throw new ServiceException("批量删除时，超级管理员不能删除");
            }
        });
        adminMapper.deleteBatch(ids);
    }

}