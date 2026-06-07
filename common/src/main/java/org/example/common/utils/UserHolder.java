package org.example.common.utils;


import org.example.pojo.dto.UserDTO;

/**
 * UserHolder类用于在当前线程中存储UserDTO对象
 * 使用ThreadLocal实现线程间的数据隔离
 */
public class UserHolder {
    // 使用ThreadLocal存储UserDTO对象，确保每个线程都有自己独立的数据副本
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    /**
     * 保存UserDTO对象到当前线程的ThreadLocal中
     * @param user 需要保存的UserDTO对象
     */
    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    /**
     * 从当前线程的ThreadLocal中获取UserDTO对象
     * @return 当前线程中存储的UserDTO对象，如果没有则返回null
     */
    public static UserDTO getUser(){
        return tl.get();
    }

    /**
     * 移除当前线程中存储的UserDTO对象
     * 通常在请求处理完成后调用，防止内存泄漏
     */
    public static void removeUser(){
        tl.remove();
    }
}
