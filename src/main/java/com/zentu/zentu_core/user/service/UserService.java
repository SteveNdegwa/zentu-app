package com.zentu.zentu_core.user.service;

import com.zentu.zentu_core.group.dto.GroupMembershipDto;
import com.zentu.zentu_core.user.dto.CreateUserRequest;
import com.zentu.zentu_core.user.dto.UpdateUserRequest;
import com.zentu.zentu_core.user.dto.UserDto;
import com.zentu.zentu_core.user.dto.UserSummaryDto;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.service.GroupService;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.group.entity.GroupMembership;
import com.zentu.zentu_core.user.enums.UserRole;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.group.repository.GroupMembershipRepository;
import com.zentu.zentu_core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupMembershipRepository userGroupMembershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;

    @Transactional
    public UUID createUser(CreateUserRequest request, UserRole role, Boolean isSuperUser){
        if (userRepository.existsByPhoneNumberAndState(request.getPhoneNumber(), State.ACTIVE)){
            throw  new RuntimeException("Phone number already exists");
        }

        if (userRepository.existsByEmailAndState(request.getEmail(), State.ACTIVE)){
            throw  new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .otherName(request.getOtherName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isSuperUser(isSuperUser)
                .build();
        userRepository.save(user);

        return user.getId();
    }

    @Transactional
    public void updateUser(UpdateUserRequest request, UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setOtherName(request.getOtherName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(UUID userId, String roleName){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));

        UserRole role;
        try{
            role = UserRole.valueOf(roleName.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserIsSuperUser(UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        user.setIsSuperUser(!user.getIsSuperUser());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        user.setState(State.INACTIVE);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<GroupMembershipDto> getUserGroupMemberships(User user){
        List<GroupMembership> memberships = userGroupMembershipRepository.findAllByUserAndState(user, State.ACTIVE);

        return memberships.stream()
                .map(membership -> {
                    Group group = membership.getGroup();
                    Boolean isAdmin = groupService.isUserGroupAdmin(group, user);
                    return GroupMembershipDto.builder()
                            .groupId(group.getId())
                            .groupName(group.getName())
                            .groupDescription(group.getDescription())
                            .isAdmin(isAdmin)
                            .role(membership.getState().toString())
                            .joinedAt(membership.getCreatedAt())
                            .build();
                })
                .toList();
    }

    private UserDto convertToUserDto(User user){
        List<GroupMembershipDto> memberships = getUserGroupMemberships(user);
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .otherName(user.getOtherName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().toString())
                .isSuperUser(user.getIsSuperUser())
                .groupMemberships(memberships)
                .build();
    }

    private UserSummaryDto convertToUserSummaryDto(User user){
        return UserSummaryDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .otherName(user.getOtherName())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId){
        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not found"));
        return convertToUserDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAllByState(State.ACTIVE);
        return users.stream()
                .map(this::convertToUserDto).toList();
    }
}
