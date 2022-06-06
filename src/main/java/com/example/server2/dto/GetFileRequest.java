package com.example.server2.dto;

public class GetFileRequest implements BasicRequest{
        @Override
        public String getType() {
            return "getFileList";
        }
}
