package com.wztc.demo.controller;


import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.MessageBoard;
import com.wztc.demo.service.MessageBoardService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 留言板Controller层  -> 对应表: message_board
 */
@RestController
@RequestMapping("/messageBoard")
public class MessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(MessageBoard messageBoard,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<MessageBoard> lstMessageBoard = messageBoardService.selectByPage(messageBoard, pageNum, pageSize);
        PageInfo<MessageBoard> pageInfo = new PageInfo<>(lstMessageBoard);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<MessageBoard>(total, lstMessageBoard));
    }


    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody MessageBoard messageBoard){
        messageBoardService.add(messageBoard);
        return Response.success();
    }

    /**
     * 更新数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody MessageBoard messageBoard){
        messageBoardService.update(messageBoard);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        messageBoardService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除数据
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        messageBoardService.deleteBatch(ids);
        return Response.success();
    }
}