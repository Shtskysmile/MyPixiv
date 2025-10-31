package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface FavoriteMapper {
    @Insert("INSERT INTO favorite (userId, contributionId) VALUES (#{userId}, #{contributionId})")
    void insertFavorite(String userId, String contributionId);

    @Delete("DELETE FROM favorite WHERE userId = #{userId} AND contributionId = #{contributionId}")
    void deleteFavorite(String userId, String contributionId);

    @Select("SELECT 1 FROM favorite WHERE userId = #{userId} AND contributionId = #{contributionId} LIMIT 1")
    boolean isFavorite(
            @Param("userId") String userId,
            @Param("contributionId") String contributionId
    );
}
