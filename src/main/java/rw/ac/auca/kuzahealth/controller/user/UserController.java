package rw.ac.auca.kuzahealth.controller.user;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.controller.user.dto.UpdateUserRequest;
import rw.ac.auca.kuzahealth.controller.user.dto.UserResponse;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.core.user.service.UserService;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/users", "/api/v1/users" })
@RequiredArgsConstructor
public class UserController {

    private static final String ADMIN_OR_SELF = "hasRole('ADMIN') or #id == principal.id";

    private final UserService userService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(UserResponse::from).toList());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> search(@RequestParam(required = false) String q,
            @RequestParam(required = false) EUserType role,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(userService.search(q, role, PageRequests.of(page, size, sort)), UserResponse::from);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMIN_OR_SELF)
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(UserResponse.from(userService.getUserById(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(ADMIN_OR_SELF)
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest user,
            @AuthenticationPrincipal CustomUserDetails caller) {
        boolean byAdmin = caller.hasRole(EUserType.ADMIN);
        return ResponseEntity.ok(UserResponse.from(userService.updateUser(id, user, byAdmin)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
