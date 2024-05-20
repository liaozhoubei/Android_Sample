package com.example.studyservice.binderpool;

interface ISecurityCenter {
    String encrypt(String content);
    String decrypt(String password);
}