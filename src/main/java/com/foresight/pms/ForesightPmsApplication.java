package com.foresight.pms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foresight.pms.dao.ProjectManagementRepository;
import com.foresight.pms.dto.ProjectHierarchyDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class ForesightPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForesightPmsApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ProjectManagementRepository projectManagementRepository) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            TypeReference<ProjectHierarchyDto> typeReference = new TypeReference<ProjectHierarchyDto>(){};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/proj.json");
            try {
                ProjectHierarchyDto entities = mapper.readValue(inputStream,typeReference);
                projectManagementRepository.saveAll(entities.getItems());
                System.out.println("Projects persisted");
            } catch (IOException e){
                System.out.println("Unable to persist projects: " + e.getMessage());
            }
        };
    }

}
