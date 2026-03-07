package com.example.BiteSpeed.service;

import com.example.BiteSpeed.dto.IdentifyRequest;
import com.example.BiteSpeed.dto.IdentifyResponse;
import com.example.BiteSpeed.entity.Contact;
import com.example.BiteSpeed.entity.LinkPrecedence;
import com.example.BiteSpeed.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public IdentifyResponse identify (IdentifyRequest request) {
        String email =  request.getEmail();
        String phone = request.getPhoneNumber();

        List<Contact> contacts=contactRepository.findByEmailOrPhoneNumber(email,phone);

        //no contacts

        if(contacts.isEmpty()){
            Contact newContact = new Contact();
            newContact.setEmail(email);
            newContact.setPhoneNumber(phone);
            newContact.setLinkPrecedence(LinkPrecedence.primary);
            Contact saved = contactRepository.save(newContact);
            return buildResponse(List.of(newContact));
        }

        //finding primary contact
        Contact primary=contacts.stream()
                .filter(c->c.getLinkPrecedence()==LinkPrecedence.primary)
                .min(Comparator.comparing (Contact ::getCreatedAt))
                .orElse(contacts.get(0));

        //checking if any info is there email/phone
        boolean emailExists = contacts.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), email));

        boolean phoneExists = contacts.stream()
                .anyMatch(c -> Objects.equals(c.getPhoneNumber(), phone));

        if (!emailExists || !phoneExists) {

            Contact secondary = new Contact();
            secondary.setEmail(email);
            secondary.setPhoneNumber(phone);
            secondary.setLinkedId(primary.getId());
            secondary.setLinkPrecedence(LinkPrecedence.secondary);

            contactRepository.save(secondary);

            contacts.add(secondary);
        }

        return buildResponse(contacts);
}
    private IdentifyResponse buildResponse(List<Contact> contacts) {

        Contact primary = contacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.primary)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseThrow();

        List<String> emails = contacts.stream()
                .map(Contact::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> phones = contacts.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<Long> secondaryIds = contacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.secondary)
                .map(Contact::getId)
                .collect(Collectors.toList());

        return IdentifyResponse.builder()
                .contact(
                        IdentifyResponse.ContactResponse.builder()
                                .primaryContatctId(primary.getId())
                                .emails(emails)
                                .phoneNumbers(phones)
                                .secondaryContactIds(secondaryIds)
                                .build()
                )
                .build();
    }

}
