package com.project.demo.logic.entity.user;

import com.project.demo.logic.entity.rol.Role;

import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createUser();
    }

    private void createUser() {
        User newuser = new User();
        newuser.setName("User");
        newuser.setLastname("User");
        newuser.setEmail("user.user@gmail.com");
        newuser.setPassword("superadmin123");

        User superAdmin = new User();
        superAdmin.setName("Super");
        superAdmin.setLastname("Admin");
        superAdmin.setEmail("super.admin@gmail.com");
        superAdmin.setPassword("superadmin123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<Role> optionalRoleAdmin = roleRepository.findByName(RoleEnum.SUPER_ADMIN_ROLE);
        Optional<User> optionalUser = userRepository.findByEmail(newuser.getEmail());
        Optional<User> optionalUserAdmin = userRepository.findByEmail(superAdmin.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()||optionalUserAdmin.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(newuser.getName());
        user.setLastname(newuser.getLastname());
        user.setEmail(newuser.getEmail());
        user.setPassword(passwordEncoder.encode(newuser.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
        System.out.println(user.getName()+"Se guardó correctamente");

        user = new User();
        user.setName(superAdmin.getName());
        user.setLastname(superAdmin.getLastname());
        user.setEmail(superAdmin.getEmail());
        user.setPassword(passwordEncoder.encode(superAdmin.getPassword()));
        user.setRole(optionalRoleAdmin.get());
        userRepository.save(user);
        System.out.println(user.getName()+"Se guardó correctamente");
    }

}
