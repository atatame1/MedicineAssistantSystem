package com.atatame.medicineassistantsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.atatame.medicineassistantsystem.mapper")
@EnableScheduling
public class MedicineAssistantSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineAssistantSystemApplication.class, args);
    }

}
