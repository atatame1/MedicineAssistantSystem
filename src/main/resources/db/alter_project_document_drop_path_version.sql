-- 已有库升级：删除 project_document 冗余列 file_path、version（与 schema.sql 对齐）
ALTER TABLE `project_document` DROP COLUMN `file_path`;
ALTER TABLE `project_document` DROP COLUMN `version`;
