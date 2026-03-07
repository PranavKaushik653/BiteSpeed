package com.example.BiteSpeed.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdentifyResponse {
    private ContactResponse contact;

    @Data
    @Builder
    public static class ContactResponse {

        private Long primaryContatctId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;

    }
}
