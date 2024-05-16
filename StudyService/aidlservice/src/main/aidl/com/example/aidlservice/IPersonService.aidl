// IPersonService.aidl
package com.example.aidlservice;

import com.example.aidlservice.Person;

// Declare any non-default types here with import statements

interface IPersonService {
    int add(int num1, int num2);
    List<Person> addPerson(in Person p);
}
