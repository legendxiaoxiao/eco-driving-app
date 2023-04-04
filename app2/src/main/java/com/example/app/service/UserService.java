package com.example.app.service;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserRepository repository;

    public Iterable<User> getAll(){
        return repository.findAll();
    }

    public Optional<User> getUser(Long id){
        return repository.findById(id);
    }

    public boolean existsByName(User user){ //通过名字判断
        return repository.existsByName(user.getName());
    }
    public User findByNameAndPassword(User user){
        return repository.findByNameAndPassword(user.getName(),user.getPassword());
    }
    public boolean insert(User user){
        repository.save(user);
        return true;
    }
    public boolean update(User user){
        if (!repository.existsByName(user.getName())){
            return false;
        }
        User oldUser=repository.findByName(user.getName());
        repository.update(oldUser.get_id(), oldUser.getName(), oldUser.getPassword(), oldUser.getEmail(), oldUser.getPhonenum(), user.getMostFreCity(),
                user.getCarEge(), user.getCarBrand(), user.getCarType(), user.getGasType(), user.getPurpose());
        return true;
    }
    public boolean delete(User user){
        if (!repository.existsByName(user.getName())){
            return false;
        }
        repository.deleteById(repository.findByName(user.getName()).get_id());
        return true;
    }
}
