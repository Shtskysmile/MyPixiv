CREATE DATABASE MyPixiv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE MyPixiv;
-- 1. 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userId VARCHAR(36) NOT NULL UNIQUE  DEFAULT (UUID()),
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(512) DEFAULT '/files/default_user/avatar/default_avatar.png',
    sex TINYINT DEFAULT 0 COMMENT '0=未知,1=男,2=女',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常,1=封禁',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '0=普通用户,1=社区管理员,2=系统管理员',
    INDEX idx_username (username),
    INDEX idx_userId (userId),
    FULLTEXT idx_username_fulltext (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 作品表
CREATE TABLE contribution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    contributionId VARCHAR(36) NOT NULL UNIQUE  DEFAULT (UUID()),
    type TINYINT  COMMENT '0=插画,1=漫画',
    title VARCHAR(255) NOT NULL,
    image JSON NOT NULL,
    description TEXT,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常,1=已封禁',
    publishTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    authorId VARCHAR(36) NOT NULL,
    auditStatus TINYINT NOT NULL DEFAULT 0 COMMENT '0=待审核,1=通过,2=驳回',
    viewCount INT NOT NULL DEFAULT 0,
    favoriteCount INT NOT NULL DEFAULT 0,
    likeCount INT NOT NULL DEFAULT 0,
    commentCount INT NOT NULL DEFAULT 0,
    dismissalReason TEXT,
    FOREIGN KEY (authorId) REFERENCES user(userId) ON DELETE CASCADE,
    INDEX idx_author (authorId),
    INDEX idx_contributionId (contributionId),
    INDEX idx_type (type),
    INDEX idx_audit (auditStatus),
    INDEX idx_title (title),                 
    FULLTEXT idx_title_fulltext (title) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 评论表
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    commentId VARCHAR(36) NOT NULL UNIQUE DEFAULT (UUID()),
    description TEXT NOT NULL,
    time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author VARCHAR(36) NOT NULL,
    contribution VARCHAR(36) NOT NULL,
    FOREIGN KEY (author) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (contribution) REFERENCES contribution(contributionId) ON DELETE CASCADE,
    INDEX idx_commentId (commentId),
    INDEX idx_author (author),
    INDEX idx_contribution (contribution)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 关注表
CREATE TABLE follow (
    followerId VARCHAR(36) NOT NULL,
    followedId VARCHAR(36) NOT NULL,
    PRIMARY KEY (followerId, followedId),
    FOREIGN KEY (followerId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (followedId) REFERENCES user(userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 点赞表
CREATE TABLE likes (
    userId VARCHAR(36) NOT NULL,
    contributionId VARCHAR(36) NOT NULL,
    PRIMARY KEY (userId, contributionId),
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (contributionId) REFERENCES contribution(contributionId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 收藏表
CREATE TABLE favorite (
    userId VARCHAR(36) NOT NULL,
    contributionId VARCHAR(36) NOT NULL,
    PRIMARY KEY (userId, contributionId),
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (contributionId) REFERENCES contribution(contributionId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 标签表
CREATE TABLE tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tagName VARCHAR(64) NOT NULL UNIQUE,
    INDEX idx_tagName (tagName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 标签关系表
CREATE TABLE tag_relation (
    tagId BIGINT NOT NULL,
    contributionId VARCHAR(36) NOT NULL,
    PRIMARY KEY (tagId, contributionId),
    FOREIGN KEY (tagId) REFERENCES tag(id) ON DELETE CASCADE,
    FOREIGN KEY (contributionId) REFERENCES contribution(contributionId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 操作日志表
CREATE TABLE log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    operatorId VARCHAR(36) NOT NULL,
    time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(512) NOT NULL,
    FOREIGN KEY (operatorId) REFERENCES user(userId) ON DELETE CASCADE,
    INDEX idx_operator (operatorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 密保问题表
CREATE TABLE security_issue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userId VARCHAR(36) NOT NULL,
    description VARCHAR(255) NOT NULL,
    answer VARCHAR(255) NOT NULL,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE,
    INDEX idx_userId (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;