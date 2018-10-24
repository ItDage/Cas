package com.itdage.Dao;

import com.itdage.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by huayu on 2018/10/24.
 */
@Component
@Repository
public interface  UserDao {
    User getByName(String loginName);
}
