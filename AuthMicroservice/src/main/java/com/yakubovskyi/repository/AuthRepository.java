package com.yakubovskyi.repository;

import com.yakubovskyi.entity.Auth;

public interface AuthRepository extends MyGenericRepo<Auth,Long> {
    Boolean existsByUserNameAndPassword(String userName, String password);
}
