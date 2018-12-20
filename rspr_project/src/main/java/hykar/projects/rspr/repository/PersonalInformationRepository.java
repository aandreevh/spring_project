package hykar.projects.rspr.repository;

import hykar.projects.rspr.entity.PersonalInformation;
import org.springframework.data.repository.CrudRepository;

public interface PersonalInformationRepository extends CrudRepository<PersonalInformation, Long> {
}
