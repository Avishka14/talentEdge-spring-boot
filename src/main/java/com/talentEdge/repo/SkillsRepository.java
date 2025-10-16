package com.talentEdge.repo;

import com.talentEdge.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface SkillsRepository extends JpaRepository<Skills , Integer> {

}
