package com.wztc.demo.controller;


import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.User;
import com.wztc.demo.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 Controller层  -> 对应表: user
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    public Response selectByPage(User user,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<User> lstUser = userService.selectByPage(user, pageNum, pageSize);
        PageInfo<User> pageInfo = new PageInfo<>(lstUser);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<User>(total, lstUser));
    }

    /**
     * 查询所有
     */
    @GetMapping("/select")
    public Response select() {
        List<User> lstUser = userService.select(null);
        return Response.success(lstUser);
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody User user){
        userService.add(user);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody User user){
        userService.update(user);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        userService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        userService.deleteBatch(ids);
        return Response.success();
    }
}