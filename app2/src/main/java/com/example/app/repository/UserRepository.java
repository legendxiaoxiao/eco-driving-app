package com.example.app.repository;

import com.example.app.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends CrudRepository<User,Long> {
    boolean existsByName(String name);
    User findByNameAndPassword(String name,String password);
    User findByName(String name);

    @Modifying
    @Transactional
    @Query("update User u set u.name=?2, u.password=?3, u.email=?4, u.phonenum=?5, u.mostFreCity=?6, u.carEge=?7," +
            "u.carBrand=?8,u.carType=?9,u.gasType=?10,u.purpose=?11 where u._id=?1 ")
    void update(Long id,String name,String password,String email,String phonenum,String mostFreCity,String carEge,
                String carBrand,String carType,String gasType,String purpose);
}