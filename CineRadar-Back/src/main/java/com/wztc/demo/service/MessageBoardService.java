package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.entity.Attach;
import com.wztc.demo.entity.MessageBoard;
import com.wztc.demo.mapper.AttachMapper;
import com.wztc.demo.mapper.MessageBoardMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言板 Service层
 */
@Service
public class MessageBoardService {

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private AttachMapper attachMapper;


    /**
     * 根据主键查询
     */
    public MessageBoard selectById(Integer id){
        return messageBoardMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<MessageBoard> selectByPage(MessageBoard messageBoard, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MessageBoard> lstMessageBoard = messageBoardMapper.select(messageBoard); // 执行分页查询

        //查询二级
        lstMessageBoard.forEach(item ->{
            MessageBoard messageBoardChildren = new MessageBoard();
            messageBoardChildren.setLevel(2);
            messageBoardChildren.setParentId(item.getId());
            item.setChildren(messageBoardMapper.select(messageBoardChildren));
        });

        //查询一级对应附件
        lstMessageBoard.forEach(item->{
            Attach attach = new Attach();
            attach.setTableName("message_board");
            attach.setTableId(item.getId());
            item.setAttachList(attachMapper.select(attach));
        });
        return lstMessageBoard;
    }

    /**
     * 查询所有
     * @return
     */
    public List<MessageBoard> select(MessageBoard messageBoard) {
        return messageBoardMapper.select(messageBoard);
    }

    /**
     * 新增数据
     */
    public int add(MessageBoard messageBoard) {
        messageBoard.setCreateTime(DateUtils.date2ShortString(new Date()));
        //一级留言
        if(ObjectUtil.isEmpty(messageBoard.getParentId())){
            messageBoard.setLevel(1);
        }else{
            //二级留言
            messageBoard.setLevel(2);
        }
        int nCount = messageBoardMapper.insert(messageBoard);

        //新增留言板附件数据
        if(CollectionUtil.isNotEmpty(messageBoard.getAttachList())){
            List<Attach> attachList = messageBoard.getAttachList();
            attachList.forEach(attach->{
                attach.setTableName("message_board");
                attach.setTableId(messageBoard.getId());
                attach.setCreateTime(DateUtils.date2ShortString(new Date()));
                attachMapper.insert(attach);
            });
        }

        return nCount;
    }

    /**
     * 修改数据
     */
    public void update(MessageBoard messageBoard) {
        messageBoard.setCreateTime(DateUtils.date2ShortString(new Date()));
        messageBoardMapper.update(messageBoard);

        //删除留言相关的附件数据
        Attach attachDelete = new Attach();
        attachDelete.setTableName("message_board");
        attachDelete.setTableId(messageBoard.getId());
        List<Attach> attachDeleteList = attachMapper.select(attachDelete);
        List<Integer> ids = attachDeleteList.stream().map(Attach::getId).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(ids)){
            attachMapper.deleteBatch(ids);
        }


        //添加留言附件数据全部重新
        List<Attach> attachList = messageBoard.getAttachList();
        if(CollectionUtil.isNotEmpty(attachList)){
            attachList.forEach(attach->{
                attach.setId(null);
                attach.setTableName("message_board");
                attach.setTableId(messageBoard.getId());
                attach.setCreateTime(DateUtils.date2ShortString(new Date()));
                attachMapper.insert(attach);
            });
        }
    }


    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        MessageBoard messageBoard = messageBoardMapper.selectById(id);
        //删除一级留言
        if(messageBoard.getLevel() ==1){
            MessageBoard messageBoardSearch = new MessageBoard();
            messageBoardSearch.setParentId(messageBoard.getId());
            List<MessageBoard> lstMessageBoardChildren = messageBoardMapper.select(messageBoardSearch);
            if(CollectionUtil.isNotEmpty(lstMessageBoardChildren)){
                /*throw new ServiceException("先删除二级留言,然后才能删除一级留言");*/
                lstMessageBoardChildren.forEach(item->{
                    messageBoardMapper.deleteById(item.getId());
                });
            }
        }

        //删除留言
        messageBoardMapper.deleteById(id);

        //删除关联数据
        Attach attachDelete = new Attach();
        attachDelete.setTableName("message_board");
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
