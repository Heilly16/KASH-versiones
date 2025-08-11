package com.proyecto.KASH;

import com.proyecto.KASH.Repository.ComentarioRepositorio;
import com.proyecto.KASH.Repository.foroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KashApplication  {

	public static void main(String[] args) {
		SpringApplication.run(KashApplication.class, args);
	}
        
        @Autowired
        private foroRepositorio repositorio;
        
         @Autowired
        private ComentarioRepositorio ComentarioRepositorio;
        


}
