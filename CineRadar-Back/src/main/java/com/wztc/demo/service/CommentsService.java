package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.entity.Attach;
import com.wztc.demo.entity.Comments;
import com.wztc.demo.mapper.AttachMapper;
import com.wztc.demo.mapper.CommentsMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论 Service层
 */
@Service
public class CommentsService {

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private AttachMapper attachMapper;

    /**
     * 根据主键查询
     */
    public Comments selectById(Integer id){
        return commentsMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Comments> selectByPage(Comments comments, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comments> lstComments = commentsMapper.select(comments); // 执行分页查询

        //查询二级
        lstComments.forEach(item ->{
            Comments commentsChildren = new Comments();
            commentsChildren.setLevel(2);
            commentsChildren.setParentId(item.getId());
            item.setChildren(commentsMapper.select(commentsChildren));
        });

        //查询一级对应附件
        lstComments.forEach(item->{
            Attach attach = new Attach();
            attach.setTableName("comments");
            attach.setTableId(item.getId());
            item.setAttachList(attachMapper.select(attach));
        });

        return lstComments;
    }

    /**
     * 查询所有
     * @return
     */
    public List<Comments> select(Comments comments) {
        return commentsMapper.select(comments);
    }

    /**
     * 新增数据
     */
    public int add(Comments comments) {
        comments.setCreateTime(DateUtils.date2ShortString(new Date()));
        //一级留言
        if(ObjectUtil.isEmpty(comments.getParentId())){
            comments.setLevel(1);
        }else{
            //二级留言
            comments.setLevel(2);
        }

        int nCount = commentsMapper.insert(comments);
        //新增评论附件数据
        if(CollectionUtil.isNotEmpty(comments.getAttachList())){
            List<Attach> attachList = comments.getAttachList();
            attachList.forEach(attach->{
                attach.setTableName("comments");
                attach.setTableId(comments.getId());
                attach.setCreateTime(DateUtils.date2ShortString(new Date()));
                attachMapper.insert(attach);
            });
        }
        return nCount;
    }

    /**
     * 修改数据
     */
    public void update(Comments comments) {
        comments.setCreateTime(DateUtils.date2ShortString(new Date()));
        commentsMapper.update(comments);

        //删除关联数据
        Attach attachDelete = new Attach();
        attachDelete.setTableName("comments");
        attachDelete.setTableId(comments.getId());
        List<Attach> attachDeleteList = attachMapper.select(attachDelete);
        List<Integer> ids = attachDeleteList.stream().map(Attach::getId).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(ids)){
            attachMapper.deleteBatch(ids);
        }


        //关联数据全部重新添加
        List<Attach> attachList = comments.getAttachList();
        if(CollectionUtil.isNotEmpty(attachList)){
            attachList.forEach(attach->{
                attach.setId(null);
                attach.setTableName("comments");
                attach.setTableId(comments.getId());
                attach.setCreateTime(DateUtils.date2ShortString(new Date()));
                attachMapper.insert(attach);
            });
        }
    }


    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        Comments comments = commentsMapper.selectById(id);
        //删除一级评论
        if(comments.getLevel() ==1){
            Comments commentsSearch = new Comments();
            commentsSearch.setParentId(comments.getId());
            List<Comments> lstCommentsChildren = commentsMapper.select(commentsSearch);
            if(CollectionUtil.isNotEmpty(lstCommentsChildren)){
                /*throw new ServiceException("先删除二级评论,然后才能删除一级评论");*/
                //循环删除二级评论
                lstCommentsChildren.forEach(item->{
                    commentsMapper.deleteById(item.getId());
                });
            }
        }
        //删除评论
        commentsMapper.deleteById(id);

        //删除关联数据
        Attach attachDelete = new Attach();
        attachDelete.setTableName("comments");
        attachDelete.setTableId(id);
        List<Attach> attachDeleteList = attachMapper.select(attachDelete);
        List<Integer> ids = attachDeleteList.stream().map(Attach::getId).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(ids)){
            attachMapper.deleteBatch(ids);
        }
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        //批量删除
        ids.forEach(id->{
            deleteById(id);
        });
    }
}
