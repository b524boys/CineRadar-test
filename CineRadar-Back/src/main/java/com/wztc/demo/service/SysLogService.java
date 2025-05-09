package com.wztc.demo.service;

import com.wztc.demo.entity.SysLog;
import com.wztc.demo.mapper.SysLogMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统日志 Service层
 */
@Service
public class SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 根据主键查询
     */
    public SysLog selectById(Integer id){
        return sysLogMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<SysLog> selectByPage(SysLog sysLog, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return sysLogMapper.select(sysLog); // 执行分页查询
    }

    /**
     * 新增数据
     */
    public void add(SysLog sysLog) {
        sysLogMapper.insert(sysLog);
    }

    /**
     * 修改数据
     */
    public void update(SysLog sysLog) {
        sysLogMapper.update(sysLog);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        sysLogMapper.deleteById(id);
    }

    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        sysLogMapper.deleteBatch(ids);
    }

}