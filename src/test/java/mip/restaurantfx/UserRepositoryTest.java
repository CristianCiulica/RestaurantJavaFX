package mip.restaurantfx;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void getAllStaff_returnsOnlyStaff() {
        UserRepository repo = new UserRepository();

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        repo.save(new User("admin_" + suffix, "pw", "Admin Test", User.Role.ADMIN));
        repo.save(new User("staff1_" + suffix, "pw", "Ospatar 1", User.Role.STAFF));
        repo.save(new User("staff2_" + suffix, "pw", "Ospatar 2", User.Role.STAFF));

        var staff = repo.getAllStaff();

        assertTrue(staff.stream().allMatch(u -> u.getRol() == User.Role.STAFF), "Lista trebuie sa contina doar STAFF");
        assertTrue(staff.stream().anyMatch(u -> u.getUsername().equals("staff1_" + suffix)));
        assertTrue(staff.stream().anyMatch(u -> u.getUsername().equals("staff2_" + suffix)));
        assertFalse(staff.stream().anyMatch(u -> u.getUsername().equals("admin_" + suffix)));
    }
}

