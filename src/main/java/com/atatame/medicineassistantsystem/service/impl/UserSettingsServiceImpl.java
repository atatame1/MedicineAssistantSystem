package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.mapper.UserSettingsMapper;
import com.atatame.medicineassistantsystem.model.entity.UserSettings;
import com.atatame.medicineassistantsystem.service.IUserSettingsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsServiceImpl extends ServiceImpl<UserSettingsMapper, UserSettings> implements IUserSettingsService {
}
