package com.yakubovskyi.repository;

import com.yakubovskyi.document.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile,String> {
}
