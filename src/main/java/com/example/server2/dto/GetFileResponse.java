package com.example.server2.dto;

import java.io.File;
import java.util.List;

public class GetFileResponse extends BasicResponse{
        private final List<File> itemsList;

        public List<File> getItemsList() {
            return itemsList;
        }

        public GetFileResponse(String response, List<File> itemsList) {
            super(response);
            this.itemsList = itemsList;
        }
    }


