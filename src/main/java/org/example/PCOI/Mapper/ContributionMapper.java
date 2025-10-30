package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.PCOI.Entity.Contribution;
import org.example.PCOI.Entity.SecurityIssue;

@Mapper
public interface ContributionMapper {
    @Insert("INSERT INTO contribution contribution (contributionId, type, title, image, description, authorId)  VALUES (#{contributionId}, #{type}, #{title}, #{image}, #{description}, #{author.id})")
    void insertContribution(Contribution contribution);
}
