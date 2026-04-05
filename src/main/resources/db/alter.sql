-- 将 project_decision.version 由 INT 改为 VARCHAR（执行前请先备份）
UPDATE `project_decision` SET `version` = 1 WHERE `version` IS NULL;
ALTER TABLE `project_decision`
  MODIFY COLUMN `version` VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '版本号';
