package com.wztc.demo.mapper;

import com.wztc.demo.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 电影 Mapper层
 */
@Mapper
public interface GoodsMapper {
    /**
     * 根据主键查询
     */
    Goods selectById(Integer id);

    /**
     * 根据电影名称查询
     */
    Goods selectByName(String name);

    /**
     * 条件查询
     */
    List<Goods> select(Goods good);

    /**
     * 根据ids批量查询
     */
    List<Goods> selectGoods(List<Integer> ids);

    /**
     *  热门电影 (点击数)
     */
    List<Goods> selectHotGoods();

    /**
     *  根据用户id查询收藏电影列表
     */
    String getCollectedMovies(int userId);

    /**
     *  根据用户id查询评价电影列表
     */
    String getRatedMovies(int userId);

    /**
     * 新增数据
     */
    int insert(Goods good);

    /**
     * 更新数据
     */
    int update(Goods good);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);


    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);

}
