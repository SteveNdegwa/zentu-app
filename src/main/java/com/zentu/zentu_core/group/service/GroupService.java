package com.zentu.zentu_core.group.service;

import com.zentu.zentu_core.group.dto.CreateGroupRequest;
import com.zentu.zentu_core.group.dto.GroupDto;
import com.zentu.zentu_core.group.dto.UpdateGroupRequest;
import com.zentu.zentu_core.group.dto.GroupMemberDto;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.entity.GroupMembership;
import com.zentu.zentu_core.group.repository.GroupRepository;
import com.zentu.zentu_core.group.repository.GroupMembershipRepository;
import com.zentu.zentu_core.user.dto.UserSummaryDto;
import com.zentu.zentu_core.group.enums.GroupRole;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMembershipRepository groupMembershipRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    private String getNextAlias(String currentMax) {
        if (currentMax == null) {
            return "0000";
        }
        int nextNumber = Integer.parseInt(currentMax) + 1;
        return String.format("%04d", nextNumber);
    }

    @Transactional
    public UUID createGroup(CreateGroupRequest request, User user) {
        Group group = groupRepository.save(
                Group.builder().name(request.getName()).description(request.getDescription()).build());
        String maxAlias = groupRepository.findMaxAlias();
        group.setAlias = getNextAlias(maxAlias);
        group.getAdmins().add(user);
        groupRepository.save(group);

        GroupMembership membership = GroupMembership.builder().user(user).group(group).build();
        groupMembershipRepository.save(membership);

        return group.getId();
    }

    public boolean isUserGroupAdmin(Group group, User user) {
        return group.getAdmins().stream().anyMatch(admin -> admin.getId().equals(user.getId()));
    }

    @Transactional
    public void updateGroup(UUID groupId, UpdateGroupRequest request, User user) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)) {
            throw new RuntimeException("Unauthorized to perform this action");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(UUID groupId, User user) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)) {
            throw new RuntimeException("Unauthorized to perform this action");
        }

        group.setState(State.INACTIVE);
        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.findAllByState(State.ACTIVE);
        return groups.stream().map(this::convertToGroupDto).toList();
    }

    @Transactional(readOnly = true)
    public GroupDto getGroupById(UUID groupId){
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("Group not found"));
        return convertToGroupDto(group);
    }

    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groupMembershipRepository.findByUserAndGroupAndState(user, group, State.ACTIVE).isPresent()){
            throw new RuntimeException("User is already in the group");
        }

        GroupMembership membership = GroupMembership.builder().user(user).group(group).build();

        groupMembershipRepository.save(membership);
    }

    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMembership membership = groupMembershipRepository.findByUserAndGroupAndState(user, group, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("User not in the group"));

        membership.setState(State.INACTIVE);

        groupMembershipRepository.save(membership);

    }

    @Transactional
    public void setUserAsGroupAdmin(UUID groupId, UUID userId, User user) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!isUserGroupAdmin(group, user)){
            throw new RuntimeException("Unauthorized to perform this operation");
        }

        User newAdmin = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!groupMembershipRepository.existsByUserAndGroupAndState(newAdmin, group, State.ACTIVE)){
            throw new RuntimeException("User is not a member of the group");
        }

        group.getAdmins().add(newAdmin);

        groupRepository.save(group);
    }

    @Transactional
    public void updateUserGroupRole(UUID groupId, UUID userId, String roleName) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByIdAndState(userId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMembership membership = groupMembershipRepository.findByUserAndGroupAndState(user, group, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User is not a member of the group"));

        GroupRole role;
        try {
            role = GroupRole.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name");
        }

        membership.setRole(role);
        groupMembershipRepository.save(membership);

    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getGroupAdmins(UUID groupId) {
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Set<User> admins = group.getAdmins();

        return admins.stream()
                .map(admin -> UserSummaryDto.builder()
                        .id(admin.getId())
                        .firstName(admin.getFirstName())
                        .lastName(admin.getLastName())
                        .otherName(admin.getOtherName())
                        .build()).toList();
    }

    @Transactional(readOnly = true)
    private List<GroupMemberDto> getGroupMembers(Group group){
        List<GroupMembership> memberships = groupMembershipRepository.findAllByGroupAndState(group, State.ACTIVE);
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
