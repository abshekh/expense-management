package com.example.geektrust;

import com.example.geektrust.service.MemberService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.example.geektrust.constants.Constants.MAX_SIZE;

public class Main {
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("FAILURE");
            return;
        }

        MemberService memberService = new MemberService(MAX_SIZE);

        try (Stream<String> commandStream = Files.lines(Paths.get(args[0]))) {
            commandStream.forEach(memberService::processCommands);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
