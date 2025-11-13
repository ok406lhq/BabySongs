package com.cool.music.dao;

import com.cool.music.until.SqliteT;

import java.util.List;
import java.util.Map;

public class DeleteDao {

    /**
     *
     * @return
     */
    public static void  delData(){
        //只要任何一个方法，或者删除的操作，都让他来检查一下内容，是否满足条件，不满足则删除
        String sql="select d_music_listen.*,d_music.id,  d_user.account from d_music_listen " +
                "left join d_music on d_music.id=d_music_listen.music_id  " +
                "left join d_user  on d_user.account=d_music_listen.user_id " +
                "WHERE d_music.id IS NULL OR d_user.account IS NULL;" ;
        List<Map<String, String>> list = SqliteT.queryMapList(sql);
        for (Map<String, String> stringStringMap : list) {//执行删除语句
            String musicId = stringStringMap.get("music_id");
            String userId = stringStringMap.get("user_id");
            String sql1="delete from d_music_listen where music_id=? and user_id=?";
            SqliteT.update(sql1,musicId,userId);
        }
        sql="select d_music_like.* ,d_music. id,d_user.account  from d_music_like left \n" +
                "join d_music on d_music.id=d_music_like.music_id \n" +
                "join d_user on d_user.account=d_music_like.user_id  \n" +
                "where  d_music.id is null or d_user.account is null";
        //上面处理了 d_music_listen
        list = SqliteT.queryMapList(sql);
        for (Map<String, String> stringStringMap : list) {
            String musicId = stringStringMap.get("music_id");
            String userId = stringStringMap.get("user_id");
            sql="delete from d_music_like where music_id=? and user_id=?";
            SqliteT.update(sql,musicId,userId);
        }
        //处理播放列表

        sql="select d_music_play.*, d_music.id , d_user.account  from d_music_play " +
                "left join d_music on d_music.id=d_music_play.id \n" +
                "left join d_user on d_user.account=d_music_play.user_id \n" +
                "where d_music.id is null or d_user.account is null\n";
        list = SqliteT.queryMapList(sql);
        for (Map<String, String> stringStringMap : list) {
            String musicId = stringStringMap.get("id");
            String userId = stringStringMap.get("user_id");
            SqliteT.update("delete from d_music_play where id=? and user_id=?",musicId,userId);
        }

        sql="select d_sheet_music.*,d_music.id,d_sheet.id from d_sheet_music \n" +
                "left join d_sheet on d_sheet.id=d_sheet_music.id \n" +
                "left join d_music on d_music.id=d_sheet_music.mid \n" +
                "where d_music.id is null or d_sheet.id is null";
        list=SqliteT.queryMapList(sql);
        for (Map<String, String> stringStringMap : list) {
            String sheetId = stringStringMap.get("id");
            String mid = stringStringMap.get("mid");
            SqliteT.update("delete from d_sheet_music where id=? and mid=?",sheetId,mid);
        }


    }
}
