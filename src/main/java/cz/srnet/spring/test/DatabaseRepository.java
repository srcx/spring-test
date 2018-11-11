package cz.srnet.spring.test;

import org.springframework.data.jpa.repository.JpaRepository;

interface DatabaseRepository extends JpaRepository<Database, String> {

}
