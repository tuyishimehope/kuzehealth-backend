package rw.ac.auca.kuzahealth.controller.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class ParentSeeder implements CommandLineRunner {

    @Autowired
    private ParentRepository parentRepository;

    private final List<String> rwandanFirstNames = List.of(
            "Aline", "Eric", "Jean", "Sandrine", "Claudine", "Patrick", "Innocent", "Divine", "Mugisha", "Mukamana",
            "Alice", "Samuel", "Esther", "Alain", "Josiane", "Emmanuel", "Clarisse", "Jeanette", "Yves", "Chantal"
    );

    private final List<String> rwandanLastNames = List.of(
            "Uwimana", "Nshimiyimana", "Irakoze", "Munyaneza", "Uwamahoro", "Mutoni", "Habimana", "Niyonsaba", "Twagirimana", "Umutoni"
    );

    private final List<String> districts = List.of("Kigali", "Rubavu", "Musanze", "Huye", "Muhanga", "Rwamagana");
    private final List<String> sectors = List.of("Gasabo", "Kacyiru", "Remera", "Nyamirambo", "Gisozi");
    private final List<String> cells = List.of("Kigabiro", "Nyakabanda", "Rugando", "Kimironko");
    private final List<String> villages = List.of("Umudugudu A", "Umudugudu B", "Umudugudu C");

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (parentRepository.count() > 0) return;

        for (int i = 0; i < 100; i++) {
            Parent parent = new Parent();

            String firstName = getRandom(rwandanFirstNames);
            String lastName = getRandom(rwandanLastNames);

            parent.setFirstName(firstName);
            parent.setLastName(lastName);
            parent.setEmail((firstName + "." + lastName + i + "@gmail.com").toLowerCase());
            parent.setPhone("+25078" + (1000000 + random.nextInt(8999999)));
            parent.setBloodGroup(getRandom(List.of("O+", "A+", "B+", "AB+")));
            parent.setMaritalStatus(getRandom(List.of("Single", "Married", "Widowed")));
            parent.setExpectedDeliveryDate(java.sql.Date.valueOf(LocalDate.now().plusDays(random.nextInt(100) + 30)));
            parent.setEmergencyContactFullName("Contact " + lastName);
            parent.setEmergencyContactNumber("+25072" + (1000000 + random.nextInt(8999999)));
            parent.setEmergencyContactRelationship(getRandom(List.of("Husband", "Wife", "Sister", "Brother")));

            parent.setDistrict(getRandom(districts));
            parent.setSector(getRandom(sectors));
            parent.setCell(getRandom(cells));
            parent.setVillage(getRandom(villages));

            parentRepository.save(parent);
        }

        System.out.println("Seeded 100 parents.");
    }

    private <T> T getRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
