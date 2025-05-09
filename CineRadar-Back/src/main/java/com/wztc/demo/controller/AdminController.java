package com.wztc.demo.controller;

import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Admin;
import com.wztc.demo.service.AdminService;
import com.wztc.demo.service.SysLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员Controller层  -> 对应表: admin
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    public Response selectByPage(Admin admin,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Admin> lstAdmin = adminService.selectByPage(admin, pageNum, pageSize);
        PageInfo<Admin> pageInfo = new PageInfo<>(lstAdmin);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Admin>(total, lstAdmin));
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Admin admin){
        adminService.add(admin);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody Admin admin){
        adminService.update(admin);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        adminService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        adminService.deleteBatch(ids);
        return Response.success();
    }
}
