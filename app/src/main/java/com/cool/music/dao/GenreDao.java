package com.cool.music.dao;

import com.cool.music.bean.GenreBean;
import com.cool.music.until.SqliteT;

import java.util.List;

/**
 * 曲风分类数据访问类
 */
public class GenreDao {

    /**
     * 添加曲风
     * @param id
     * @param name
     * @return
     */
    public static int addGenre(String id, String name) {
        String sql = "insert into d_genre (id, name) values(?, ?)";
        return SqliteT.update(sql, id, name);
    }

    /**
     * 更新曲风
     * @param id
     * @param name
     * @return
     */
    public static int updateGenre(String id, String name) {
        String sql = "update d_genre set name = ? where id = ?";
        return SqliteT.update(sql, name, id);
    }

    /**
     * 删除曲风
     * @param id
     * @return
     */
    public static int deleteGenre(String id) {
        String sql = "delete from d_genre where id = ?";
        return SqliteT.update(sql, id);
    }

    /**
     * 根据ID查询曲风
     * @param id
     * @return
     */
    public static GenreBean getGenreById(String id) {
        String sql = "select * from d_genre where id = ?";
        return SqliteT.queryOne(sql, GenreBean.class, id);
    }

    /**
     * 查询所有曲风
     * @return
     */
    public static List<GenreBean> getAllGenres() {
        String sql = "select * from d_genre order by create_time asc";
        return SqliteT.query(sql, GenreBean.class);
    }

    /**
     * 根据名称搜索曲风
     * @param name
     * @return
     */
    public static List<GenreBean> searchGenres(String name) {
        if (name == null || name.equals("")) {
            return getAllGenres();
        }
        name = "%" + name + "%";
        String sql = "select * from d_genre where name like ?";
        return SqliteT.query(sql, GenreBean.class, name);
    }
}
