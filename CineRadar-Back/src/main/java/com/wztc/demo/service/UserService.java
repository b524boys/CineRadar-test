package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.entity.User;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.UserMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户 Service层
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据主键查询
     */
    public User selectById(Integer id){
        return userMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<User> selectByPage(User user, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return userMapper.select(user); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<User> select(User user) {
        return userMapper.select(user);
    }

    /**
     * 新增数据
     */
    public int add(User user) {
        //用户名不能重复
        if(ObjectUtil.isNotEmpty(user.getUserName())){
            User userQuery = new User();
            userQuery.setUserName(user.getUserName());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery)){
                throw new ServiceException("用户名已经存在");
            }
        }

        //手机号不能重复
        if(ObjectUtil.isNotEmpty(user.getPhone())){
            User userQuery = new User();
            userQuery.setPhone(user.getPhone());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery)){
                throw new ServiceException("这个手机号已经被其他用户使用, 请换个手机号!!!");
            }
        }

        //邮箱不能重复
        if(ObjectUtil.isNotEmpty(user.getEmail())){
            User userQuery = new User();
            userQuery.setEmail(user.getEmail());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery)){
                throw new ServiceException("这个邮箱已经被其他用户使用, 请换个邮箱!!!");
            }
        }
        user.setCreateTime(DateUtils.date2ShortString(new Date()));
        return userMapper.insert(user);
    }

    /**
     * 修改数据
     */
    public void update(User user) {

        //用户名不能重复
        if(ObjectUtil.isNotEmpty(user.getUserName())){
            User userQuery = new User();
            userQuery.setUserName(user.getUserName());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery) && user.getId().intValue() != userQuery.getId().intValue()){
                throw new ServiceException("用户名已经存在");
            }
        }

        //手机号不能重复
        if(ObjectUtil.isNotEmpty(user.getPhone())){
            User userQuery = new User();
            userQuery.setPhone(user.getPhone());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery) && user.getId().intValue() != userQuery.getId().intValue()){
                throw new ServiceException("这个手机号已经被其他用户使用, 请换个手机号!!!");
            }
        }

        //邮箱不能重复
        if(ObjectUtil.isNotEmpty(user.getEmail())){
            User userQuery = new User();
            userQuery.setEmail(user.getEmail());
            List<User> lstUser = userMapper.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(ObjectUtil.isNotEmpty(userQuery) && user.getId().intValue() != userQuery.getId().intValue()){
                throw new ServiceException("这个邮箱已经被其他用户使用, 请换个邮箱!!!");
            }
        }

        userMapper.update(user);
    }

    /**
     * 根据id删除用户
     */
    public void deleteById(Integer id) {
        try{
            userMapper.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new ServiceException("此用户有外键关联数据,暂不能删除");
        }

    }

    /**
     * 批量删除用户
     */
    public void deleteBatch(List<Integer> ids) {
        userMapper.deleteBatch(ids);
    }

}
