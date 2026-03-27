package com.atatame.medicineassistantsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.atatame.medicineassistantsystem.mapper")
public class MedicineAssistantSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineAssistantSystemApplication.class, args);
    }

}
