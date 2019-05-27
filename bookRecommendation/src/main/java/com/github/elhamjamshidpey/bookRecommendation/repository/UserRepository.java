package com.github.elhamjamshidpey.bookRecommendation.repository;
/*
@uthor by Elham
May 27, 2019
*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.elhamjamshidpey.bookRecommendation.data.User;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

}
