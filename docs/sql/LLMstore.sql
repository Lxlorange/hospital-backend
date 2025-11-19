-- LLM 常见问题存储表（MySQL）
-- 功能：存放常见问题的 system prompt 与用户问题 message
-- 说明：若你的库中使用下划线命名（如 llm_store），请在实体上加 @TableName("llm_store")

CREATE TABLE IF NOT EXISTS `llmstore` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `prompt` TEXT NULL COMMENT 'system 提示，可为空，点击常见问题时作为 system prompt',
  `message` TEXT NOT NULL COMMENT '用户问题内容，点击常见问题时作为 user message',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 示例数据
INSERT INTO `llmstore` (`prompt`, `message`) VALUES
('你是医院后台的智能助手，只回答与本系统功能和使用相关的问题；拒绝提供医疗建议或与系统无关的信息。回答准确、简洁，面向用户实际操作场景。', '如何预约挂号？'),
('你是医院后台的智能助手，只回答与本系统功能和使用相关的问题；拒绝提供医疗建议或与系统无关的信息。回答准确、简洁，面向用户实际操作场景。', '怎么查看医生的排班？'),
('你是医院后台的智能助手，只回答与本系统功能和使用相关的问题；拒绝提供医疗建议或与系统无关的信息。回答准确、简洁，面向用户实际操作场景。', '如何取消预约或改签？');