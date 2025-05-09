package com.wztc.demo.controller;

import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.SysLog;
import com.wztc.demo.service.SysLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志Controller层  -> 对应表: sys_log
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    public Response selectByPage(SysLog sysLog,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<SysLog> lstSysLog = sysLogService.selectByPage(sysLog, pageNum, pageSize);
        PageInfo<SysLog> pageInfo = new PageInfo<>(lstSysLog);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<SysLog>(total, lstSysLog));
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody SysLog sysLog){
        sysLogService.add(sysLog);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody SysLog sysLog){
        sysLogService.update(sysLog);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        sysLogService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        sysLogService.deleteBatch(ids);
        return Response.success();
    }
}
