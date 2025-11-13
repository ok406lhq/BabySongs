package com.cool.music.until;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当前类是封装重复利用Sql查询功能
 */
public class SqliteT {


    public static SQLiteDatabase db=DBUntil.con;

    /**
     * 查询所有内容的工具
     * @param sql
     * @param clazz
     * @param data
     * @return
     * @param <T>
     */
    public static <T> List<T> query(String sql, Class<T> clazz, String ...data) {

        Cursor result = db.rawQuery(sql, data);
        List<T> list = new ArrayList<>();
        while(result.moveToNext()){


            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);//设置公开访问
                T t = constructor.newInstance();
                Field[] z=clazz.getDeclaredFields();//获取类里面属性
                for (Field field : z) {
                    field.setAccessible(true);//设置公开访问
                    String fieldName = field.getName();

                    int powIndex = result.getColumnIndex(fieldName);
                    String value = result.getString(powIndex);//这个就是查询到数据库值
                    field.set(t, value);


                }
                list.add(t);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }



        }
        return list;


    }


    /**
     * 查询单个
     * @param sql
     * @param clazz
     * @param data
     * @return
     * @param <T>
     */
    public static <T> T queryOne(String sql, Class<T> clazz, String ...data) {


        Cursor result = db.rawQuery(sql, data);

        if(result.moveToNext()){

            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);//设置公开访问
                T t = constructor.newInstance();
                Field[] z=clazz.getDeclaredFields();//获取类里面属性
                for (Field field : z) {
                    field.setAccessible(true);//设置公开访问
                    String fieldName = field.getName();

                    int powIndex = result.getColumnIndex(fieldName);
                    String value = result.getString(powIndex);//这个就是查询到数据库值
                    field.set(t, value);
                }
                return t;

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }



        }
        return null;
    }


    /**
     * 更改方法
     * @param sql
     * @param data
     * @return
     */
    public static int  update(String sql,String ...data) {
        try {
            db.execSQL(sql, data);
            return 1;
        }catch (Exception e){
            return 0;
        }

    }








    /**
     * 查询所有内容的工具
     * @param sql
     * @param clazz
     * @param data
     * @return
     * @param <T>
     */
    public static <T> List<Map<String,String>> queryMapList(String sql, Class<T> clazz, String ...data) {

        Cursor result = db.rawQuery(sql, data);
        List<Map<String,String>> list=new ArrayList<>();
        while(result.moveToNext()){


            Map<String,String> t=new HashMap<>();
            Field[] z=clazz.getDeclaredFields();//获取类里面属性
            for (Field field : z) {
                field.setAccessible(true);//设置公开访问
                String fieldName = field.getName();


                int powIndex = result.getColumnIndex(fieldName);
                String value = result.getString(powIndex);//这个就是查询到数据库值


                t.put(fieldName,value);
            }
            list.add(t);

        }
        return list;


    }


    /**
     * 查询单个
     * @param sql
     * @param clazz
     * @param data
     * @return
     * @param <T>
     */
    /**
     * 查询所有内容的工具
     * @param sql
     * @param clazz
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Map<String,String> queryMapOne(String sql, Class<T> clazz, String ...data) {

        Cursor result = db.rawQuery(sql, data);
        while(result.moveToNext()){
            Map<String,String> t=new HashMap<>();
            Field[] z=clazz.getDeclaredFields();//获取类里面属性
            for (Field field : z) {
                field.setAccessible(true);//设置公开访问
                String fieldName = field.getName();


                int powIndex = result.getColumnIndex(fieldName);
                String value = result.getString(powIndex);//这个就是查询到数据库值


                t.put(fieldName,value);
            }
            return  t;

        }
        return null;
    }







    /**
     * 查询所有内容返回map list
     * @param sql
     * @param data
     * @return
     * @param <T>
     */
    public static <T> List<Map<String,String>> queryMapList(String sql, String ...data) {

        Cursor result = db.rawQuery(sql, data);
        List<Map<String,String>> list=new ArrayList<>();
        String[] columnNames = result.getColumnNames();

        while(result.moveToNext()){
            Map<String,String> t=new HashMap<>();
            for (String columnName : columnNames) {
                int powIndex = result.getColumnIndex(columnName);
                String value = result.getString(powIndex);//这个就是查询到数据库值
                t.put(columnName,value);
            }
            list.add(t);

        }
        return list;


    }

}


