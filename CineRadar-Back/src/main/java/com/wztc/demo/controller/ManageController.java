package com.wztc.demo.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.common.*;
import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.Response;
import com.wztc.demo.common.ResponseCodeEnum;
import com.wztc.demo.common.SysOperation;
import com.wztc.demo.entity.Admin;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.service.AdminService;
import com.wztc.demo.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage")
public class ManageController {
    @Autowired
    private AdminService adminService;

    /**
     * 后台管理员登录
     * @param admin
     * @return
     */
    @SysOperation("管理员登录")
    @NoTokenAccess
    @PostMapping("/login")
    public Response login(@RequestBody Admin admin) {
        if (ObjectUtil.isEmpty(admin.getUserName()) || ObjectUtil.isEmpty(admin.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        List<Admin> lstAdmin = adminService.select(admin);
        Admin adminQuery = CollectionUtil.isEmpty(lstAdmin) ? null : lstAdmin.get(0);

        if (ObjectUtil.isEmpty(adminQuery)) {
            throw new ServiceException("用户信息不正确");
        }
        if (!admin.getPassword().equals(adminQuery.getPassword())) {
            throw new ServiceException("用户信息不正确");
        }

        //生成token
        String tokenData = adminQuery.getId() + "-" + adminQuery.getRole();
        String token = TokenUtils.createToken(tokenData, admin.getPassword());
        adminQuery.setToken(token);

        return Response.success(adminQuery);
    }

    /**
     * 修改密码
     * @param admin
     * @return
     */
    @SysOperation("管理员修改密码")
    @PostMapping("/password")
    public Response updatePassword(@RequestBody Admin admin){
        //判断参数是否缺失
        if (ObjectUtil.isEmpty(admin.getUserName()) || ObjectUtil.isEmpty(admin.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        //判断原密码是否正确
        Admin adminQuery = adminService.selectById(admin.getId());
        if(adminQuery.getPassword().compareTo(admin.getPassword()) != 0){
            return Response.error("原密码不正确!");
        }

        //更新密码
        admin.setPassword(admin.getNewPassword());
        adminService.update(admin);
        return Response.success(admin);
    }
}
