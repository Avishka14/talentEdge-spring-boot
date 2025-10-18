package com.talentEdge.repo;

import com.talentEdge.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilephotoRepository extends JpaRepository<ProfilePhoto , Integer>{
    Optional<ProfilePhoto> findFirstByUserProfile_Id(Integer userId);
}
