package com.github.elhamjamshidpey.bookRecommendation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
@uthor by Elham
May 27, 2019
*/
@SpringBootApplication
public class BookRecommendationApplication {

    private static final Logger log = LoggerFactory.getLogger(BookRecommendationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BookRecommendationApplication.class, args);
	}
	
}
