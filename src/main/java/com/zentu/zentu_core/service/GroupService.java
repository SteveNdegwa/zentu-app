package com.zentu.zentu_core.service;

import com.zentu.zentu_core.dto.group.CreateGroupRequest;
import com.zentu.zentu_core.dto.group.GroupDto;
import com.zentu.zentu_core.dto.group.UpdateGroupRequest;
import com.zentu.zentu_core.dto.membership.GroupMemberDto;
import com.zentu.zentu_core.dto.user.UserSummaryDto;
import com.zentu.zentu_core.entity.*;
import com.zentu.zentu_core.enums.GroupRole;
import com.zentu.zentu_core.enums.State;
import com.zentu.zentu_core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupMembershipRepository userGroupMembershipRepository;
    private final UserService userService;

    @Transactional
    public GroupDto createGroup(CreateGroupRequest request, User user) {
        Group group = Group.builder().name(request.getName()).description(request.getDescription()).build();
        group = groupRepository.save(group);

        group.getAdmins().add(user);
        groupRepository.save(group);

        UserGroupMembership membership = UserGroupMembership.builder().user(user).group(group).build();
        userGroupMembershipRepository.save(membership);

        return convertToGroupDto(group);
    }

    public boolean isUserGroupAdmin(Group group, User user) {
        return group.getAdmins().stream().anyMatch(admin -> admin.getId().equals(user.getId()));
    }

    @Transactional
    public GroupDto updateGroup(UUID groupId, UpdateGroupRequest request, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)) {
            throw new RuntimeException("Unauthorized to perform this action");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group = groupRepository.save(group);

        return convertToGroupDto(group);
    }

    @Transactional
    public void deleteGroup(UUID groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)) {
            throw new RuntimeException("Unauthorized to perform this action");
        }

        group.setState(State.INACTIVE);
        groupRepository.save(group);
    }

    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(this::convertToGroupDto)
                .collect(Collectors.toList());
    }

    public GroupDto getGroupById(UUID groupId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(()-> new RuntimeException("Group not found"));
        return convertToGroupDto(group);
    }

    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userGroupMembershipRepository.findByUserAndGroup(user, group).isPresent()){
            throw new RuntimeException("User is already in the group");
        }

        UserGroupMembership membership = UserGroupMembership.builder().user(user).group(group).build();

        userGroupMembershipRepository.save(membership);
    }

    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroupMembership membership = userGroupMembershipRepository.findByUserAndGroup(user, group)
                .orElseThrow(()-> new RuntimeException("User not in the group"));

        membership.setState(State.INACTIVE);

        userGroupMembershipRepository.save(membership);

    }

    @Transactional
    public void setUserAsGroupAdmin(UUID groupId, UUID userId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)){
            throw new RuntimeException("Unauthorized to perform this operation");
        }

        User newAdmin = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!userGroupMembershipRepository.existsByUserAndGroup(newAdmin, group)){
            throw new RuntimeException("User is not a member of the group");
        }

        group.getAdmins().add(newAdmin);

        groupRepository.save(group);
    }

    @Transactional
    public void updateUserGroupRole(UUID groupId, UUID userId, String roleName) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroupMembership membership = userGroupMembershipRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new RuntimeException("User is not a member of the group"));

        GroupRole role;
        try {
            role = GroupRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name");
        }

        membership.setRole(role);
        userGroupMembershipRepository.save(membership);

    }

    public List<UserSummaryDto> getGroupAdmins(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Set<User> admins = group.getAdmins();

        return admins.stream()
                .map(userService::convertToUserSummaryDto).toList();
    }

    public List<GroupMemberDto> getGroupMembers(Group group){
        List<UserGroupMembership> memberships = userGroupMembershipRepository.findAllByGroup(group);
        return memberships.stream()
                .map(membership -> {
                    User user = membership.getUser();
                    Boolean isAdmin = isUserGroupAdmin(group, user);
                    return GroupMemberDto.builder()
                            .userId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .isAdmin(isAdmin)
                            .role(membership.getState().toString())
                            .joinedAt(membership.getCreatedAt())
                            .build();
                })
                .toList();
    }


    private GroupDto convertToGroupDto(Group group) {
        List<GroupMemberDto> members = getGroupMembers(group);
        return GroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .members(members)
                .build();
    }

}
