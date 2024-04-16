package com.nowcoder.community.utils;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 在多线程环境下存储和管理用户信息
 */
@Component
public class HostHolder {
    /**
     * user变量可以在每个线程存储一个User对象
     * 使用 ThreadLocal 可以确保在多线程环境下每个线程都有自己独立的User对象
     */
    ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 将当前线程关联的User存储起来
     * @param user
     */
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * 获取当前线程关联的User对象
     * @return
     */
    public User getUser() {
        return users.get();
    }

    /**
     * 清除当前线程关联的User对象
     * 在处理完一个请求之后可以在users中清除相关用户信息，避免内存泄漏或信息泄露
     * 1. 内存泄露：如果处理完一个请求之后不进行删除，这个user对象可能会一直占用内存，导致内存占用过高的问题
     * 2. 信息泄露：如果处理完一个请求之后不进行清除，当前线程一直携带当前用户信息，
     *             则其他请求复用当前线程时，其他请求仍然能访问到该用户信息，造成信息泄露
     */
    public void remove() {
        users.remove();
    }
}
