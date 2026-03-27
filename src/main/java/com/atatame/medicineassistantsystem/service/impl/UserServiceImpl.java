package com.atatame.medicineassistantsystem.service.impl;

import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.mapper.UserMapper;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户账户 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-03-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
