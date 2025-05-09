package com.wztc.demo.service;

import com.wztc.demo.entity.Attach;
import com.wztc.demo.mapper.AttachMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 附件 Service层
 */
@Service
public class AttachService {

    @Autowired
    private AttachMapper attachMapper;


    /**
     * 根据主键查询
     */
    public Attach selectById(Integer id){
        return attachMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Attach> selectByPage(Attach attach, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return attachMapper.select(attach); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<Attach> select(Attach attach) {
        return attachMapper.select(attach);
    }

    /**
     * 新增数据
     */
    public void add(Attach attach) {
        attach.setCreateTime(DateUtils.date2ShortString(new Date()));
        attachMapper.insert(attach);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        attachMapper.deleteById(id);
    }

    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        attachMapper.deleteBatch(ids);
    }


}
