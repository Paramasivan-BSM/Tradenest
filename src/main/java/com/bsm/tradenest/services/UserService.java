package com.bsm.tradenest.services;

import com.bsm.tradenest.dao.Userdao;
import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.enums.Role;
import com.bsm.tradenest.model.Usermodel;
import org.springframework.stereotype.Service;

import javax.management.Query;


@Service
public class UserService {

   private final Userdao dao;
    public UserService(Userdao dao){
        this.dao = dao;
    }

    public  String switchUser(String email){
         Usermodel obj =  dao.findByEmail(email);


       dao.updateRole(email,Role.ROLE_WORKER);

         return "Worker Mode Applied";
    }


}
