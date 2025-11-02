package org.example.PCOI.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.PCOI.Entity.Contribution;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.util.List;

@Mapper
public interface ContributionMapper {

    // 先注册通用 ResultMap，避免解析顺序导致找不到
    @Results(id = "ContributionMap", value = {
            @Result(column = "image", property = "image", typeHandler = JacksonTypeHandler.class)
    })
    @Select("SELECT 1")
    Integer _registerContributionMap();

    @Insert("INSERT INTO contribution(type,title,image,description,authorId) VALUES(#{type},#{title},#{image, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler},#{description},#{authorId})")
    void insertContribution(Contribution contribution);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE contributionId = #{contributionId} AND status = 0 AND auditStatus = 1")
    Contribution selectContributionById(String contributionId);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE authorId = #{authorId}")
    List<Contribution> selectContributionsByAuthorId(String authorId);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE auditStatus = #{auditStatus}")
    List<Contribution> selectContributionsByAuditStatus(int auditStatus);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE authorId = #{authorId} AND auditStatus = #{auditStatus}")
    List<Contribution> selectContributionsByAuthorIdAndAuditStatus(@Param("authorId") String authorId, @Param("auditStatus") int auditStatus);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = #{status} ")
    List<Contribution> selectContributionsByStatus(int status);

    @Delete("DELETE FROM contribution WHERE contributionId = #{contributionId} AND status = 0 AND auditStatus = 1")
    void deleteContributionById(String contributionId);

    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE type = #{type} AND status = 0 AND auditStatus = 1")
    List<Contribution> selectContributionsByType(int type);

    // 按类型并按浏览量排序
    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = 0 AND auditStatus = 1 AND type = #{type} ORDER BY viewCount DESC LIMIT #{limit}")
    List<Contribution> selectContributionsByTypeAndViewCount(@Param("type") int type, @Param("limit") int limit);

    // 按类型并按收藏量排序
    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = 0 AND auditStatus = 1 AND type = #{type} ORDER BY favoriteCount DESC LIMIT #{limit}")
    List<Contribution> selectContributionsByTypeAndFavoriteCount(@Param("type") int type, @Param("limit") int limit);

    // 按类型并按点赞量排序
    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = 0 AND auditStatus = 1 AND type = #{type} ORDER BY likeCount DESC LIMIT #{limit}")
    List<Contribution> selectContributionsByTypeAndLikeCount(@Param("type") int type, @Param("limit") int limit);

    // 按类型并按评论量排序
    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = 0 AND auditStatus = 1 AND type = #{type} ORDER BY commentCount DESC LIMIT #{limit}")
    List<Contribution> selectContributionsByTypeAndCommentCount(@Param("type") int type, @Param("limit") int limit);
    
    @Update("UPDATE contribution SET type = #{type}, title = #{title}, image = #{image, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, description = #{description}, status = #{status}, auditStatus = #{auditStatus}, publishTime = #{publishTime}, authorId = #{authorId}, viewCount = #{viewCount}, favoriteCount = #{favoriteCount}, likeCount = #{likeCount}, commentCount = #{commentCount}, dismissalReason = #{dismissalReason} WHERE contributionId = #{contributionId}")
    void updateContribution(Contribution contribution);


    @ResultMap("ContributionMap")
    @Select("""
            SELECT * FROM contribution
            WHERE MATCH(title) AGAINST(#{titleKeyword} IN NATURAL LANGUAGE MODE)
            AND status = 0 AND auditStatus = 1
            ORDER BY MATCH(title) AGAINST(#{titleKeyword} IN NATURAL LANGUAGE MODE) DESC
            LIMIT #{limit}
            """)
    List<Contribution> selectContributionsByTitle(
            @Param("limit") int limit,
            @Param("titleKeyword") String titleKeyword
    );

    @ResultMap("ContributionMap")
    @Select("""
    SELECT c.*
    FROM contribution c
    JOIN tag_relation tr ON c.contributionId = tr.contributionId
    JOIN tag t ON tr.tagId = t.id
    WHERE t.tagName = #{tagName}
    AND c.auditStatus = 1 AND c.status = 0
    ORDER BY c.publishTime DESC
    """)
    List<Contribution> selectContributionsByTag(String tagName);


    @ResultMap("ContributionMap")
    @Select("""
    SELECT c.*
    FROM contribution c
    JOIN favorite fc ON c.contributionId = fc.contributionId
    WHERE fc.userId = #{userId}
    AND c.auditStatus = 1 AND c.status = 0
    ORDER BY  c.publishTime DESC
""")
    List<Contribution> selectFavoriteContributionsByUserId(String userId);

    @ResultMap("ContributionMap")
    @Select("""
    SELECT c.*
    FROM contribution c
    JOIN likes lc ON c.contributionId = lc.contributionId
    WHERE lc.userId = #{userId}
    AND c.auditStatus = 1 AND c.status = 0
    ORDER BY  c.publishTime DESC
    """)
    List<Contribution> selectLikeContributionsByUserId(String userId);

    // 获取所有审核通过且未封禁的作品
    @ResultMap("ContributionMap")
    @Select("SELECT * FROM contribution WHERE status = 0 AND auditStatus = 1 ORDER BY publishTime DESC")
    List<Contribution> selectAllContributions();
}
