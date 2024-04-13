package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.RoleEnum;
import com.aua.museum.booking.exception.notfound.RoleNotFoundException;
import com.aua.museum.booking.repository.RoleRepository;
import com.aua.museum.booking.service.RoleService;
import com.aua.museum.booking.exception.notfound.RoleNotFoundException;
import com.aua.museum.booking.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;

    @Override
    public Role getRole(RoleEnum roleName) {
        return repository.getByRoleName(roleName).orElseThrow(() ->
                new RoleNotFoundException(roleName));
    }
}
