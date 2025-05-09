package com.wztc.demo.controller;


import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Attach;
import com.wztc.demo.service.AttachService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 附件Controller层  -> 对应表: attach
 */
@RestController
@RequestMapping("/attach")
public class AttachController {

    @Autowired
    private AttachService attachService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(Attach attach,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Attach> lstAttach = attachService.selectByPage(attach, pageNum, pageSize);
        PageInfo<Attach> pageInfo = new PageInfo<>(lstAttach);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Attach>(total, lstAttach));
    }


    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Attach attach){
        attachService.add(attach);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        attachService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除数据
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        attachService.deleteBatch(ids);
        return Response.success();
    }
}