package com.wztc.demo.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.common.*;
import com.wztc.demo.entity.*;
import com.wztc.demo.common.*;
import com.wztc.demo.entity.*;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.service.*;
import com.wztc.demo.service.*;
import com.wztc.demo.util.DateUtils;
import com.wztc.demo.util.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private CollectService collectService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private MessageBoardService messageBoardService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsTypeService newsTypeService;

    @Autowired
    private RecommendByWeightService recommendByWeightService;

    @Autowired
    private RecommendByCollaborativeCosineService recommendByCollaborativeCosineService;

    /**
     * 电影详情
     * @param id
     * @return
     */
    @GetMapping("/goods/detail/{id}")
    @NoTokenAccess
    public Response selectGoodsById(@PathVariable Integer id){
        Goods goods = goodsService.selectById(id);
        int hits = Optional.ofNullable(goods.getHits()).orElse(0);
        goods.setHits(hits + 1);
        goodsService.update(goods);
        return Response.success(goods);
    }


    /**
     *  热门电影 (点击数)
     */
    @NoTokenAccess
    @GetMapping("/goods/selectHotGoods")
    public Response selectHotGoods(){
        List<Goods> lstGoods = goodsService.selectHotGoods();
        return Response.success(lstGoods);
    }


    /**
     * 注册
     */
    @PostMapping("/user/register")
    @NoTokenAccess
    @SysOperation("用户注册")
    public Response register(@RequestBody User user){
        if (ObjectUtil.isEmpty(user.getUserName()) || ObjectUtil.isEmpty(user.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        //判断是否有相同的用户名账户
        User userQuery = new User();
        userQuery.setUserName(user.getUserName());
        List<User> lstUser = userService.select(userQuery);
        userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
        if (ObjectUtil.isNotEmpty(userQuery)) {
            throw new ServiceException("用户名已经存在");
        }
        //新增用户
        user.setCreateTime(DateUtils.date2ShortString(new Date()));
        int nCount = userService.add(user);
        if(1 == nCount){
            return Response.success("注册成功");
        }else{
            return Response.error("注册失败");
        }
    }


    /**
     * 登陆
     */
    @PostMapping("/user/login")
    @NoTokenAccess
    @SysOperation("用户登录")
    public Response login(@RequestBody  User user){
        if (ObjectUtil.isEmpty(user.getUserName()) || ObjectUtil.isEmpty(user.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        List<User> lstUser = userService.select(user);
        User loginUser = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new ServiceException("用户信息不正确,请确认用户名和密码");
        }

        if (!user.getPassword().equals(loginUser.getPassword())) {
            throw new ServiceException("用户信息不正确,请确认用户名和密码");
        }

        //生成token
        String tokenData = loginUser.getId() + "-" + RoleEnum.USER.code;
        String token = TokenUtils.createToken(tokenData, loginUser.getPassword());
        loginUser.setToken(token);
        loginUser.setRole(RoleEnum.USER.code);

        return Response.success(loginUser);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/user/updateInfo")
    @SysOperation("用户更新信息")
    public Response updateInfo(@RequestBody  User user){
        //防止用户篡改数据
        if(user.getId().intValue() != getLoginUser().getId().intValue()){
            return Response.error(ResponseCodeEnum.USER_ATTACKED);
        }

        if (ObjectUtil.isEmpty(user.getUserName()) || ObjectUtil.isEmpty(user.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        //手机号不能重复
        if(ObjectUtil.isNotEmpty(user.getPhone())){
            User userQuery = new User();
            userQuery.setPhone(user.getPhone());
            List<User> lstUser = userService.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(null != userQuery && user.getId().intValue() != userQuery.getId().intValue()){
                throw new ServiceException("这个电话已经被其他用户使用, 请换个电话!!!");
            }
        }

        //邮箱不能重复
        if(ObjectUtil.isNotEmpty(user.getEmail())){
            User userQuery = new User();
            userQuery.setEmail(user.getEmail());
            List<User> lstUser = userService.select(userQuery);
            userQuery = CollectionUtil.isEmpty(lstUser) ? null : lstUser.get(0);
            if(null != userQuery && user.getId().intValue() != userQuery.getId().intValue()){
                throw new ServiceException("这个邮箱已经被其他用户使用, 请换个邮箱!!!");
            }
        }

        user.setPassword(null);
        userService.update(user);
        return Response.success(user);
    }

    /**
     * 修改密码
     */
    @PostMapping("/user/updatePassword")
    @SysOperation("用户修改密码")
    public Response updatePassword(@RequestBody User user){

        //防止用户篡改数据
        if(user.getId().intValue() != getLoginUser().getId().intValue()){
            return Response.error(ResponseCodeEnum.USER_ATTACKED);
        }

        //判断参数是否缺失
        if (ObjectUtil.isEmpty(user.getNewPassword()) || ObjectUtil.isEmpty(user.getPassword())) {
            return Response.error(ResponseCodeEnum.PARAM_MISSED_ERROR);
        }

        //判断原密码是否正确
        User userQuery = userService.selectById(user.getId());
        if(userQuery.getPassword().compareTo(user.getPassword()) != 0){
            return Response.error("原密码不正确!");
        }

        //更新密码
        user.setPassword(user.getNewPassword());
        userService.update(user);
        return Response.success(user);
    }


    /**
     * 查询是否收藏
     */
    @PostMapping("/collect/isCollect")
    public Response isCollect(@RequestBody Collect collect){
        collect.setUserId(getLoginUser().getId());
        List<Collect> lstCollect = collectService.select(collect);
        if(CollectionUtil.isEmpty(lstCollect)){
            return Response.error("未收藏");
        }else{
            return Response.success("已收藏");
        }
    }


    /**
     * 添加收藏
     */
    @SysOperation("添加收藏")
    @PostMapping("/collect/addCollect")
    public Response addCollect(@RequestBody Collect collect){
        collect.setUserId(getLoginUser().getId());
        List<Collect> lstCollect = collectService.select(collect);
        if(CollectionUtil.isNotEmpty(lstCollect)){
            return Response.error("用户之前收藏过");
        }else{
            collect.setCreateTime(DateUtils.date2ShortString(new Date()));
            int nCount = collectService.add(collect);
            if(1 == nCount){
                return Response.success("收藏成功");
            }else{
                return Response.error("收藏失败");
            }
        }
    }

    /**
     * 取消收藏
     */
    @SysOperation("取消收藏")
    @PostMapping("/collect/cancelCollect")
    public Response cancelCollect(@RequestBody Collect collect){
        collect.setUserId(getLoginUser().getId());
        List<Collect> lstCollect = collectService.select(collect);
        if(CollectionUtil.isEmpty(lstCollect)){
            return Response.error("用户之前未收藏过");
        }else{
            collect = lstCollect.get(0);
            int nCount = collectService.deleteById(collect.getId());
            if(1 == nCount){
                return Response.success("取消收藏成功");
            }else{
                return Response.success("取消收藏失败");
            }
        }
    }

    /**
     * 收藏分页查询
     */
    @GetMapping("/collect/selectCollectByPage")
    public Response selectCollectByPage(Collect collect,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        collect.setUserId(getLoginUser().getId());
        List<Collect> lstCollect = collectService.selectByPage(collect, pageNum, pageSize);
        PageInfo<Collect> pageInfo = new PageInfo<>(lstCollect);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Collect>(total, lstCollect));
    }


    /**
     * 添加足迹
     */
    @PostMapping("/history/addHistory")
    public Response addHistory(@RequestBody History history){
        history.setUserId(getLoginUser().getId());
        history.setCreateTime(DateUtils.date2ShortString(new Date()));
        int nCount = historyService.add(history);
        if(1 == nCount){
            return Response.success("添加足迹成功");
        }else{
            return Response.error("添加足迹失败");
        }
    }

    /**
     * 足迹分页查询
     */
    @GetMapping("/history/selectHistoryByPage")
    public Response selectHistoryByPage(History history,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        history.setUserId(getLoginUser().getId());
        List<History> lstHistory = historyService.selectByPage(history, pageNum, pageSize);
        PageInfo<History> pageInfo = new PageInfo<>(lstHistory);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<History>(total, lstHistory));
    }


    /**
     * 添加评论
     */
    @SysOperation("添加评论")
    @PostMapping("/comments/addComments")
    public Response addComments(@RequestBody Comments comments){
        comments.setUserId(getLoginUser().getId());
        int nCount = commentsService.add(comments);
        if(1 == nCount){
            return Response.success("添加评论成功");
        }else{
            return Response.error("添加评论失败");
        }
    }

    /**
     * 评论分页查询
     */
    @GetMapping("/comments/selectCommentsByPage")
    public Response selectCommentsByPage(Comments comments,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        comments.setUserId(getLoginUser().getId());
        List<Comments> lstComments = commentsService.selectByPage(comments, pageNum, pageSize);
        PageInfo<Comments> pageInfo = new PageInfo<>(lstComments);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Comments>(total, lstComments));
    }

    /**
     * 添加留言板
     */
    @PostMapping("/messageBoard/addMessageBoard")
    @SysOperation("添加留言")
    public Response addMessageBoard(@RequestBody MessageBoard messageBoard){
        messageBoard.setUserId(getLoginUser().getId());
        int nCount = messageBoardService.add(messageBoard);
        if(1 == nCount){
            return Response.success("添加评论成功");
        }else{
            return Response.error("添加评论失败");
        }
    }


    /**
     * 留言板分页查询
     */
    @GetMapping("/messageBoard/selectMessageBoardByPage")
    public Response selectMessageBoardByPage(MessageBoard messageBoard,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        messageBoard.setUserId(getLoginUser().getId());
        List<MessageBoard> lstMessageBoard = messageBoardService.selectByPage(messageBoard, pageNum, pageSize);
        PageInfo<MessageBoard> pageInfo = new PageInfo<>(lstMessageBoard);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<MessageBoard>(total, lstMessageBoard));
    }


    /**
     *  查询带图片的公告
     */
    @NoTokenAccess
    @GetMapping("/news/selectNewsByPage")
    public Response selectNewsByPage(News news,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize){
        List<News> lstNews = newsService.selectByPage(news, pageNum, pageSize);
        PageInfo<News> pageInfo = new PageInfo<>(lstNews);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<News>(total, lstNews));
    }

    /**
     *  根据类型查询对应的公告新闻
     */
    @NoTokenAccess
    @GetMapping("/news/typeNewsList")
    public Response typeNewsList(){
        List<NewsType> lstNewsType = newsTypeService.select(null);
        lstNewsType.forEach(item->{
            News news = new News();
            news.setNewsTypeId(item.getId());
            List<News> lstNews = newsService.selectByPage(news, 1, 7);
            //List<News> lstNews = newsService.selectByPageByView(news, 1, 7);
            item.setLstNews(lstNews);
        });
        return Response.success(lstNewsType);
    }

    /**
     *  查询对应的公告新闻详情
     */
    @NoTokenAccess
    @GetMapping("/news/detail/{id}")
    public Response newsDetail(@PathVariable Integer id){
        News news = newsService.selectById(id);
        int hits = Optional.ofNullable(news.getHits()).orElse(0);
        news.setHits(hits + 1);
        newsService.update(news);
        //News news = newsService.selectByIdProcedure(id);
        return Response.success(news);
    }

    /**
     * 查询推荐电影
     */
    @NoTokenAccess
    @GetMapping("/goods/queryRecommendGoods/{userId}")
    public Response loadRecommendGoods(@PathVariable String userId){
        boolean number = NumberUtil.isNumber(userId);
        Integer targetUserId = null;
        if (!number) {
            targetUserId = null;
        }else {
            targetUserId = Integer.parseInt(userId);
        }

        //收藏, 浏览, 评论 权重推荐
        //List<Goods> lstGoods = recommendByWeightService.queryRecommendGoods();
        System.out.println("打印---------------------"+targetUserId);

        List<Goods> lstGoods = recommendByCollaborativeCosineService.queryRecommendGoods(targetUserId);

        return Response.success(lstGoods);
    }
}
