package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Comments;
import com.wztc.demo.service.CommentsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论Controller层  -> 对应表: comments
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(Comments comments,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Comments> lstComments = commentsService.selectByPage(comments, pageNum, pageSize);
        PageInfo<Comments> pageInfo = new PageInfo<>(lstComments);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Comments>(total, lstComments));
    }


    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Comments comments){
        commentsService.add(comments);
        return Response.success();
    }

    /**
     * 更新数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody Comments comments){
        commentsService.update(comments);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        commentsService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除数据
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        commentsService.deleteBatch(ids);
        return Response.success();
    }
}