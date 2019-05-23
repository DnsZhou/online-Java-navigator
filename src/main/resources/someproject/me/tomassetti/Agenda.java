package me.tomassetti;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Agenda {
    public class testInternalClass{
        public int tesInterInt;
    }

    private List<Person> persons = new ArrayList<>();

    public void addPerson(Person person) {
        persons.add(person);
    }

    public List<Address> findAddressesOfPersons(String personName) {
        return persons.stream().
                filter(p -> p.getName().equals(personName)).
                map(p -> p.getAddress()).
                collect(Collectors.toList());
    }

}

final class PrivateClassTest{
    private int test1;
}
